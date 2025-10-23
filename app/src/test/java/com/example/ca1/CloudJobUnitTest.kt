package com.example.ca1

import com.example.ca1.models.CloudJobModel
import junit.framework.TestCase.assertEquals
import org.junit.Test

internal class CloudJobUnitTest {
    @Test
    fun defaults_are_as_expected() {
        val job = CloudJobModel()
        assertEquals(0L, job.id)
        assertEquals("", job.title)
        assertEquals("", job.description)
        assertEquals("", job.deadline)
        assertEquals("Micro", job.CPUType)
        assertEquals(1, job.replicas)
        assertEquals(-1, job.duration)
    }
}
