package com.shift.gear6.adapters

import com.shift.gear6.Global
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress
import java.net.SocketTimeoutException

class WiFiAdapter() : IAdapter {
    private val ipAddress = "192.168.0.10"
    private val port = 35000
    private val timeOut = 2000 // two seconds

    private val mConnection = Socket()

    override fun getInputStream(): InputStream {
        return mConnection.getInputStream()
    }

    override fun getOutputStream(): OutputStream {
        return mConnection.getOutputStream()
    }

    override fun connect(): Boolean {
        if (mConnection.isConnected) {
            mConnection.close()
        }

        val address = InetSocketAddress(ipAddress, port)

        mConnection.connect(address as SocketAddress, timeOut)

        return mConnection.isConnected
    }
}