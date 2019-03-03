package com.shift.gear6

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.shift.gear6.adapters.IAdapter
import com.shift.gear6.tasks.obd2.ConnectTask
import com.shift.gear6.tasks.obd2.FetchDataTask
import com.shift.gear6.tasks.server.UploadDataTask
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var adapter: IAdapter? = null
    private var snapshot: CarDataSnapshot? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "When clicked this will share performance figures", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        buttonConnectAdapter.setOnClickListener {
            connectToAdapter()
        }

        buttonFetchData.setOnClickListener {
            fetchData()
        }

        buttonUploadData.setOnClickListener {
            uploadToServer()
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun connectToAdapter() {
        val connectParams = ConnectTask.Params()

        val context = this as Context

        connectParams.callback = object : OnTaskCompleted<IAdapter?> {
            override fun onTaskCompleted(data: IAdapter?) {
                if (data != null) {
                    adapter = data
                    Toast.makeText(context, "Received adapter.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Failed to get adapter.", Toast.LENGTH_LONG).show()
                }
            }
        }
        ConnectTask().execute(connectParams)
    }

    private fun fetchData() {
        if (adapter == null) {
            Toast.makeText(this, "Cannot fetch data. Adapter is null.", Toast.LENGTH_LONG).show()
            return
        }

        val fetchParams = FetchDataTask.Params()

        fetchParams.adapter = adapter

        fetchParams.getRPM = true
        fetchParams.getEngineLoad = true

        fetchParams.callback = object : OnTaskCompleted<CarDataSnapshot> {
            override fun onTaskCompleted(data: CarDataSnapshot) {
                snapshot = data
            }
        }

        FetchDataTask().execute(fetchParams)
    }

    private fun uploadToServer() {
        if (snapshot == null) {
            Toast.makeText(this, "Cannot upload. Snapshot is null.", Toast.LENGTH_LONG).show()
            return
        }
        val context = this as Context

        val uploadParams = UploadDataTask.Params()

        uploadParams.snapshot = snapshot

        uploadParams.callback = object : OnTaskCompleted<Boolean> {
            override fun onTaskCompleted(data: Boolean) {
                if (data) {
                    Toast.makeText(context, "Upload Successful", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Upload Failed", Toast.LENGTH_LONG).show()
                }
            }

        }

        UploadDataTask().execute(uploadParams)
    }
}
