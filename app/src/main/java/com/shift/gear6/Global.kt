package com.shift.gear6

import com.shift.gear6.adapters.IAdapter
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.LockSupport

class Global {
    companion object {
        @JvmStatic
        var log = ArrayList<String>()

        @Synchronized
        fun logMessage(message: String) {
            log.add(message)
        }
    }
}