package com.example.btapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper

class MainActivity : AppCompatActivity() {

    private lateinit var btApiService: BTApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val xmlMapper = XmlMapper()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://www.bt4uclassic.org/webservices/bt4u_webservice.asmx/")
            .addConverterFactory(JacksonConverterFactory.create(xmlMapper)) // Use Jackson for XML
            .build()

        btApiService = retrofit.create(BTApiService::class.java)

        fetchBusRoutes()
    }


    private fun fetchBusRoutes() {
        val call = btApiService.getCurrentRoutes()
        call.enqueue(object : Callback<CurrentRoutesResponse> {
            override fun onResponse(
                call: Call<CurrentRoutesResponse>,
                response: Response<CurrentRoutesResponse>
            ) {
                Log.e("MainActivity", "Message:$response")
                if (response.isSuccessful) {
                    val routeResponse = response.body()
                    Log.d("MainActivity", "Response: $routeResponse")
//                    val xmlResponse = response.body()
//                    val jsonMapper = ObjectMapper()
//
//                    // Convert the XML response to JSON
//                    val jsonString = jsonMapper.writeValueAsString(xmlResponse)
//                    Log.d("MainActivity", "JSON Response: $jsonString")
                   // Log.d("MainActivity", "Response: ${response.body()}")
                } else {
                    Log.e("MainActivity", "Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<CurrentRoutesResponse>, t: Throwable) {
                Log.e("MainActivity", "Failed to fetch bus routes: ${t.message}")
            }
        })
    }

}
