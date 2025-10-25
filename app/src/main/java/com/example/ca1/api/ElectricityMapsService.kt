package com.example.ca1.api

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ElectricityMapsService {
    @GET("v3/carbon-intensity/latest")
    fun getCarbonIntensity(
        @Header("Authorization") authHeader: String,
        @Query("zone") zone: String
    ): CarbonIntensityResponse
}
