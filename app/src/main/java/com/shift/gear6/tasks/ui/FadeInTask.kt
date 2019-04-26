package com.shift.gear6.tasks.ui

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.AsyncTask
import android.widget.ImageView

class FadeInTask : AsyncTask<FadeInTask.Params, Unit, Unit>() {
    class Params {
        var opacity = 0
        var imageView: ImageView? = null
        var callback: (() -> Unit)? = null
    }

    private var myParams = Params()

    override fun doInBackground(vararg params: Params?) {
        myParams = params[0]!!

        if (myParams.imageView == null) {
            return // There is no work to do if there is no image
        }
    }

    override fun onPostExecute(result: Unit?) {
        super.onPostExecute(result)

        if (myParams.opacity < 200) {
            val color = Integer.toHexString(myParams.opacity + 10)

            val array = FloatArray(3)
            array[0] = 0f
            array[1] = 0f
            array[2] = 1f

            myParams.imageView?.setColorFilter(Color.HSVToColor(myParams.opacity + 10, array), PorterDuff.Mode.OVERLAY)
        }

        myParams.callback?.invoke()
    }

}