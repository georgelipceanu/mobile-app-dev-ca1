package com.example.ca1.main

import android.app.Application
import com.example.ca1.models.CloudJobMemStore
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {
    val cloudJobs = CloudJobMemStore()

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        i("Cloud Jobs App started")
    }
}