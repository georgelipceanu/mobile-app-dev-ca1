package com.example.ca1.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val retrofit: ElectricityMapsService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.electricitymap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ElectricityMapsService::class.java)
    }
}
