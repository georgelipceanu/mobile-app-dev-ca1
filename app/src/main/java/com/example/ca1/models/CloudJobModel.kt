package com.example.ca1.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class CloudJobModel(var id: Long = 0,
                         var title: String = "",
                         var description: String = "") : Parcelable
