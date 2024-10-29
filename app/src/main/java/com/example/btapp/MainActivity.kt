package com.example.btapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.btapp.databinding.ActivityMainBinding
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var btApiService: BTApiService
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up NavHostFragment and NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Set up bottom navigation with NavController
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)

        // Set up Retrofit for API calls
        val xmlMapper = XmlMapper()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://www.bt4uclassic.org/webservices/bt4u_webservice.asmx/")
            .addConverterFactory(JacksonConverterFactory.create(xmlMapper))
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
                if (response.isSuccessful) {
                    val routeResponse = response.body()
                    val jsonMapper = ObjectMapper()
                    val jsonString = jsonMapper.writeValueAsString(routeResponse)
                    Log.d("MainActivity", "JSON Response: $jsonString")
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
