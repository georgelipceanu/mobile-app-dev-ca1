package com.example.ca1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ca1.databinding.ActivityCloudjobBinding
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import timber.log.Timber.i

class CloudJobActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCloudjobBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCloudjobBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Timber.plant(Timber.DebugTree())
        i("CloudJob Activity started..")

        binding.btnAdd.setOnClickListener() {
            val cloudjobTitle = binding.cloudjobTitle.text.toString()
            if (cloudjobTitle.isNotEmpty()) {
                i("add Button Pressed: $cloudjobTitle")
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