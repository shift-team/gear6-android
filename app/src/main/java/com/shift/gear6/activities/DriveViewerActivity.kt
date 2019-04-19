package com.shift.gear6.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TableRow
import android.widget.TextView
import de.siegmar.fastcsv.reader.CsvReader
import kotlinx.android.synthetic.main.activity_drive_viewer.*
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
        table.addView(headerRow)

        for (header in contents.header) {
            val view = TextView(this)
            view.text = header
            headerRow.addView(view)
        }

        for (row in contents.rows) {
            val newRow = TableRow(this)
            table.addView(newRow)
            for (data in row.fields) {
                val view = TextView(this)
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
                    port.toString() + "/" +
                    uploadPath


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
}
