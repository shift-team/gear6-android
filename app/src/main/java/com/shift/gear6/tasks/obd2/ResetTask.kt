package com.shift.gear6.tasks.obd2

import android.os.AsyncTask
import br.ufrn.imd.obd.commands.ObdCommandGroup
import br.ufrn.imd.obd.commands.protocol.EchoOffCommand
import br.ufrn.imd.obd.commands.protocol.LineFeedOffCommand
import br.ufrn.imd.obd.commands.protocol.SelectProtocolCommand
import br.ufrn.imd.obd.commands.protocol.TimeoutCommand
import br.ufrn.imd.obd.enums.ObdProtocols
import com.shift.gear6.OnTaskCompleted
import com.shift.gear6.adapters.IAdapter

class ResetTask : AsyncTask<ResetTask.Params, Void, Boolean>() {
    class Params {
        var adapter: IAdapter? = null
        var callback: OnTaskCompleted<Boolean>? = null
    }

    private var callback: OnTaskCompleted<Boolean>? = null

    override fun doInBackground(vararg params: ResetTask.Params): Boolean {
        callback = params[0].callback

        return try {
            val commands = ObdCommandGroup()

            commands.add(br.ufrn.imd.obd.commands.protocol.ObdResetCommand())
            commands.add(EchoOffCommand())
            commands.add(LineFeedOffCommand())
            commands.add(TimeoutCommand(500))
            commands.add(SelectProtocolCommand(ObdProtocols.AUTO))

            commands.run(params[0].adapter?.getInputStream(), params[0].adapter?.getOutputStream())

            true
        } catch (ex: Exception) {
            false
        }
    }

    override fun onPostExecute(result: Boolean) {
        if (callback != null) {
            callback?.onTaskCompleted(result)
        }
    }

}