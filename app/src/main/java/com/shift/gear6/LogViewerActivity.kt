package com.shift.gear6

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_log_viewer.*

class LogViewerActivity : AppCompatActivity() {

    var backActivity: Activity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_viewer)

        button.setOnClickListener {
            finish()
        }

        logEntries.removeAllViews()

        val app = applicationContext as App
        for (entry in app.log) {
            val view = TextView(logEntries.context)
            view.text = entry
            logEntries.addView(view)
        }
    }
}
