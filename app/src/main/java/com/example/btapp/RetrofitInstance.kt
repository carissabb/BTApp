package com.example.btapp

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

// Set up Retrofit for API calls
object RetrofitInstance {
    private val xmlMapper = XmlMapper()
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://www.bt4uclassic.org/")
        .addConverterFactory(JacksonConverterFactory.create(xmlMapper))
        .build()

    val apiService: BTApiService = retrofit.create(BTApiService::class.java)
//  val apiInterface: BTApiInterface = retrofit.create(BTApiInterface::class.java)
}



