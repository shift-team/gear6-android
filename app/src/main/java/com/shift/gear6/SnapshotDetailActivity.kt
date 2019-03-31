package com.shift.gear6

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_snapshot_detail.*
import kotlinx.android.synthetic.main.content_snapshot_detail.*

class SnapshotDetailActivity : AppCompatActivity() {
    var snapshot: CarDataSnapshot? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snapshot_detail)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        snapshot = intent?.extras?.getSerializable("snapshot") as CarDataSnapshot?

        if (snapshot == null) {
            snapshot = CarDataSnapshot()
        }

        textView_rpm_value.text = snapshot?.rpm.toString()
        textView_engineLoad_value.text = snapshot?.engineLoad.toString()
        textView_barometricPressure_value.text = snapshot?.barometricPressure.toString()
        textView_airFuelRatio_value.text = snapshot?.airFuelRatio.toString()
        textView_MAF_value.text = snapshot?.MAF.toString()
        textView_oilTemp_value.text = snapshot?.oilTemp.toString()
        textView_engineRuntime_value.text = snapshot?.engineRuntime.toString()
        textView_speed_value.text = snapshot?.speed.toString()
        textView_fuelConsumption_value.text = snapshot?.fuelConsumption.toString()
        textView_fuelLevel_value.text = snapshot?.fuelLevel.toString()
        textView_fuelPressure_value.text = snapshot?.fuelPressure.toString()
        textView_intakeManifoldPressure_value.text = snapshot?.intakeManifoldPressure.toString()
        textView_airIntakeTemperature_value.text = snapshot?.airIntakeTemp.toString()
        textView_ambientAirTemperature_value.text = snapshot?.ambientAirTemp.toString()
        textView_engineCoolantTemperature_value.text = snapshot?.engineCoolantTemp.toString()
    }
}
