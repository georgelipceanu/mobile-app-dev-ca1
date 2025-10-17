package com.example.ca1.models

interface CloudJobStore {
    fun findAll(): List<CloudJobModel>
    fun create(cloudJob: CloudJobModel)

    fun update(placemark: CloudJobModel)
}