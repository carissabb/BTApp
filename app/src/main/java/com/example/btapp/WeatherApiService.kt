package com.example.btapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("forecast")
    fun getWeatherForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("hourly") hourly: String = "temperature_2m,precipitation,windspeed_10m,weathercode,relativehumidity_2m,visibility,snowfall,cloudcover",
        @Query("timezone") timezone: String = "auto",
        @Query("start") start: String,
        @Query("end") end: String
    ): Call<WeatherResponse>
}
