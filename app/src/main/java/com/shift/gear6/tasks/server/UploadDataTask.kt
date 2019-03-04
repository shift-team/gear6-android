package com.shift.gear6.tasks.server

import android.os.AsyncTask
import com.shift.gear6.App
import com.shift.gear6.CarDataSnapshot
import com.shift.gear6.Config
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class UploadDataTask : AsyncTask<UploadDataTask.Params, Void, Boolean>() {
    class Params {
        var snapshot: CarDataSnapshot? = null
        var callback: ((Boolean) -> Unit)? = null
        var app: App? = null
    }

    private var mCallback: ((Boolean) -> Unit)? = null

    override fun doInBackground(vararg params: UploadDataTask.Params): Boolean {
        if (params.isEmpty() || params[0].snapshot == null) {
            return false
        }

        try {
            mCallback = params[0].callback

            val url = URL(
                Config.Web.protocol + "://" +
                        Config.Web.hostname + ":" +
                        Config.Web.port.toString() + "/" +
                        Config.Web.uploadPath
            )

            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.connectTimeout = Config.Web.timeout
            conn.readTimeout = Config.Web.timeout
            conn.doOutput = true
            conn.doInput = true

            val postData = params[0].snapshot?.toJSONString()?.toByteArray(StandardCharsets.UTF_8)

            conn.setRequestProperty("charset", "utf-8")
            conn.setRequestProperty("Content-length", postData?.size.toString())
            conn.setRequestProperty("Content-Type", "application/json")

            val outputStream = DataOutputStream(conn.outputStream)

            outputStream.write(postData)
            outputStream.flush()

            return conn.responseCode == HttpURLConnection.HTTP_OK
        } catch (ex: Exception) {
            if (params[0].app != null && ex.message != null) {
                params[0].app!!.log.add(ex.message!!)
            }
            return false
        }
    }

    override fun onPostExecute(result: Boolean) {
        if (mCallback != null) {
            mCallback?.invoke(result)
        }
    }
}