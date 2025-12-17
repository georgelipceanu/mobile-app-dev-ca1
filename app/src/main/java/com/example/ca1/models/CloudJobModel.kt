package com.example.ca1.models

import android.net.Uri
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Parcelize
@Serializable
data class CloudJobModel(var id: Long = 0,
                         var title: String = "",
                         var description: String = "",
                         var deadline: String = "",
                         var CPUType: String = "Micro",
                         var replicas: Int = 1,
                         var duration: Int = -1,     // default = no duration/runs indefinitely (eg. web server, database, etc.)
                         var emissions: Double? = null, // default null if API doesn't work properly
                         var lat : Double = 0.0,
                         var lng: Double = 0.0,
                         var zoom: Float = 0f,
                         var imageUrl: String = "",
                         @Transient // transient for now since i plan on doing cloud based persistence, which means making a separate serializer for URI useless
                         @get:Exclude @set:Exclude // ref: https://firebase.google.com/docs/reference/android/com/google/firebase/firestore/Exclude
                         var image: Uri = Uri.EMPTY
) : Parcelable

@Parcelize
@Serializable
data class DataCentreLocation(var lat: Double = 0.0,
                    var lng: Double = 0.0,
                    var zoom: Float = 0f) : Parcelable