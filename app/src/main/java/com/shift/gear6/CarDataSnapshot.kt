package com.shift.gear6

import org.json.JSONObject

class CarDataSnapshot {
    var rpm = 0
    var engineLoad = 0f

    fun toJSONString(): String {
        val json = JSONObject()

        json.put("rpm", rpm)
        json.put("engineLoad", engineLoad)

        return json.toString()
    }

}
