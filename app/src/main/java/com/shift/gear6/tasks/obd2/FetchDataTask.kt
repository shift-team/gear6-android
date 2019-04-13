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
import com.shift.gear6.CommandBinding
import com.shift.gear6.CommandNames
import com.shift.gear6.adapters.IAdapter
import java.io.Serializable

class FetchDataTask : AsyncTask<FetchDataTask.Params, Void, CarDataSnapshot>() {
    class Params : Serializable {
        var adapter: IAdapter? = null
        var callback: ((CarDataSnapshot) -> Unit)? = null

        var app: App? = null

        var dataToGet = HashMap<String, Boolean>()
    }

    private var mCallback: ((CarDataSnapshot) -> Unit)? = null
    private var mError: String? = null

    override fun doInBackground(vararg params: FetchDataTask.Params): CarDataSnapshot? {
        mCallback = params[0].callback

        val commandList = buildCommandList(params[0])

        if (executeCommands(commandList, params[0].adapter!!)) {
            return buildSnapshot(commandList)
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

        for (kvPair in params.dataToGet) {
            if (kvPair.value) {
                commandList[kvPair.key] = CommandBinding.bindings[kvPair.key]!!.createCommand!!.invoke()
            }
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
        commandList: HashMap<String, ObdCommand>
    ): CarDataSnapshot {
        val snapshot = CarDataSnapshot()

        for (c in commandList) {
            snapshot.data[c.key] = c.value.formattedResult + " " + c.value.resultUnit
        }

        return snapshot
    }
}