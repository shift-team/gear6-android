package com.shift.gear6.adapters

import com.shift.gear6.Config
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

class WiFiAdapter : IAdapter {
    private var connection: Socket = Socket(
                Config.WifiAdapter.ipAddress,
                Config.WifiAdapter.port)

    override fun getInputStream(): InputStream {
        return connection.getInputStream()
    }

    override fun getOutputStream(): OutputStream {
        return connection.getOutputStream()
    }
}