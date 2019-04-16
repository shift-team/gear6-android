package com.shift.gear6.tasks.obd2

import android.os.AsyncTask
import br.ufrn.imd.obd.commands.ObdCommandGroup
import br.ufrn.imd.obd.commands.protocol.*
import br.ufrn.imd.obd.enums.ObdProtocols
import com.shift.gear6.adapters.*
import java.net.SocketTimeoutException

class ConnectTask : AsyncTask<ConnectTask.Params, Void, ConnectTask.Result>() {
    class Params {
        enum class AdapterType {WiFi, BlueTooth}

        var adapterType = AdapterType.WiFi
        var callback: ((Result) -> Unit)? = null
    }

    class Result {
        var success = true
        var adapter: IAdapter? = null
        var error = ""
    }

    private var mParams = Params()

    override fun doInBackground(vararg params: ConnectTask.Params): Result {
        val result = Result()

        mParams = params[0]

        when(mParams.adapterType) {
            Params.AdapterType.WiFi -> result.adapter = WiFiAdapter()
            Params.AdapterType.BlueTooth -> result.adapter = BlueToothAdapter()
        }

        // Failed to make a connection with an adapter such as failing to connect to an IP Address
        try {
            if (!result.adapter!!.connect()) {
                result.success = false
                result.error = "Failed to connect to the adapter"

                return result
            }
        } catch (e: SocketTimeoutException) {
            result.success = false
            result.error = "Timed out while trying to connect to the adapter. Exception: " + e.message.orEmpty()

            return result
        }

        // Failed to initialize the adapter by sending it preparation commands
        if (!prepareAdapter(result.adapter!!)) {
            result.success = false
            result.error = "Failed to initialize the adapter"

            return result
        }

        return result
    }

    override fun onPostExecute(result: Result) {
        if (mParams.callback != null) {
            mParams.callback?.invoke(result)
        }
    }

    private fun prepareAdapter(adapter: IAdapter): Boolean {
        return try {
            val commands = ObdCommandGroup()

            commands.add(ObdResetCommand())
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
