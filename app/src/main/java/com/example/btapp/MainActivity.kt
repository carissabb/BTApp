package com.example.btapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.btapp.databinding.ActivityMainBinding
import com.example.btapp.ui.routes.RoutesViewModel
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    // declare variables
    private lateinit var binding: ActivityMainBinding
    var currentRoutesList: List<CurrentRoutesResponse>? = null
    private lateinit var routesViewModel: RoutesViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up NavHostFragment and NavController (for bottom navigation)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)

        // Initialize ViewModel (Sends fetchBusRoute data for RoutesViewModel)
        routesViewModel = ViewModelProvider(this)[RoutesViewModel::class.java]

        // call fetch functions
        fetchBusRoutes()
    }

// fetch functions here
    // do we want to put these in BTApiServiceFetch ??
    private fun fetchBusRoutes() {
        val call = RetrofitInstance.apiService.getCurrentRoutes()
        call.enqueue(object : Callback<List<CurrentRoutesResponse>> {
            override fun onResponse(
                call: Call<List<CurrentRoutesResponse>>,
                response: Response<List<CurrentRoutesResponse>>
            ) {
                if (response.isSuccessful) {
                    // could also make a function for this to call in each fetch instead of copy/pasting
                    val routeResponse = response.body()
                    val jsonMapper = ObjectMapper()
                    val jsonString = jsonMapper.writeValueAsString(routeResponse)
                    Log.d("MainActivity", "JSON Response: $jsonString")

                    // convert to object
                    val routesList: List<CurrentRoutesResponse> =
                        jsonMapper.readValue(jsonString, object : TypeReference<List<CurrentRoutesResponse>>() {})

                    currentRoutesList = routesList
                    routesViewModel.setRoutesList(routesList) // Update ViewModel with data
                    routesList.forEach { route ->
                        Log.d("MainActivity", "Route Short Name: ${route.routeShortName}")
                        Log.d("MainActivity", "Route COLOR: #${route.routeColor}")
                    }
                } else {
                    Log.e("MainActivity", "Error: ${response.code()} - ${response.message()}")
                }
            }
            override fun onFailure(call: Call<List<CurrentRoutesResponse>>, t: Throwable) {
                Log.e("MainActivity", "Failed to fetch bus routes: ${t.message}")
            }
        })
    }
    // add rest of fetch functions here
}
