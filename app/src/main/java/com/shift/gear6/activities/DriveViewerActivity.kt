package com.shift.gear6.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TableRow
import android.widget.TextView
import com.shift.gear6.R
import de.siegmar.fastcsv.reader.CsvReader
import kotlinx.android.synthetic.main.activity_drive_viewer.*
import java.io.File
import java.io.FileReader

class DriveViewerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drive_viewer)

        var filename = intent.extras!!.getString("filename")!!

        populateTable(filename)
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
}
