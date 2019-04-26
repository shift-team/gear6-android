package com.shift.gear6.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.shift.gear6.R
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private class ImageTouchListener : View.OnTouchListener {
        private val colorString = "#80FFFFFF" // Half transparent white

        override fun onTouch(view: View?, event: MotionEvent?): Boolean {
            if (view is ImageView) {
                if (event?.action == MotionEvent.ACTION_DOWN) {
                    (view as ImageView).setColorFilter(Color.parseColor(colorString), PorterDuff.Mode.OVERLAY)
                } else if (event?.action == MotionEvent.ACTION_UP) {
                    (view as ImageView).clearColorFilter()
                }
            }
            return false
        }
    }

    private class TextTouchListener(private var imageView: ImageView) : View.OnTouchListener {
        override fun onTouch(view: View?, event: MotionEvent?): Boolean {
            return imageView.dispatchTouchEvent(event)
        }
    }

    private val imageTouchListener = ImageTouchListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageNewDrive.setOnTouchListener(imageTouchListener)
        imagePreviousDrives.setOnTouchListener(imageTouchListener)
        imageSettings.setOnTouchListener(imageTouchListener)

        textNewDrive.setOnTouchListener(TextTouchListener(imageNewDrive))
        textPreviousDrives.setOnTouchListener(TextTouchListener(imagePreviousDrives))
        textSettings.setOnTouchListener(TextTouchListener(imageSettings))
    }

    fun onNewDriveClick(view: View) {
        val intent = Intent(this, SelectDataToFetchActivity::class.java)

        startActivity(intent)
    }

    fun onPreviousDrivesClick(view: View) {
        val intent = Intent(this, PreviousDriveSelectionActivity::class.java)

        startActivity(intent)
    }

    fun onSettingsClick(view: View) {
        val intent = Intent(this, SettingsActivity::class.java)

        startActivity(intent)
    }

    fun onButtonTouch(view: View, event: MotionEvent) {
    }
}
