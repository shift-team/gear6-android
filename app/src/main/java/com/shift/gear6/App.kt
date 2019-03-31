package com.shift.gear6

import android.app.Application
import com.shift.gear6.adapters.IAdapter
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class App : Application() {
    var log = ArrayList<String>()
    var adapter: IAdapter? = null

    fun LogMessage(message: String) {
        log.add(Date().toString() + ": " + message)
    }
}