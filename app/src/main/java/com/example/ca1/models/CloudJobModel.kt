package com.example.ca1.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CloudJobModel(var title: String = "",
                         var description: String = "") : Parcelable
