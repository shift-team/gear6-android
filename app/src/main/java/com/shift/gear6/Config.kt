package com.shift.gear6

class Config {
    class WebConfig {
        val protocol = "http"
        val hostname = "192.168.0.2"
        val port = 3000
        val uploadPath = "obd2data"
    }

    class WifiAdapterConfig {
        val ipAddress = "192.168.0.10"
        val port = 35000
    }

    companion object {
        @JvmStatic
        val Web = WebConfig()

        @JvmStatic
        val WifiAdapter = WifiAdapterConfig()
    }
}