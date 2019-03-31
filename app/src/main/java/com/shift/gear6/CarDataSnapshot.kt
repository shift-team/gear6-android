package com.shift.gear6

import org.json.JSONObject
import java.io.Serializable

class CarDataSnapshot : Serializable {
    var rpm = 0
    var engineLoad = 0f
    var barometricPressure = 0f
    var airFuelRatio = 0.0
    var MAF = 0.0
    var oilTemp = 0f
    var engineRuntime = 0L
    var speed = 0f
    var fuelConsumption = 0f
    var fuelLevel = 0f
    var fuelPressure = 0f
    var intakeManifoldPressure = 0f
    var airIntakeTemp = 0f
    var ambientAirTemp = 0f
    var engineCoolantTemp = 0f

    fun toJSONString(): String {
        val json = JSONObject()

        json.put("rpm", rpm)
        json.put("engineLoad", engineLoad)
        json.put("barometricPressure", barometricPressure)
        json.put("airFuelRatio", airFuelRatio)
        json.put("MAF", MAF)
        json.put("oilTemp", oilTemp)
        json.put("engineRuntime", engineRuntime)
        json.put("speed", speed)
        json.put("fuelConsumption", fuelConsumption)
        json.put("fuelLevel", fuelLevel)
        json.put("fuelPressure", fuelPressure)
        json.put("intakeManifoldPressure", intakeManifoldPressure)
        json.put("airIntakeTemp", airIntakeTemp)
        json.put("ambientAirTemp", ambientAirTemp)
        json.put("engineCoolantTemp", engineCoolantTemp)

        return json.toString()
    }

}
