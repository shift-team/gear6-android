package com.shift.gear6.tasks.obd2

import android.os.AsyncTask
import br.ufrn.imd.obd.commands.ObdCommandGroup
import br.ufrn.imd.obd.commands.protocol.EchoOffCommand
import br.ufrn.imd.obd.commands.protocol.LineFeedOffCommand
import br.ufrn.imd.obd.commands.protocol.SelectProtocolCommand
import br.ufrn.imd.obd.commands.protocol.TimeoutCommand
import br.ufrn.imd.obd.enums.ObdProtocols
import com.shift.gear6.OnTaskCompleted
import com.shift.gear6.adapters.*

class ConnectTask : AsyncTask<ConnectTask.Params, Void, IAdapter?>() {
    class Params {
        var callback: OnTaskCompleted<IAdapter?>? = null
    }

    private var callback: OnTaskCompleted<IAdapter?>? = null

    override fun doInBackground(vararg params: ConnectTask.Params): IAdapter? {
        callback = params[0].callback

        val wifiAdapter = getWifiAdapter()
        if (wifiAdapter != null) {
            return if (prepareAdapter(wifiAdapter))
                wifiAdapter
            else
                null
        }

        val blueToothAdapter = getBlueToothAdapter()
        if (blueToothAdapter != null) {
            return if (prepareAdapter(blueToothAdapter))
                blueToothAdapter
            else
                null
        }

        return null
    }

    override fun onPostExecute(result: IAdapter?) {
        if (callback != null) {
            callback?.onTaskCompleted(result)
        }
    }

    private fun getWifiAdapter(): IAdapter? {
        return try {
            WiFiAdapter()
        } catch (ex: Exception) {
            null
        }
    }

    private fun getBlueToothAdapter(): IAdapter? {
        return try {
            BlueToothAdapter()
        } catch (ex: Exception) {
            null
        } catch (ex: NotImplementedError) {
            null
        }
    }


    private fun prepareAdapter(adapter: IAdapter): Boolean {
        return try {
            val commands = ObdCommandGroup()

            commands.add(br.ufrn.imd.obd.commands.protocol.ObdResetCommand())
            commands.add(EchoOffCommand())
            commands.add(LineFeedOffCommand())
            commands.add(TimeoutCommand(500))
            commands.add(SelectProtocolCommand(ObdProtocols.AUTO))

            commands.run(adapter.getInputStream(), adapter.getOutputStream())

            true
        } catch (ex: Exception) {
            false
        }
    }
}
