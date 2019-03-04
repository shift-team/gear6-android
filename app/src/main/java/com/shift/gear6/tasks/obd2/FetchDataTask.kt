package com.shift.gear6.tasks.obd2

import android.os.AsyncTask
import br.ufrn.imd.obd.commands.ObdCommand
import br.ufrn.imd.obd.commands.ObdCommandGroup
import br.ufrn.imd.obd.commands.engine.LoadCommand
import br.ufrn.imd.obd.commands.engine.RPMCommand
import com.shift.gear6.App
import com.shift.gear6.CarDataSnapshot
import com.shift.gear6.adapters.IAdapter

class FetchDataTask : AsyncTask<FetchDataTask.Params, Void, CarDataSnapshot>() {
    class Params {
        var adapter: IAdapter? = null
        var callback: ((CarDataSnapshot) -> Unit)? = null

        var app: App? = null

        var getRPM = false
        var getEngineLoad = false
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

        return snapshot
    }
}