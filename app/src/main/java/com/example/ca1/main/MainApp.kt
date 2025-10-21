package com.example.ca1.main

import android.app.Application
import com.example.ca1.models.CloudJobMemStore
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {
    lateinit var cloudJobs : CloudJobMemStore
    override fun onCreate() {
        super.onCreate()
        cloudJobs = CloudJobMemStore(this) // app crashes if lateinit is not used
        cloudJobs.load()
        Timber.plant(Timber.DebugTree())
        i("Cloud Jobs App started")
    }
}