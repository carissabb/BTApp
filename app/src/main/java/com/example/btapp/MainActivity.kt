package com.example.btapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.jaxb.JaxbConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var btApiService: BTApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://www.bt4uclassic.org/webservices/bt4u_webservice.asmx/") // Update to your base URL
            .addConverterFactory(JaxbConverterFactory.create()) // Add JAXB for XML parsing
            .build()

        btApiService = retrofit.create(BTApiService::class.java)

        fetchBusRoutes()
    }

    private fun fetchBusRoutes() {
        val call = btApiService.getCurrentRoutes()
        call.enqueue(object : Callback<GetCurrentRoutesResponse> {
            override fun onResponse(
                call: Call<GetCurrentRoutesResponse>,
                response: Response<GetCurrentRoutesResponse>
            ) {
                if (response.isSuccessful) {
                    val busRoutes = response.body()?.result
                    Log.d("MainActivity", "Bus Routes: $busRoutes")
                } else {
                    Log.e("MainActivity", "Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GetCurrentRoutesResponse>, t: Throwable) {
                Log.e("MainActivity", "Failed to fetch bus routes: ${t.message}")
            }
        })
    }
}
