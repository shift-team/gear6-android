package com.shift.gear6.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TableRow
import android.widget.TextView
import com.shift.gear6.CarDataSnapshot
import com.shift.gear6.R
import kotlinx.android.synthetic.main.activity_snapshot_detail.*

class SnapshotDetailActivity : AppCompatActivity() {
    var snapshot: CarDataSnapshot? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snapshot_detail)

        snapshot = intent?.extras?.getSerializable("snapshot") as CarDataSnapshot?

        if (snapshot == null) {
            snapshot = CarDataSnapshot()
        }

        for (keyValuePair in snapshot!!.data) {
            val row = TableRow(this)

            val key = TextView(this)
            key.text = keyValuePair.key

            val value = TextView(this)
            value.text = keyValuePair.value

            row.addView(key)
            row.addView(value)

            table.addView(row)
        }
    }
}
