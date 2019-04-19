package com.shift.gear6

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