package com.example.btapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// get traffic data from tom tom api
interface TrafficApi {
    // traffic flow data
    @GET("traffic/services/4/flowSegmentData/absolute/10/json")
    suspend fun getFlowData(
        @Query("key") apiKey: String,
        @Query("point") point: String
    ): FlowResponse

    //traffic incident data
    @GET("traffic/services/5/incidentDetails")
    suspend fun getIncidentData(
        @Query("key") apiKey: String,
        @Query("bbox") bbox: String
    ): IncidentResponse
}

// load data classes
data class FlowResponse(
    val flowSegmentData: FlowSegmentData
)
data class FlowSegmentData(
    val currentSpeed: Double,
    val freeFlowSpeed: Double,
    val currentTravelTime: Int,
    val freeFlowTravelTime: Int,
    val confidence: Double
)
data class IncidentResponse(
    val incidentData: List<IncidentDetails>
)
data class IncidentDetails(
    val type: String,
    val geometry: Geometry,
    val properties: IncidentProperties
)
data class Geometry(
    val type: String,
    val coordinates: List<List<Double>>
)
data class IncidentProperties(
    val iconCategory: Int
)

// retrofit Setup
val retrofitTraffic = Retrofit.Builder()
    .baseUrl("https://api.tomtom.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val api = retrofitTraffic.create(TrafficApi::class.java)
