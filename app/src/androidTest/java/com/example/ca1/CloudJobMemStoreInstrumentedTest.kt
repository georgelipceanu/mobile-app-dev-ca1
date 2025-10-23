package com.example.ca1

import androidx.test.platform.app.InstrumentationRegistry
import com.example.ca1.models.CloudJobMemStore
import com.example.ca1.models.CloudJobModel
import org.junit.Assert
import org.junit.Test

class CloudJobMemStoreInstrumentedTest {
    // Context of the app under test.
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    val cloudJobMemStore = CloudJobMemStore(appContext)
    val job = CloudJobModel(
        id = 42L,
        title = "Model training",
        description = "ResNet50",
        deadline = "2025-10-30",
        CPUType = "Big",
        replicas = 4,
        duration = 90
    )
    @Test
    fun json_save_and_load_functions_after_reboot() {
        cloudJobMemStore.create(job) //.save() is performed here so no need to add again
        val newStore = CloudJobMemStore(appContext) //reboot context
        Assert.assertEquals(0, newStore.cloudJobs.size)
        newStore.load()
        Assert.assertEquals(job, newStore.cloudJobs.get(0)) //since only one job in arraylist, these must match
    }
}