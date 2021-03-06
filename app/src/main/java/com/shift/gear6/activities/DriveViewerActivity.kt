package com.shift.gear6.activities

import android.app.AlertDialog
import android.content.DialogInterface
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
import java.lang.IllegalStateException


class DriveViewerActivity : AppCompatActivity() {
    var filename: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.shift.gear6.R.layout.activity_drive_viewer)

        filename = intent.extras!!.getString("filename")!!

        populateTable(filename as String)
    }

    private fun populateTable(filename: String) {
        val file = File(getExternalFilesDir(null), filename)
        val fileReader = FileReader(file)

        val csvReader = CsvReader()

        csvReader.setContainsHeader(true)

        try {
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
        } catch (ex: IllegalStateException) {
            AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage("Failed to open this drive file. It may be empty or corrupt.")
                .setPositiveButton("Delete Drive") { dialog: DialogInterface, _: Int ->
                    dialog.dismiss()
                    deleteFile()
                    finish()
                }
                .setNegativeButton("Return") { dialog: DialogInterface, _: Int ->
                    dialog.dismiss()
                    finish()
                }.create().show()
        }
    }

    fun onUploadButtonClicked(view: View) {
        doUpload()
    }

    fun onDeleteButtonClicked(view: View) {
        deleteFile()
        finish()
    }

    private fun deleteFile() {
        File(getExternalFilesDir(null), filename).delete()
    }

    private fun doUpload() {
        val service = ServiceGenerator.createService(FileUploadService::class.java)

        val file = File(getExternalFilesDir(null), filename)

        val requestBody = RequestBody.create(MediaType.parse("text/csv"), file)

        // MultipartBody.Part is used to send also the actual file name
        val body = MultipartBody.Part.createFormData("driveLog", file.name, requestBody)

        val call = service.upload(body)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                //TODO: Add url from response with ability to visit page
                Log.v("Upload", "success")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Upload error:", t.message)
            }
        })
    }
}
