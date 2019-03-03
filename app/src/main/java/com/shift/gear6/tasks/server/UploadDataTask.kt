package com.shift.gear6.tasks.server

import android.content.Context
import android.os.AsyncTask
import android.util.Xml
import android.widget.Toast
import com.shift.gear6.CarDataSnapshot
import com.shift.gear6.Config
import com.shift.gear6.OnTaskCompleted
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class UploadDataTask : AsyncTask<UploadDataTask.Params, Void, Boolean>() {
    class Params {
        var snapshot: CarDataSnapshot? = null
        var callback: OnTaskCompleted<Boolean>? = null
    }

    var callback: OnTaskCompleted<Boolean>? = null

    override fun doInBackground(vararg params: UploadDataTask.Params): Boolean {
        try {
            callback = params[0].callback

            val url = URL(
                Config.Web.protocol + "://" +
                        Config.Web.hostname + ":" +
                        Config.Web.port.toString() + "/" +
                        Config.Web.uploadPath
            )

            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.connectTimeout = 5000
            conn.readTimeout = 5000
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
            return false
        }
    }

    override fun onPostExecute(result: Boolean) {
        if (callback != null) {
            callback?.onTaskCompleted(result)
        }
    }

}