package com.shift.gear6.adapters

import java.io.InputStream
import java.io.OutputStream

interface IAdapter {
    fun getInputStream(): InputStream

    fun getOutputStream(): OutputStream

    fun connect(): Boolean
}
