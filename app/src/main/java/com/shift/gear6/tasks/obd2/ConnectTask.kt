package com.shift.gear6.tasks.obd2

import android.os.AsyncTask
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
            return wifiAdapter
        }

        val blueToothAdapter = getBlueToothAdapter()
        if (blueToothAdapter != null) {
            return blueToothAdapter
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
}
