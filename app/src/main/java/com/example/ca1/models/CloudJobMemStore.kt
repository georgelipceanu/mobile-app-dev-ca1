package com.example.ca1.models
import timber.log.Timber.i

class CloudJobMemStore : CloudJobStore {

    val cloudJobs = ArrayList<CloudJobModel>()

    override fun findAll(): List<CloudJobModel> {
        return cloudJobs
    }

    override fun create(cloudJob: CloudJobModel) {
        cloudJobs.add(cloudJob)
        logAll()
    }

    fun logAll() {
        cloudJobs.forEach{ i("$it") }
    }

}