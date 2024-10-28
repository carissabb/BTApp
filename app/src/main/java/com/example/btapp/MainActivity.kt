package com.example.btapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.btapp.ui.map.MapFragment
import com.example.btapp.ui.planTrip.PlanTripFragment
import com.example.btapp.ui.routes.RoutesFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.btapp.databinding.AppBarMainBinding
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI


class MainActivity : AppCompatActivity() {

    private lateinit var btApiService: BTApiService
    private lateinit var binding: AppBarMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AppBarMainBinding.inflate(layoutInflater)
        //setContentView(binding.root) //R.layout.activity_main
        setContentView(R.layout.activity_main)

        // for nav menu
        // Set up NavHostFragment and NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Set up bottom navigation with NavController
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        NavigationUI.setupWithNavController(bottomNav, navController) //binding.bottomNavigation


        // convert xml to json for api processing
        val xmlMapper = XmlMapper()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://www.bt4uclassic.org/webservices/bt4u_webservice.asmx/")
            .addConverterFactory(JacksonConverterFactory.create(xmlMapper)) // Use Jackson for XML
            .build()

        btApiService = retrofit.create(BTApiService::class.java)

        fetchBusRoutes()
    }

    // get current routes
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
                    val jsonMapper = ObjectMapper()
                    Log.d("MainActivity", "Response: $routeResponse")

                    // Convert the XML response to JSON
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

    // get arrival/departure times for buses
    private fun fetchArrivalAndDepartureTimesForRoutes(){

    }

}
