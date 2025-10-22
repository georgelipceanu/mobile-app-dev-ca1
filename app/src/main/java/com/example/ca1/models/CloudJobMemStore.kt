package com.example.ca1.models
import android.content.Context
import kotlinx.serialization.json.Json
import timber.log.Timber.i
import java.io.File

// refs for json serialisation:
// - https://medium.com/@kerry.bisset/kotlin-serialization-json-mistakes-i-made-with-polymorphism-and-more-e8ae367dc90a
// - https://kotlinlang.org/api/kotlinx.serialization/kotlinx-serialization-json/kotlinx.serialization.json/-json/
var lastId = 0L
internal fun getId(): Long {
    return lastId++
}

class CloudJobMemStore(context: Context) : CloudJobStore { // context needed for File(context.filesDir, file)
    val json = Json { prettyPrint = true }
    val cloudJobs = ArrayList<CloudJobModel>()
    val file = File(context.filesDir, "cloudjobs.json") // ref: https://developer.android.com/training/data-storage/app-specific
    override fun findAll(): List<CloudJobModel> {
        return cloudJobs
    }

    override fun create(cloudJob: CloudJobModel) {
        cloudJob.id = getId()
        cloudJobs.add(cloudJob)
        logAll()
        save()
    }

    override fun update(cloudJob : CloudJobModel) {
        var foundCloudJob: CloudJobModel? = cloudJobs.find { p -> p.id == cloudJob.id }
        if (foundCloudJob != null) {
            foundCloudJob.title = cloudJob.title
            foundCloudJob.description = cloudJob.description
            foundCloudJob.deadline = cloudJob.deadline
            foundCloudJob.CPUType = cloudJob.CPUType
            foundCloudJob.replicas = cloudJob.replicas
            logAll()
            save()
        }
    }

    override fun delete(cloudJob : CloudJobModel) {
        var foundCloudJob: CloudJobModel? = cloudJobs.find { p -> p.id == cloudJob.id }
        if (foundCloudJob != null) {
            cloudJobs.remove(foundCloudJob)
            logAll()
            save()
        }
    }

    fun logAll() {
        cloudJobs.forEach{ i("$it") }
    }

    // Serialisation helper functions
    fun load() {
        if (file.exists()) {
            val text = file.readText()
            if (text.isNotBlank()) {
                cloudJobs.clear()
                cloudJobs.addAll(json.decodeFromString(text))
                lastId = if (cloudJobs.isEmpty()) 0L else cloudJobs.maxOf { it.id } + 1 // ref: https://stackoverflow.com/questions/75316930/what-are-the-differences-between-the-maxof-and-max-methods-in-kotlin and https://kotlinlang.org/docs/lambdas.html
            }
        }
    }

    private fun save() {
        file.writeText(json.encodeToString(cloudJobs))
    }
}