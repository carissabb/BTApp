package com.example.btapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory


class MainActivity : AppCompatActivity() {

    private lateinit var btApiService: BTApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://www.bt4uclassic.org/webservices/bt4u_webservice.asmx/")
            .addConverterFactory(JacksonConverterFactory.create()) // Use Jackson for XML
            .build()

        btApiService = retrofit.create(BTApiService::class.java)

        fetchBusRoutes()
    }


    private fun fetchBusRoutes() {
        val call = btApiService.getCurrentRoutes()
        Log.e("MainActivity", "call: $call")
        call.enqueue(object : Callback<GetCurrentRoutesResponse> {
            override fun onResponse(
                call: Call<GetCurrentRoutesResponse>,
                response: Response<GetCurrentRoutesResponse>
            ) {
                Log.e("MainActivity", "Message:$response")
                if (response.isSuccessful) {
                    Log.d("MainActivity", "Response: ${response.body()}")
                } else {
                    Log.e("MainActivity", "Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GetCurrentRoutesResponse>, t: Throwable) {
                Log.e("MainActivity", "Failed to fetch bus routes: ${t.message}")
                Log.e("MainActivity", "call: $call")

            }
        })
    }

}
