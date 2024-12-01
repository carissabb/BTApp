package com.example.btapp

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.jackson.JacksonConverterFactory

// Set up Retrofit for API calls
object RetrofitInstance {
    private val xmlMapper = XmlMapper()

    // for BT to get bus data
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://www.bt4uclassic.org")
        .addConverterFactory(JacksonConverterFactory.create(xmlMapper))
        .build()

    val apiService: BTApiService = retrofit.create(BTApiService::class.java)


//    // for tomtom to get traffic data
//    private val retrofitTrafficAPI: Retrofit = Retrofit.Builder()
//        .baseUrl("https://api.tomtom.com/")
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//    val trafficApiService: TrafficApi = retrofitTrafficAPI.create(TrafficApi::class.java)

    private val weatherRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/v1/") // Open-Meteo API base URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val weatherApiService: WeatherApiService = weatherRetrofit.create(WeatherApiService::class.java)

}



