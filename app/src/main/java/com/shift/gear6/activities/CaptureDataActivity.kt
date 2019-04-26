package com.shift.gear6.activities

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TableRow
import android.widget.TextView
import com.shift.gear6.CarDataSnapshot
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
import java.text.SimpleDateFormat
import java.util.*

class CaptureDataActivity : AppCompatActivity() {
    private class RecordRow(context: Context) {
        public val parameterNameView = TextView(context)
        public val currentView = TextView(context)
        public val lowView = TextView(context)
        public val highView = TextView(context)
    }

    private var fetchTask = FetchDataTask()
    private var adapter: IAdapter? = null

    private var params = FetchDataTask.Params()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    private lateinit var file: File
    private lateinit var fileWriter: FileWriter
    private lateinit var csvWriter: CsvWriter
    private lateinit var csvAppender: CsvAppender

    private var filename = ""

    private var dataCaptured = 0

    private val tableRecords = HashMap<String, RecordRow>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_data)

        filename = dateFormat.format(Date()) + ".csv"

        file = File(getExternalFilesDir(null), filename)
        fileWriter = FileWriter(file)
        csvWriter = CsvWriter()
        csvAppender = csvWriter.append(fileWriter)

        params = intent.extras!!.getSerializable("params") as FetchDataTask.Params

        createTable(params.dataToGet)

        csvAppender.appendField("Timestamp")
        for (kv in params.dataToGet) {
            if (kv.value) {
                csvAppender.appendField(kv.key)
            }
        }
        csvAppender.endLine()
        csvAppender.flush()

        initializeAdapter()
    }

    private fun padElement(view: View) {
        view.setPadding(0, 10, 20, 10)
    }

    private fun createTable(dataToGet: HashMap<String, Boolean>) {
        val headerRow = TableRow(this)

        val headerParameter = TextView(this)
        headerParameter.text = "Parameter"
        padElement(headerParameter)

        headerRow.addView(headerParameter)

        val headerCurrent = TextView(this)
        headerCurrent.text = "Current"
        padElement(headerCurrent)

        val headerLow = TextView(this)
        headerLow.text = "Low"
        padElement(headerLow)

        val headerHigh = TextView(this)
        headerHigh.text = "High"
        padElement(headerHigh)

        headerRow.addView(headerCurrent)
        headerRow.addView(headerLow)
        headerRow.addView(headerHigh)

        table.addView(headerRow)

        for (kvPair in dataToGet) {
            if (!kvPair.value) {
                continue
            }

            val row = TableRow(this)

            val record = RecordRow(this)
            record.parameterNameView.text = kvPair.key

            padElement(record.parameterNameView)
            padElement(record.currentView)
            padElement(record.lowView)
            padElement(record.highView)

            row.addView(record.parameterNameView)
            row.addView(record.currentView)
            row.addView(record.lowView)
            row.addView(record.highView)

            table.addView(row)

            tableRecords.put(kvPair.key, record)
        }
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
                showErrorDialog("Failed to connect to OBD2 adapter.")
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

    private fun showErrorDialog(error: String) {
        AlertDialog.Builder(this)
            .setMessage(error)
            .setCancelable(false)
            .setPositiveButton("Go back") { dialogInterface: DialogInterface, i: Int ->
                File(getExternalFilesDir(null), filename).delete()
                dialogInterface.dismiss()
                finish()
            }.create().show()
    }

    private fun onFetchComplete(result: FetchDataTask.Result) {
        if (!result.success) {
            Global.logMessage(result.error)
            showErrorDialog("Failed to fetch data from OBD2 adapter.")
            return
        } else {
            csvAppender.appendField(dateFormat.format(Date()))
            for (kv in result.data.data) {
                csvAppender.appendField(kv.value)
                dataCaptured += 4 // 4 bytes per param
            }
            csvAppender.endLine()
            csvAppender.flush()

            dataCapturedText.text = dataCaptured.toString() + " bytes"

            updateUI(result.data)
        }

        /*val handler = Handler()
        handler.postDelayed({*/
        fetchTask = FetchDataTask()
        fetchTask.execute(params)
        //}, 500)
    }

    private fun updateUI(data: CarDataSnapshot) {
        for (dataPair in data.data) {
            tableRecords[dataPair.key]!!.currentView.text = dataPair.value

            val current = dataPair.value.toDoubleOrNull()
            val low = tableRecords[dataPair.key]!!.lowView.text.toString().toDoubleOrNull()
            val high = tableRecords[dataPair.key]!!.highView.text.toString().toDoubleOrNull()

            if (current == null) {
                return
            }

            if (low == null || current < low) {
                tableRecords[dataPair.key]!!.lowView.text = current.toString()
            }

            if (high == null || current > high) {
                tableRecords[dataPair.key]!!.highView.text = current.toString()
            }
        }
    }

    fun onStopButtonClick(view: View) {
        fetchTask.cancel(true)

        csvAppender.close()
        fileWriter.close()

        val intent = Intent(this, DriveViewerActivity::class.java)
        intent.putExtra("filename", filename)

        startActivity(intent)

        finish()
    }

    override fun onPause() {
        super.onPause()

        finish()
    }
}
