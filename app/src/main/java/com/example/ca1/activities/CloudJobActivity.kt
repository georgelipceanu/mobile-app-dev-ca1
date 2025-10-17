package com.example.ca1.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.ca1.R
import com.example.ca1.databinding.ActivityCloudjobBinding
import com.example.ca1.main.MainApp
import com.example.ca1.models.CloudJobModel
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber.i
class CloudJobActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCloudjobBinding
    var cloudJob = CloudJobModel()
    var edit = false
    lateinit var app : MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCloudjobBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        app = application as MainApp

        if (intent.hasExtra("cloud_job_edit")) {
            edit = true
            cloudJob = intent.extras?.getParcelable("cloud_job_edit")!!
            binding.cloudjobTitle.setText(cloudJob.title)
            binding.description.setText(cloudJob.description)
            binding.btnAdd.setText(R.string.save_cloud_job)
        }

        binding.btnAdd.setOnClickListener() {
            cloudJob.title = binding.cloudjobTitle.text.toString()
            cloudJob.description = binding.description.text.toString()
            if (cloudJob.title.isEmpty()) {
                Snackbar.make(it,R.string.enter_cloud_job_title, Snackbar.LENGTH_LONG).show()
            } else {
                if (edit) {
                    app.cloudJobs.update(cloudJob.copy())
                } else {
                    app.cloudJobs.create(cloudJob.copy())
                }
            }
            setResult(RESULT_OK)
            finish()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.cloud_job_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_cancel -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}