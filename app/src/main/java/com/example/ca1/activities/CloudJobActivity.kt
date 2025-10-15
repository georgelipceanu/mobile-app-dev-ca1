package com.example.ca1.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ca1.databinding.ActivityCloudjobBinding
import com.example.ca1.models.CloudJobModel
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import timber.log.Timber.i
class CloudJobActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCloudjobBinding
    var cloudJob = CloudJobModel()
    val cloudJobs = ArrayList<CloudJobModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCloudjobBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Timber.plant(Timber.DebugTree())
        i("CloudJob Activity started..")

        binding.btnAdd.setOnClickListener() {
            cloudJob.title = binding.cloudjobTitle.text.toString()
            cloudJob.description = binding.description.text.toString()
            if (cloudJob.title.isNotEmpty()) {
                cloudJobs.add(cloudJob.copy())
                i("add Button Pressed: $cloudJob.title")
                for (i in cloudJobs.indices)
                    { i("Placemark[$i]:${this.cloudJobs[i]}") }
            }
            else {
                Snackbar
                    .make(it,"Please Enter a title", Snackbar.LENGTH_LONG)
                    .show()
            }
        }
        setContentView(binding.root)
    }
}