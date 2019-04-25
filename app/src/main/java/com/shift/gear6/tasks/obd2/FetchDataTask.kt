package com.shift.gear6.tasks.obd2

import android.os.AsyncTask
import br.ufrn.imd.obd.commands.ObdCommand
import br.ufrn.imd.obd.commands.ObdCommandGroup
import br.ufrn.imd.obd.commands.engine.*
import br.ufrn.imd.obd.commands.fuel.*
import br.ufrn.imd.obd.commands.pressure.BarometricPressureCommand
import br.ufrn.imd.obd.commands.pressure.FuelPressureCommand
import br.ufrn.imd.obd.commands.pressure.FuelRailPressureCommand
import br.ufrn.imd.obd.commands.pressure.IntakeManifoldPressureCommand
import br.ufrn.imd.obd.commands.temperature.AirIntakeTemperatureCommand
import br.ufrn.imd.obd.commands.temperature.AmbientAirTemperatureCommand
import br.ufrn.imd.obd.commands.temperature.EngineCoolantTemperatureCommand
import com.shift.gear6.CarDataSnapshot
import com.shift.gear6.CommandNames
import com.shift.gear6.Global
import com.shift.gear6.adapters.IAdapter
import java.io.Serializable

class FetchDataTask : AsyncTask<FetchDataTask.Params, Unit, FetchDataTask.Result>() {
    class Params : Serializable {
        var adapter: IAdapter? = null
        var callback: ((Result) -> Unit)? = null

        var dataToGet = HashMap<String, Boolean>()
    }

    class Result {
        var data = CarDataSnapshot()
        var success = true
        var error = ""
    }

    private var mCallback: ((Result) -> Unit)? = null
    private var mError: String? = null

    private val commandMap = createCommandMap()

    private fun createCommandMap(): Map<String, (() -> (ObdCommand))> {
        val map = HashMap<String, (() -> (ObdCommand))>()

        map[CommandNames.WidebandAirFuelRatio] = { WidebandAirFuelRatioCommand() }
        map[CommandNames.ThrottlePosition] = { ThrottlePositionCommand() }
        map[CommandNames.Speed] = { SpeedCommand() }
        map[CommandNames.RelativeThrottlePosition] = { RelativeThrottlePositionCommand() }
        map[CommandNames.OilTemperature] = { OilTempCommand() }

        map[CommandNames.MassAirFlow] = { MassAirFlowCommand() }
        map[CommandNames.Load] = { LoadCommand() }
        map[CommandNames.IntakeManifoldPressure] = { IntakeManifoldPressureCommand() }
        map[CommandNames.FuelTrim] = { FuelTrimCommand() }
        map[CommandNames.FuelRailPressure] = { FuelRailPressureCommand() }

        map[CommandNames.FuelPressure] = { FuelPressureCommand() }
        map[CommandNames.FuelLevel] = { FuelLevelCommand() }
        map[CommandNames.EngineCoolantTemperature] = { EngineCoolantTemperatureCommand() }
        map[CommandNames.ConsumptionRate] = { ConsumptionRateCommand() }
        map[CommandNames.BarometricPressure] = { BarometricPressureCommand() }

        map[CommandNames.AmbientAirTemperature] = { AmbientAirTemperatureCommand() }
        map[CommandNames.AirIntakeTemperature] = { AirIntakeTemperatureCommand() }
        map[CommandNames.AirFuelRatio] = { AirFuelRatioCommand() }
        map[CommandNames.AbsoluteLoad] = { AbsoluteLoadCommand() }
        map[CommandNames.RPM] = { RPMCommand() }

        return map
    }

    override fun doInBackground(vararg params: FetchDataTask.Params): Result {
        var result = Result()

        mCallback = params[0].callback

        val commandList = buildCommandList(params[0])

        if (executeCommands(commandList, params[0].adapter!!)) {
            result.data = buildSnapshot(commandList)
        } else {
            result.error = mError.orEmpty()
            result.success = false
        }

        return result
    }

    override fun onPostExecute(result: Result) {
        if (mCallback != null) {
            mCallback?.invoke(result)
        }
    }

    private fun buildCommandList(params: FetchDataTask.Params): HashMap<String, ObdCommand> {
        val commandList = HashMap<String, ObdCommand>()

        for (kvPair in params.dataToGet) {
            if (kvPair.value) {
                commandList[kvPair.key] = commandMap[kvPair.key]!!.invoke()
            }
        }

        return commandList
    }

    private fun executeCommands(commandList: HashMap<String, ObdCommand>, adapter: IAdapter): Boolean {
        val commands = ObdCommandGroup()

        for (cmd in commandList.values) {
            commands.add(cmd)
        }

        try {
            commands.run(adapter.getInputStream(), adapter.getOutputStream())

        } catch (ex: Throwable) {
            mError = ex.message
            return false
        }

        for (cmd in commandList) {
            Global.logMessage(cmd.key + "took " + cmd.value.elapsedTime.toString() + " ms") // I guess its in milliseconds
        }

        return true
    }

    private fun buildSnapshot(
        commandList: HashMap<String, ObdCommand>
    ): CarDataSnapshot {
        val snapshot = CarDataSnapshot()

        for (c in commandList) {
            snapshot.data[c.key] = c.value.calculatedResult
        }

        return snapshot
    }
}