package com.example.ca1

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ca1.models.CloudJobMemStore
import com.example.ca1.models.CloudJobModel
import com.example.ca1.models.lastId
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.coroutines.Continuation

class CloudJobMemStoreInstrumentedTest {
    // Context of the app under test.
    lateinit var appContext : Context
    lateinit var cloudJobMemStore : CloudJobMemStore
    val job = CloudJobModel(
        id = 42L,
        title = "Model training",
        description = "ResNet50",
        deadline = "2025-10-30",
        CPUType = "Big",
        replicas = 4,
        duration = 90
    )

    @Before
    fun setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        cloudJobMemStore = CloudJobMemStore(appContext)
        lastId = 0
        cloudJobMemStore.file.delete()
    }

    @Test
    fun json_save_and_load_functions_after_reboot() {
        cloudJobMemStore.create(job) //.save() is performed here so no need to add again
        val newStore = CloudJobMemStore(appContext) //reboot context
        Assert.assertEquals(0, newStore.cloudJobs.size)
        newStore.load()
        Assert.assertEquals(job, newStore.cloudJobs.get(0)) //since only one job in arraylist, these must match
    }

    @Test
    fun ids_scale_as_expected() {
        val newStore = CloudJobMemStore(appContext) //reboot context
        val job1 = CloudJobModel()
        newStore.create(job1)
        Assert.assertEquals(0L, job1.id)
        val job2 = CloudJobModel()
        newStore.create(job2)
        Assert.assertEquals(1L, job2.id)
    }
}