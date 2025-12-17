package com.example.ca1.main

import android.app.Application
import com.example.ca1.models.CloudJobMemStore
import com.example.ca1.models.CloudJobFirebaseMemStore
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {
    lateinit var cloudJobs : CloudJobFirebaseMemStore
    override fun onCreate() {
        super.onCreate()
        cloudJobs = CloudJobFirebaseMemStore() // app crashes if lateinit is not used
        Timber.plant(Timber.DebugTree())
        i("Cloud Jobs App started")
    }
}