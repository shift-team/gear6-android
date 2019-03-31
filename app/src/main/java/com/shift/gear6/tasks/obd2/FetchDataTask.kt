package com.shift.gear6.tasks.obd2

import android.os.AsyncTask
import br.ufrn.imd.obd.commands.ObdCommand
import br.ufrn.imd.obd.commands.ObdCommandGroup
import br.ufrn.imd.obd.commands.engine.*
import br.ufrn.imd.obd.commands.fuel.AirFuelRatioCommand
import br.ufrn.imd.obd.commands.fuel.ConsumptionRateCommand
import br.ufrn.imd.obd.commands.fuel.FuelLevelCommand
import br.ufrn.imd.obd.commands.pressure.BarometricPressureCommand
import br.ufrn.imd.obd.commands.pressure.FuelPressureCommand
import br.ufrn.imd.obd.commands.pressure.IntakeManifoldPressureCommand
import br.ufrn.imd.obd.commands.pressure.PressureCommand
import br.ufrn.imd.obd.commands.temperature.AirIntakeTemperatureCommand
import br.ufrn.imd.obd.commands.temperature.AmbientAirTemperatureCommand
import br.ufrn.imd.obd.commands.temperature.EngineCoolantTemperatureCommand
import com.shift.gear6.App
import com.shift.gear6.CarDataSnapshot
import com.shift.gear6.adapters.IAdapter

class FetchDataTask : AsyncTask<FetchDataTask.Params, Void, CarDataSnapshot>() {
    class Params {
        var adapter: IAdapter? = null
        var callback: ((CarDataSnapshot) -> Unit)? = null

        var app: App? = null

        var getRPM = true
        var getEngineLoad = true
        var getBarometricPressure = true
        var getAirFuelRatio = true
        var getMAF = true
        var getOilTemp = true
        var getOilPressure = true
        var getEngineRuntime = true
        var getSpeed = true
        var getFuelConsumption = true
        var getFuelLevel = true
        var getFuelPressure = true
        var getIntakeManifoldPressure = true
        var getAirIntakeTemp = true
        var getAmbientAirTemp = true
        var getEngineCoolantTemp = true
    }

    private var mCallback: ((CarDataSnapshot) -> Unit)? = null
    private var mError: String? = null

    override fun doInBackground(vararg params: FetchDataTask.Params): CarDataSnapshot? {
        mCallback = params[0].callback

        val commandList = buildCommandList(params[0])

        if (executeCommands(commandList, params[0].adapter!!)) {
            return buildSnapshot(params[0], commandList)
        }

        if (params[0].app != null && mError != null) {
            params[0].app!!.log.add(mError!!)
        }

        return null
    }

    override fun onPostExecute(result: CarDataSnapshot) {
        if (mCallback != null) {
            mCallback?.invoke(result)
        }
    }

    private fun buildCommandList(params: FetchDataTask.Params): HashMap<String, ObdCommand> {
        val commandList = HashMap<String, ObdCommand>()

        if (params.getRPM) {
            commandList["rpm"] = RPMCommand()
        }

        if (params.getEngineLoad) {
            commandList["engineLoad"] = LoadCommand()
        }

        if (params.getBarometricPressure) {
            commandList["barometricPressure"] = BarometricPressureCommand()
        }

        if (params.getAirFuelRatio) {
            commandList["airFuelRatio"] = AirFuelRatioCommand()
        }

        if (params.getMAF) {
            commandList["MAF"] = br.ufrn.imd.obd.commands.engine.MassAirFlowCommand()
        }

        if (params.getOilTemp) {
            commandList["oilTemp"] = br.ufrn.imd.obd.commands.engine.OilTempCommand()
        }

        if (params.getEngineRuntime) {
            commandList["engineRuntime"] = br.ufrn.imd.obd.commands.engine.RuntimeCommand()
        }

        if (params.getSpeed) {
            commandList["speed"] = br.ufrn.imd.obd.commands.engine.SpeedCommand()
        }

        if (params.getFuelConsumption) {
            commandList["fuelConsumption"] = br.ufrn.imd.obd.commands.fuel.ConsumptionRateCommand()
        }

        if (params.getFuelLevel) {
            commandList["fuelLevel"] = br.ufrn.imd.obd.commands.fuel.FuelLevelCommand()
        }

        if (params.getFuelPressure) {
            commandList["fuelPressure"] = br.ufrn.imd.obd.commands.pressure.FuelPressureCommand()
        }

        if (params.getIntakeManifoldPressure) {
            commandList["intakeManifoldPressure"] = br.ufrn.imd.obd.commands.pressure.IntakeManifoldPressureCommand()
        }

        if (params.getAirIntakeTemp) {
            commandList["airIntakeTemp"] = br.ufrn.imd.obd.commands.temperature.AirIntakeTemperatureCommand()
        }

        if (params.getAmbientAirTemp) {
            commandList["ambientAirTemp"] = br.ufrn.imd.obd.commands.temperature.AmbientAirTemperatureCommand()
        }

        if (params.getEngineCoolantTemp) {
            commandList["engineCoolantTemp"] = br.ufrn.imd.obd.commands.temperature.EngineCoolantTemperatureCommand()
        }

        return commandList
    }

    private fun executeCommands(commandList: HashMap<String, ObdCommand>, adapter: IAdapter): Boolean {
        val commands = ObdCommandGroup()

        for (cmd in commandList.values) {
            commands.add(cmd)
        }

        return try {
            commands.run(adapter.getInputStream(), adapter.getOutputStream())

            true
        } catch (ex: Exception) {
            mError = ex.message

            false
        }
    }

    private fun buildSnapshot(
        params: FetchDataTask.Params,
        commandList: HashMap<String, ObdCommand>
    ): CarDataSnapshot {
        val snapshot = CarDataSnapshot()

        if (params.getRPM) {
            snapshot.rpm = (commandList["rpm"] as RPMCommand).rpm
        }

        if (params.getEngineLoad) {
            snapshot.engineLoad = (commandList["engineLoad"] as LoadCommand).percentage
        }

        if (params.getBarometricPressure) {
            snapshot.barometricPressure = (commandList["barometricPressure"] as BarometricPressureCommand).imperialUnit
        }

        if (params.getAirFuelRatio) {
            snapshot.airFuelRatio = (commandList["airFuelRatio"] as AirFuelRatioCommand).airFuelRatio
        }

        if (params.getMAF) {
            snapshot.MAF = (commandList["MAF"] as MassAirFlowCommand).maf
        }

        if (params.getOilTemp) {
            snapshot.oilTemp = (commandList["oilTemp"] as OilTempCommand).imperialUnit
        }

        if (params.getEngineRuntime) {
            snapshot.engineRuntime = (commandList["engineRuntime"] as RuntimeCommand).elapsedTime
        }

        if (params.getSpeed) {
            snapshot.speed = (commandList["speed"] as SpeedCommand).imperialSpeed
        }

        if (params.getFuelConsumption) {
            snapshot.fuelConsumption = (commandList["fuelConsumption"] as ConsumptionRateCommand).litersPerHour
        }

        if (params.getFuelLevel) {
            snapshot.fuelLevel = (commandList["fuelLevel"] as FuelLevelCommand).fuelLevel
        }

        if (params.getFuelPressure) {
            snapshot.fuelPressure = (commandList["fuelPressure"] as FuelPressureCommand).imperialUnit
        }

        if (params.getIntakeManifoldPressure) {
            snapshot.intakeManifoldPressure = (commandList["intakeManifoldPressure"] as IntakeManifoldPressureCommand).imperialUnit
        }

        if (params.getAirIntakeTemp) {
            snapshot.airIntakeTemp = (commandList["airIntakeTemp"] as AirIntakeTemperatureCommand).imperialUnit
        }

        if (params.getAmbientAirTemp) {
            snapshot.ambientAirTemp = (commandList["ambientAirTemp"] as AmbientAirTemperatureCommand).imperialUnit
        }

        if (params.getEngineCoolantTemp) {
            snapshot.engineCoolantTemp = (commandList["engineCoolantTemp"] as EngineCoolantTemperatureCommand).imperialUnit
        }

        return snapshot
    }
}