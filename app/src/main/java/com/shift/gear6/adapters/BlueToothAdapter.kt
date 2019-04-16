package com.shift.gear6.adapters

import android.bluetooth.BluetoothAdapter
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket

class BlueToothAdapter() : IAdapter {
    private var mConnection: Socket? = null

    init {
        /*var adapter = BluetoothAdapter.getDefaultAdapter()
        if (adapter == null)
        {
            throw NullPointerException("Bluetooth is not available on this device")
        }*/
        throw NotImplementedError("Bluetooth adapter is not yet implemented")
    }

    override fun getInputStream(): InputStream {
        return mConnection!!.getInputStream()
    }

    override fun getOutputStream(): OutputStream {
        return mConnection!!.getOutputStream()
    }

    override fun connect(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return false
    }

}