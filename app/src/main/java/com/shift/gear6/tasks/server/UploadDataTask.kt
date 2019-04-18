package com.shift.gear6.tasks.server

import android.os.AsyncTask
import java.net.URL


class UploadDataTask : AsyncTask<UploadDataTask.Params, Unit, UploadDataTask.Result>() {
    class Params {
        var filename: String? = null
        var callback: ((Result) -> Unit)? = null
    }

    class Result {
        var success = true
        var error = ""
    }

    private var myParams: Params? = null

    private val protocol = "http"
    private val hostname = "192.168.0.2"
    private val port = 3000
    private val uploadPath = "obd2data"
    private val timeout = 5000 // five seconds

    override fun doInBackground(vararg params: Params): Result {
        val result = Result()
        myParams = params[0]

        if (params.isEmpty()
            || params[0].filename == null
            || myParams!!.filename!!.isEmpty()
        ) {
            result.success = false
            result.error = "No filename provided"

            return result
        }

        val url = URL(
            protocol + "://" +
                    hostname + ":" +
                    port.toString() + "/" +
                    uploadPath
        )




        return result
    }

    override fun onPostExecute(result: Result) {
        if (myParams?.callback != null) {
            myParams?.callback?.invoke(result)
        }
    }
}