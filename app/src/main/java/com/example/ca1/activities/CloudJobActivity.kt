package com.example.ca1.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ca1.databinding.ActivityCloudjobBinding
import com.example.ca1.models.CloudJobModel
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

class CloudJobActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCloudjobBinding
    var cloudJob = CloudJobModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCloudjobBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Timber.plant(Timber.DebugTree())
        Timber.i("CloudJob Activity started..")

        binding.btnAdd.setOnClickListener() {
            cloudJob.title = binding.cloudjobTitle.text.toString()
            if (cloudJob.title.isNotEmpty()) {
                Timber.i("add Button Pressed: $cloudJob.title")
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