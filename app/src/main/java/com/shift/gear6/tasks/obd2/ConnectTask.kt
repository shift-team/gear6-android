package com.shift.gear6.tasks.obd2

import android.os.AsyncTask
import br.ufrn.imd.obd.commands.ObdCommandGroup
import br.ufrn.imd.obd.commands.protocol.EchoOffCommand
import br.ufrn.imd.obd.commands.protocol.LineFeedOffCommand
import br.ufrn.imd.obd.commands.protocol.SelectProtocolCommand
import br.ufrn.imd.obd.commands.protocol.TimeoutCommand
import br.ufrn.imd.obd.enums.ObdProtocols
import com.shift.gear6.App
import com.shift.gear6.adapters.*
import java.util.*

class ConnectTask : AsyncTask<ConnectTask.Params, Void, IAdapter?>() {
    class Params {
        var callback: ((IAdapter?) -> Unit)? = null
        var app: App? = null
    }

    override fun doInBackground(vararg params: ConnectTask.Params): IAdapter? {
        mCallback = params[0].callback

        val wifiAdapter = getWifiAdapter()
        if (wifiAdapter != null) {
            if (prepareAdapter(wifiAdapter)) {
                return wifiAdapter
            } else {
                if (params[0].app != null) {
                    params[0].app!!.log.add(Date().toString() + ": Created WiFi adapter, but failed to initialize")
                }
                return null
            }
        }

        val blueToothAdapter = getBlueToothAdapter()
        if (blueToothAdapter != null) {
            if (prepareAdapter(blueToothAdapter)) {
                return blueToothAdapter
            } else {
                if (params[0].app != null) {
                    params[0].app!!.log.add(Date().toString() + ": Created BT adapter, but failed to initialize")
                }
                return null
            }
        }

        if (params[0].app != null) {
            params[0].app!!.log.add(Date().toString() + ": No adapter was found")
        }
        return null
    }

    override fun onPostExecute(result: IAdapter?) {
        if (mCallback != null) {
            mCallback?.invoke(result)
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

    private var mCallback: ((IAdapter?) -> Unit)? = null
}
