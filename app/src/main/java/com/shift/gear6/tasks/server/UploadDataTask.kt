package com.shift.gear6.tasks.server

import android.os.AsyncTask
import com.shift.gear6.CarDataSnapshot
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

class UploadDataTask : AsyncTask<UploadDataTask.Params, Unit, UploadDataTask.Result>() {
    class Params {
        // TODO: Snapshot needs to be changed to a file upload
        var snapshot: CarDataSnapshot? = null
        var callback: ((Result) -> Unit)? = null
    }

    class Result {
        var success = true
    }

    private var myParams: Params? = null

    private val protocol = "http"
    private val hostname = "192.168.0.2"
    private val port = 3000
    private val uploadPath = "obd2data"
    private val timeout = 5000 // five seconds

    override fun doInBackground(vararg params: Params): Result {
        val result = Result()

        if (params.isEmpty() || params[0].snapshot == null) {
            result.success = false

            return result
        }

        try {
            myParams = params[0]

            val url = URL(
                protocol + "://" +
                        hostname + ":" +
                        port.toString() + "/" +
                        uploadPath
            )

            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.connectTimeout = timeout
            conn.readTimeout = timeout
            conn.doOutput = true
            conn.doInput = true

            /*val postData = params[0].snapshot?.toJSONString()?.toByteArray(StandardCharsets.UTF_8)

            conn.setRequestProperty("charset", "utf-8")
            conn.setRequestProperty("Content-length", postData?.size.toString())
            conn.setRequestProperty("Content-Type", "application/json")*/

            val outputStream = DataOutputStream(conn.outputStream)

            //outputStream.write(postData)
            outputStream.flush()

            result.success = conn.responseCode == HttpURLConnection.HTTP_OK
            return result
        } catch (ex: Exception) {
            result.success = false
            return result
        }
    }

    override fun onPostExecute(result: Result) {
        if (myParams?.callback != null) {
            myParams?.callback?.invoke(result)
        }
    }
}