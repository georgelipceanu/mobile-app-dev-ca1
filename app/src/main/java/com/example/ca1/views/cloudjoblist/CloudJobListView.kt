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
    private lateinit var adapter: CloudJobAdapter

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
        adapter = CloudJobAdapter(presenter.getCloudJobs(), this)
        binding.recyclerView.adapter = adapter
        onRefresh()
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                presenter.doSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                presenter.doSearch(newText)
                return true
            }
        })
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

    fun showJobs(list: List<CloudJobModel>) {
        adapter.submitList(list)
    }

    fun onRefresh() {
        showJobs(presenter.getCloudJobs())
    }

    override fun onCloudJobClick(cloudjob: CloudJobModel) {
        presenter.doEditCloudJob(cloudjob)
    }

    override fun onCloudJobDeleteIconClick(cloudjob: CloudJobModel) {
        presenter.doDeleteCloudJob(cloudjob)
    }
}
