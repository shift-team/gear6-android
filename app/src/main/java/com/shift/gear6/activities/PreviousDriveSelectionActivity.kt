package com.shift.gear6.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TableRow
import android.widget.TextView
import com.shift.gear6.R
import kotlinx.android.synthetic.main.activity_previous_drive_selection.*

class PreviousDriveSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_previous_drive_selection)

        var prevView: View? = null

        val headerRow = TableRow(this)
        val nameHeader = TextView(this)
        nameHeader.text = "Filename"

        val sizeHeader = TextView(this)
        sizeHeader.text = "Size"
        headerRow.addView(nameHeader)
        headerRow.addView(sizeHeader)

        table.addView(headerRow)

        for (file in filesDir.listFiles()) {
            if (file.name.indexOf(".csv") != -1) {
                val row = TableRow(this)
                val view = TextView(this)
                view.text = file.name

                val size = TextView(this)
                size.text = file.length().toString() + " bytes"

                view.setOnClickListener {
                    val intent = Intent(this, DriveViewerActivity::class.java)
                    intent.putExtra("filename", (it as TextView).text)

                    startActivity(intent)
                }

                row.addView(view)
                row.addView(size)

                table.addView(row)
            }
        }
    }
}
