package com.example.ca1.models
import timber.log.Timber.i

var lastId = 0L

internal fun getId(): Long {
    return lastId++
}

class CloudJobMemStore : CloudJobStore {

    val cloudJobs = ArrayList<CloudJobModel>()

    override fun findAll(): List<CloudJobModel> {
        return cloudJobs
    }

    override fun create(cloudJob: CloudJobModel) {
        cloudJob.id = getId()
        cloudJobs.add(cloudJob)
        logAll()
    }

    override fun update(cloudJob : CloudJobModel) {
        var foundCloudJob: CloudJobModel? = cloudJobs.find { p -> p.id == cloudJob.id }
        if (foundCloudJob != null) {
            foundCloudJob.title = cloudJob.title
            foundCloudJob.description = cloudJob.description
            logAll()
        }
    }

    fun logAll() {
        cloudJobs.forEach{ i("$it") }
    }

}