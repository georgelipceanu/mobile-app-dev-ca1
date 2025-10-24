package com.example.ca1.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.view.isEmpty
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ca1.R
import com.example.ca1.adapters.CloudJobAdapter
import com.example.ca1.adapters.CloudJobListener
import com.example.ca1.databinding.ActivityCloudJobsListBinding
import com.example.ca1.main.MainApp
import com.example.ca1.models.CloudJobModel

class CloudJobsListActivity : AppCompatActivity(), CloudJobListener {
    lateinit var app: MainApp
    private lateinit var binding: ActivityCloudJobsListBinding
    private lateinit var adapter: CloudJobAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCloudJobsListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        adapter = CloudJobAdapter(app.cloudJobs.findAll(), this)
        binding.recyclerView.adapter = adapter
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                handleSearch(query)
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                handleSearch(newText)
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
            R.id.item_add -> {
                val launcherIntent = Intent(this, CloudJobActivity::class.java)
                getResult.launch(launcherIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                adapter.submitList(app.cloudJobs.findAll())
            }
        }

    override fun onCloudJobClick(cloudjob: CloudJobModel) {
        val launcherIntent = Intent(this, CloudJobActivity::class.java)
        launcherIntent.putExtra("cloud_job_edit", cloudjob)
        getClickResult.launch(launcherIntent)
    }

    private val getClickResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                adapter.submitList(app.cloudJobs.findAll())
            }
        }

    override fun onCloudJobDeleteIconClick(cloudjob: CloudJobModel) {
        app.cloudJobs.delete(cloudjob)
        adapter.submitList(app.cloudJobs.findAll())
    }

    private fun handleSearch(query: String?) {
        if (query.isNullOrEmpty()) adapter.submitList(app.cloudJobs.findAll())
        else filterCloudJobs(query)
    }

    private fun filterCloudJobs(query: String?) {
        val filteredList = if (!query.isNullOrBlank()) app.cloudJobs.findByTitle(query)
        else app.cloudJobs.findAll()
        adapter.submitList(filteredList)
    }
}
