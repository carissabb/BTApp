package com.example.btapp

import retrofit2.Retrofit
import retrofit2.converter.jaxb.JaxbConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://www.bt4uclassic.org/webservices/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(JaxbConverterFactory.create())
        .build()

    val apiService: BTApiService = retrofit.create(BTApiService::class.java)
}

