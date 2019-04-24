package com.shift.gear6.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.TableRow
import android.widget.TextView
import de.siegmar.fastcsv.reader.CsvReader
import kotlinx.android.synthetic.main.activity_drive_viewer.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import util.FileUploadService
import util.ServiceGenerator
import java.io.File
import java.io.FileReader


class DriveViewerActivity : AppCompatActivity() {
    var filename: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.shift.gear6.R.layout.activity_drive_viewer)

        filename = intent.extras!!.getString("filename")!!

        populateTable(filename as String)
    }

    private fun populateTable(filename: String) {
        val file = File(filesDir, filename)
        val fileReader = FileReader(file)

        val csvReader = CsvReader()

        csvReader.setContainsHeader(true)
        val contents = csvReader.read(fileReader)

        val headerRow = TableRow(this)
        headerRow.setPadding(0, 0, 0, 5)
        table.addView(headerRow)

        for (header in contents.header) {
            val view = TextView(this)
            view.text = header
            view.setPadding(0, 0, 20, 0)
            headerRow.addView(view)
        }

        for (row in contents.rows) {
            val newRow = TableRow(this)
            newRow.setPadding(0, 0, 0, 10)
            table.addView(newRow)
            for (data in row.fields) {
                val view = TextView(this)
                view.setPadding(0, 0, 20, 0)
                view.text = data
                newRow.addView(view)
            }
        }
    }

    fun onUploadButtonClicked(view: View) {
        val protocol = "http"
        val hostname = "192.168.0.2"
        val port = 3000
        val uploadPath = "obd2data"
        val timeout = 5000 // five seconds

        val url =
            protocol + "://" +
                    hostname + ":" +
                    port.toString()
        doUpload(url)
        return

        /*val uploadTask = UploadDataTask()
        val params = UploadDataTask.Params()
        params.filename = File(filesDir,filename).absolutePath

        params.callback = {
            if (it.success) {
                Global.logMessage("Uploaded " + params.filename + " successfully")
            } else {
                Global.logMessage(it.error)
            }
        }

        uploadTask.execute(params)*/

    }

    fun onDeleteButtonClicked(view: View) {
        File(filesDir, filename).delete()
        finish()
    }

    private fun doUpload(url: String) {
        val service = ServiceGenerator.createService(FileUploadService::class.java)

        val file = File(filesDir, filename)

        val requestBody = RequestBody.create(MediaType.parse("text/csv"), file)

        // MultipartBody.Part is used to send also the actual file name
        val body = MultipartBody.Part.createFormData("file", file.name, requestBody)

        // add another part within the multipart request
        val nameString = "driveLog"
        val name = RequestBody.create(
            okhttp3.MultipartBody.FORM, nameString
        )

        val call = service.upload(name, body)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                Log.v("Upload", "success")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Upload error:", t.message)
            }
        })
    }
}
