package com.shift.gear6.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.shift.gear6.Global
import com.shift.gear6.R
import kotlinx.android.synthetic.main.activity_log_viewer.*

class LogViewerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_viewer)

        button.setOnClickListener {
            finish()
        }

        for (message in Global.log) {
            val view = TextView(logEntries.context)
            view.text = message

            logEntries.addView(view)
        }
    }
}
