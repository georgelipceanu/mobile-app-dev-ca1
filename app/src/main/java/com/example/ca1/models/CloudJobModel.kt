package com.example.ca1.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class CloudJobModel(var id: Long = 0,
                         var title: String = "",
                         var description: String = "",
                         var deadline: String = "",
                         var CPUType: String = "Micro",
                         var replicas: Int = 1,
                         var duration: Int = -1,     // default = no duration/runs indefinitely (eg. web server, database, etc.)
                         var emissions: Double? = null // default null if API doesn't work properly
) : Parcelable
