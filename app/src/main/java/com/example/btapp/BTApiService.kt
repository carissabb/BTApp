package com.example.btapp

import retrofit2.Call
import retrofit2.http.GET

interface BTApiService {
    @GET("/webservices/bt4u_webservice.asmx/GetCurrentRoutes")
    fun getCurrentRoutes(): Call<CurrentRoutesResponse>
}


