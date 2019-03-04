package com.shift.gear6.adapters

import com.shift.gear6.Config
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress

class WiFiAdapter() : IAdapter {
    private var mConnection = Socket()

    init {
        val address = InetSocketAddress(Config.WifiAdapter.ipAddress, Config.WifiAdapter.port)
        mConnection.connect(address as SocketAddress, Config.WifiAdapter.timeOut)
    }

    override fun getInputStream(): InputStream {
        return mConnection.getInputStream()
    }

    override fun getOutputStream(): OutputStream {
        return mConnection.getOutputStream()
    }
}