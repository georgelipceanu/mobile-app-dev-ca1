package com.example.ca1.views.cloudjoblist

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ca1.R
import com.example.ca1.adapters.CloudJobAdapter
import com.example.ca1.adapters.CloudJobListener
import com.example.ca1.databinding.ActivityCloudJobsListBinding
import com.example.ca1.main.MainApp
import com.example.ca1.models.CloudJobModel
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat

class CloudJobListView : AppCompatActivity(), CloudJobListener {

    lateinit var app: MainApp
    private lateinit var binding: ActivityCloudJobsListBinding
    lateinit var presenter: CloudJobListPresenter
    private lateinit var adapter: CloudJobAdapter
    private lateinit var toggle: ActionBarDrawerToggle // ref: https://developer.android.com/reference/androidx/appcompat/app/ActionBarDrawerToggle, https://www.youtube.com/watch?v=xXfTxcqOUQA (useful video for concept)


    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        binding = ActivityCloudJobsListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)

        toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.nav_open,
            R.string.nav_close
        )
        binding.drawerLayout.addDrawerListener(toggle)         // ref: https://developer.android.com/reference/androidx/drawerlayout/widget/DrawerLayout#addDrawerListener(androidx.drawerlayout.widget.DrawerLayout.DrawerListener), https://developer.android.com/reference/androidx/drawerlayout/widget/DrawerLayout.DrawerListener
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { }
                R.id.nav_maps -> {
                    presenter.doShowCloudJobsMap()
                }
                R.id.nav_add -> {
                    presenter.doAddCloudJob()
                }
                R.id.nav_signout -> {
                    presenter.doSignOut()
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START) // closes drawer back to start side (left), ref: https://developer.android.com/reference/androidx/core/view/GravityCompat
            true // return true once a selection is made
        }

        presenter = CloudJobListPresenter(this)
        app = application as MainApp
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CloudJobAdapter(emptyList(), this) // starts with emptyList() before jobs are loaded from database
        binding.recyclerView.adapter = adapter
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

    fun showJobs(list: List<Pair<String, CloudJobModel>>) {
        adapter.submitList(list)
    }

    fun showError(message: String) {
        // TODO: add snackbar
    }

    override fun onCloudJobClick(id: String, cloudjob: CloudJobModel) {
        presenter.doEditCloudJob(id, cloudjob)
    }

    override fun onCloudJobDeleteIconClick(id: String, cloudjob: CloudJobModel) {
        presenter.doDeleteCloudJob(id)
    }

    override fun onStart() { // UI active, ref: https://developer.android.com/guide/components/activities/activity-lifecycle
        super.onStart()
        presenter.startListening()
    }

    override fun onStop() {
        super.onStop()
        presenter.stopListening()
    }
}
