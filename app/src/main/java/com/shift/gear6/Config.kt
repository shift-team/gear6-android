package com.shift.gear6

/*TODO: Delete this class once information is finalized. */

class Config {
    class WebConfig {
        val protocol = "http"
        val hostname = "192.168.0.2"
        val port = 3000
        val uploadPath = "obd2data"
        val timeout = 5000
    }

    class WifiAdapterConfig {
        val ipAddress = "192.168.0.10"
        val port = 35000
        val timeOut = 2000
    }

    companion object {
        @JvmStatic
        val Web = WebConfig()

        @JvmStatic
        val WifiAdapter = WifiAdapterConfig()
    }
}