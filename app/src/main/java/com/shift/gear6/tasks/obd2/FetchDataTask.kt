package com.shift.gear6.tasks.obd2

import android.os.AsyncTask
import br.ufrn.imd.obd.commands.ObdCommand
import br.ufrn.imd.obd.commands.ObdCommandGroup
import br.ufrn.imd.obd.commands.engine.LoadCommand
import br.ufrn.imd.obd.commands.engine.RPMCommand
import com.shift.gear6.CarDataSnapshot
import com.shift.gear6.OnTaskCompleted
import com.shift.gear6.adapters.IAdapter

class FetchDataTask : AsyncTask<FetchDataTask.Params, Void, CarDataSnapshot>() {
    class Params {
        var adapter: IAdapter? = null
        var callback: OnTaskCompleted<CarDataSnapshot>? = null

        var getRPM = false
        var getEngineLoad = false
    }

    private var callback: OnTaskCompleted<CarDataSnapshot>? = null

    override fun doInBackground(vararg params: FetchDataTask.Params): CarDataSnapshot {
        callback = params[0].callback

        val commandList = buildCommandList(params[0])

        executeCommands(commandList, params[0].adapter!!)

        return buildSnapshot(params[0], commandList)
    }

    override fun onPostExecute(result: CarDataSnapshot) {
        if (callback != null) {
            callback?.onTaskCompleted(result)
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

        return commandList
    }

    private fun executeCommands(commandList: HashMap<String, ObdCommand>, adapter: IAdapter) {
        val commands = ObdCommandGroup()

        for (cmd in commandList.values) {
            commands.add(cmd)
        }

        commands.run(adapter.getInputStream(), adapter.getOutputStream())
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

        return snapshot
    }
}