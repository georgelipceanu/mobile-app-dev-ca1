package com.example.ca1.views.cloudjoblist

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ca1.R
import com.example.ca1.adapters.CloudJobAdapter
import com.example.ca1.adapters.CloudJobListener
import com.example.ca1.databinding.ActivityCloudJobsListBinding
import com.example.ca1.main.MainApp
import com.example.ca1.models.CloudJobModel
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class CloudJobListView : AppCompatActivity(), CloudJobListener {

    lateinit var app: MainApp
    private lateinit var binding: ActivityCloudJobsListBinding
    lateinit var presenter: CloudJobListPresenter
    private var position: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        binding = ActivityCloudJobsListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)

        presenter = CloudJobListPresenter(this)
        app = application as MainApp

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        loadCloudJobs()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> presenter.doAddCloudJob()
            R.id.item_map -> presenter.doShowCloudJobsMap()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadCloudJobs() {
        binding.recyclerView.adapter =
            CloudJobAdapter(presenter.getCloudJobs(), this)
        onRefresh()
    }

    fun onRefresh() {
        binding.recyclerView.adapter =
            CloudJobAdapter(presenter.getCloudJobs(), this)
    }

    fun onDelete(position: Int) {
        binding.recyclerView.adapter?.notifyItemRemoved(position)
    }

    fun onItemChanged(position: Int) {
        binding.recyclerView.adapter?.notifyItemChanged(position)
    }

    override fun onCloudJobClick(cloudjob: CloudJobModel) {
        position = presenter.getCloudJobs().indexOf(cloudjob)
        presenter.doEditCloudJob(cloudjob, position)
    }

    override fun onCloudJobDeleteIconClick(cloudjob: CloudJobModel) {
        position = presenter.getCloudJobs().indexOf(cloudjob)
        presenter.doDeleteCloudJob(cloudjob, position)
    }
}
