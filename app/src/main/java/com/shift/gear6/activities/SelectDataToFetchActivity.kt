package com.shift.gear6.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.shift.gear6.CommandNames
import com.shift.gear6.R
import com.shift.gear6.tasks.obd2.FetchDataTask
import kotlinx.android.synthetic.main.activity_select_data_to_fetch.*

class SelectDataToFetchActivity : AppCompatActivity() {

    private val params = FetchDataTask.Params()

    private var checkBoxMapping: Map<CheckBox, String>? = null

    private var dataEstimate = 0
    private var paramsSelected = 0

    private fun calculateEstimate() {
        val BytesPerParam = 4
        val RefreshHz = 2
        val SecondsPerMinute = 60

        dataEstimate = paramsSelected * BytesPerParam * RefreshHz * SecondsPerMinute

        dataEstimateValue.text = dataEstimate.toString() + " bytes"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_data_to_fetch)

        checkBoxMapping = createCheckBoxMapping()

        setListHeaderClickHandler(engineSubHeader, engineList)
        setListHeaderClickHandler(fuelSubHeader, fuelList)
        setListHeaderClickHandler(pressureSubHeader, pressureList)
        setListHeaderClickHandler(temperatureSubHeader, temperatureList)

        goBackButton.setOnClickListener {
            finish()
        }

        beginCaptureButton.setOnClickListener {
            val intent = Intent(this, CaptureDataActivity::class.java)

            intent.putExtra("params", params)

            startActivity(intent)
        }

        setCheckBoxClickHandler(engineList)
        setCheckBoxClickHandler(fuelList)
        setCheckBoxClickHandler(pressureList)
        setCheckBoxClickHandler(temperatureList)
    }

    private fun createCheckBoxMapping(): Map<CheckBox, String> {
        val map = HashMap<CheckBox, String>()

        map[rpmCheckBox] = CommandNames.RPM
        map[absoluteLoadCheckBox] = CommandNames.AbsoluteLoad
        map[airFuelRatioCheckBox] = CommandNames.AirFuelRatio
        map[airIntakeTemperatureCheckBox] = CommandNames.AirIntakeTemperature
        map[ambientAirTemperatureCheckBox] = CommandNames.AmbientAirTemperature

        map[barometricPressureCheckBox] = CommandNames.BarometricPressure
        map[consumptionRateCheckBox] = CommandNames.ConsumptionRate
        map[engineCoolantTemperatureCheckBox] = CommandNames.EngineCoolantTemperature
        map[fuelLevelCheckBox] = CommandNames.FuelLevel
        map[fuelPressureCheckBox] = CommandNames.FuelPressure

        map[fuelRailPressureCheckBox] = CommandNames.FuelRailPressure
        map[fuelTrimCheckBox] = CommandNames.FuelTrim
        map[intakeManifoldPressureCheckBox] = CommandNames.IntakeManifoldPressure
        map[loadCheckBox] = CommandNames.Load
        map[massAirFlowCheckBox] = CommandNames.MassAirFlow

        map[oilTemperatureCheckBox] = CommandNames.OilTemperature
        map[relativeThrottlePositionCheckBox] = CommandNames.RelativeThrottlePosition
        map[speedCheckBox] = CommandNames.Speed
        map[throttlePositionCheckBox] = CommandNames.ThrottlePosition
        map[widebandAirFuelRatioCheckBox] = CommandNames.WidebandAirFuelRatio

        return map
    }

    fun setCheckBoxClickHandler(view: ViewGroup) {
        for (i in 0..view.childCount) {
            val checkbox = view.getChildAt(i) as CheckBox?
            if (checkbox != null) {
                checkbox.setOnClickListener {
                    params.dataToGet[checkBoxMapping!!.getValue(it as CheckBox)] = it.isChecked
                    if (it.isChecked) {
                        ++paramsSelected
                    } else {
                        --paramsSelected
                    }
                    calculateEstimate()
                }
            }
        }
    }

    fun setListHeaderClickHandler(view: View, list: View) {
        view.setOnClickListener {
            val textView = view as TextView
            val text = textView.text

            if (list.visibility == View.GONE) {
                list.visibility = View.VISIBLE
                //TODO: These need to be changed to resource strings
                textView.text = text.split(' ')[0] + " (tap to contract)"
            } else {
                list.visibility = View.GONE
                textView.text = text.split(' ')[0] + " (tap to expand)"
            }
        }
    }
}
