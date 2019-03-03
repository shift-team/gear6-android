package com.shift.gear6

interface OnTaskCompleted<T> {
    fun onTaskCompleted(data: T)
}
