package com.example.btapp

import retrofit2.Call
import retrofit2.http.GET

// get request from BT site to get XML API data

interface BTApiService {
    @GET("/webservices/bt4u_webservice.asmx/GetCurrentRoutes")
    fun getCurrentRoutes(): Call<List<CurrentRoutesResponse>>

    @GET("/webservices/bt4u_webservice.asmx/GetArrivalAndDepartureTimesForRoutes")
    fun getArrivalAndDepartureTimesForRoutes(): Call<ArrivalAndDepartureTimesForRoutesResponse>

    @GET("/webservices/bt4u_webservice.asmx/GetCurrentBusInfo")
    fun getCurrentBusInfo(): Call<List<BusInfo>>
}


