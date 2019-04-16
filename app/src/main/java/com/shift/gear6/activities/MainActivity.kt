package com.shift.gear6.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.shift.gear6.R


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun newDrive_onClick(view: View) {
        val intent = Intent(this, SelectDataToFetchActivity::class.java)

        startActivity(intent)
    }

    fun onPreviousDrivesClick(view: View) {
        val intent = Intent(this, PreviousDriveSelectionActivity::class.java)

        startActivity(intent)
    }

    fun settings_onClick(view: View) {
        val intent = Intent(this, SettingsActivity::class.java)

        startActivity(intent)
    }
}
