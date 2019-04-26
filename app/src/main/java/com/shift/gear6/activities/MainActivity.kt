package com.shift.gear6.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import com.shift.gear6.R
import com.shift.gear6.tasks.ui.FadeInTask
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private class ImageTouchListener : View.OnTouchListener {
        private val colorString = "#20FFFFFF" // slightly opaque white

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

        animateButtonAndStartActivity(imageNewDrive, intent)
    }

    fun onPreviousDrivesClick(view: View) {
        val intent = Intent(this, PreviousDriveSelectionActivity::class.java)

        animateButtonAndStartActivity(imagePreviousDrives, intent)
    }

    fun onSettingsClick(view: View) {
        val intent = Intent(this, SettingsActivity::class.java)

        animateButtonAndStartActivity(imageSettings, intent)
    }

    private fun animateButtonAndStartActivity(imageView: ImageView, intent: Intent) {
        var task = FadeInTask()
        val params = FadeInTask.Params()
        params.imageView = imageView
        params.opacity = 0
        params.callback = {
            if (params.opacity > 200) {
                startActivity(intent)
            } else {
                task = FadeInTask()
                params.opacity += 10
                task.execute(params)
            }
        }

        task.execute(params)
    }

    override fun onResume() {
        super.onResume()

        imageNewDrive.clearColorFilter()
        imageSettings.clearColorFilter()
        imagePreviousDrives.clearColorFilter()
    }
}
