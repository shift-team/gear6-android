package com.shift.gear6.activities

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.shift.gear6.CommandNames
import com.shift.gear6.Global
import com.shift.gear6.R
import com.shift.gear6.adapters.IAdapter
import com.shift.gear6.tasks.obd2.ConnectTask
import com.shift.gear6.tasks.obd2.FetchDataTask
import de.siegmar.fastcsv.writer.CsvAppender
import de.siegmar.fastcsv.writer.CsvWriter
import kotlinx.android.synthetic.main.activity_capture_data.*
import java.io.File
import java.io.FileWriter
import java.util.*

class CaptureDataActivity : AppCompatActivity() {
    private var fetchTask = FetchDataTask()
    private var adapter: IAdapter? = null

    private var params = FetchDataTask.Params()

    private lateinit var file: File
    private lateinit var fileWriter: FileWriter
    private lateinit var csvWriter: CsvWriter
    private lateinit var csvAppender: CsvAppender

    private var dataCaptured = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_data)

        file = File(filesDir, Date().toString().replace(" ", "") + ".csv")
        fileWriter = FileWriter(file)
        csvWriter = CsvWriter()
        csvAppender = csvWriter.append(fileWriter)

        params = intent.extras!!.getSerializable("params") as FetchDataTask.Params

        for (kv in params.dataToGet) {
            if (kv.value) {
                csvAppender.appendField(kv.key)
            }
        }
        csvAppender.endLine()
        csvAppender.flush()

        initializeAdapter()
    }

    private fun initializeAdapter() {
        val connectTask = ConnectTask()

        val params = ConnectTask.Params()
        params.adapterType = ConnectTask.Params.AdapterType.WiFi
        params.callback = {
            if (it.success) {
                adapter = it.adapter

                beginCapture()
            } else {
                Global.logMessage(it.error)
            }
        }

        connectTask.execute(params)
    }

    private fun beginCapture() {
        params.callback = {
            onFetchComplete(it)
        }
        params.adapter = adapter

        fetchTask.execute(params)
    }

    private fun onFetchComplete(result: FetchDataTask.Result) {
        if (!result.success) {
            Global.logMessage(result.error)

            return
        } else {
            for (kv in result.data.data) {
                csvAppender.appendField(kv.value)
                dataCaptured += 4 // 4 bytes per param
            }
            csvAppender.endLine()
            csvAppender.flush()

            dataCapturedText.text = dataCaptured.toString() + " bytes"
            currentRPM.text = result.data.data[CommandNames.RPM]
        }

        val handler = Handler()
        handler.postDelayed({
            fetchTask = FetchDataTask()
            fetchTask.execute(params)
        }, 500)
    }

    fun onStopButtonClick(view: View) {
        // val intent = Intent(this, MainActivity::class.java)
        // startActivity(intent)

        fetchTask.cancel(true)

        csvAppender.close()
        fileWriter.close()

        finish()
    }
}
