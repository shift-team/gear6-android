package com.shift.gear6.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.shift.gear6.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }

    fun onViewLogButtonClicked(view: View) {
        val intent = Intent(this, LogViewerActivity::class.java)

        startActivity(intent)
    }
}
