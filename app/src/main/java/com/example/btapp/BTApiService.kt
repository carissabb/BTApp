package com.example.btapp

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// get request from BT site to get XML API data

interface BTApiService {
    @GET("/webservices/bt4u_webservice.asmx/GetCurrentRoutes")
    fun getCurrentRoutes(): Call<List<CurrentRoutesResponse>>

    @FormUrlEncoded
    @POST("webservices/bt4u_webservice.asmx/GetArrivalAndDepartureTimesForRoutes")
    fun getArrivalAndDepartureTimes(
    @Field("routeShortNames") routeShortNames: String,
    @Field("noOfTrips") noOfTrips: String,
    @Field("serviceDate") serviceDate: String
    ): Call<List<ArrivalAndDepartureTimesForRoutesResponse>>

    @GET("/webservices/bt4u_webservice.asmx/GetCurrentBusInfo")
    fun getCurrentBusInfo(): Call<List<BusInfo>>

    @GET("/webservices/bt4u_webservice.asmx/GetAllPlaces")
    fun getAllPlaces(): Call<List<AllPlacesResponse>>

    @FormUrlEncoded
    @POST("webservices/bt4u_webservice.asmx/GetNearestStops")
    fun getNearestStops(
        @Field("latitude") latitude: Double,
        @Field("longitude") longitude: Double,
        @Field("noOfStops") noOfStops: String,
        @Field("serviceDate") serviceDate: String
    ): Call<List<NearestStopsResponse>>
}


