package com.example.btapp

import RouteAdapter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.RecyclerView
import com.example.btapp.databinding.ActivityMainBinding
import com.example.btapp.ui.map.CustomMapFragment
import com.example.btapp.ui.map.MapViewModel
import com.example.btapp.ui.routes.RouteDetailFragment
import com.example.btapp.ui.routes.RoutesViewModel
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

class MainActivity : AppCompatActivity() , RouteDetailFragment.RouteDetailListener{
    // declare variables
    private lateinit var binding: ActivityMainBinding
    var currentRoutesList: List<CurrentRoutesResponse>? = null
    var arrivalAndDepartureTimesList: List<ArrivalAndDepartureTimesForRoutesResponse>? = null
    var busInfoList: List<BusInfo>? = null
    private lateinit var routesViewModel: RoutesViewModel
    private lateinit var mapViewModel: MapViewModel

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
        mapViewModel = ViewModelProvider(this)[MapViewModel::class.java]

        // call fetch functions
        fetchBusRoutes()
        //getArrivalAndDepartureTimesForRoutes(route)
        fetchBusData()

    }
     override fun fetchArrivalAndDepartureTimes(routeShortName: String) {
        fetchArrivalAndDepartureTimesForRoutes(routeShortName)
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
                    Log.d("FetchedRoutes", "JSON Response: $jsonString")

                    // convert to object
                    val routesList: List<CurrentRoutesResponse> =
                        jsonMapper.readValue(jsonString, object : TypeReference<List<CurrentRoutesResponse>>() {})

                    currentRoutesList = routesList
                    routesViewModel.setRoutesList(routesList) // Update ViewModel with data
//                    routesList.forEach { route ->
//                        Log.d("MainActivity", "Route Short Name: ${route.routeShortName}")
//                        Log.d("MainActivity", "Route COLOR: #${route.routeColor}")
//                    }
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
    private fun fetchArrivalAndDepartureTimesForRoutes(routeShortName: String) {
        // Define the parameters for the request
        val noOfTrips = "6" // Example value, adjust as needed
        val serviceDate: LocalDate = LocalDate.now() // Example date

        val call = RetrofitInstance.apiService.getArrivalAndDepartureTimes(routeShortName, noOfTrips,
            serviceDate.toString()
        )

        call.enqueue(object : Callback<List<ArrivalAndDepartureTimesForRoutesResponse>> {
            override fun onResponse(
                call: Call<List<ArrivalAndDepartureTimesForRoutesResponse>>,
                response: Response<List<ArrivalAndDepartureTimesForRoutesResponse>>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()
                    Log.d("FetchedRoutes", "JSON Response: $data")


//                    response.body()?.let { data ->
//                        val arrivalTime = data.arrivalTime
//                        val departureTime = data.departureTime
//                        updateUIWithTimes(arrivalTime, departureTime)
                    //}
                } else {
                   Log.e("MainActivity", "Error: ${response.code()} - ${response.message()}")
            }
            }

            override fun onFailure(call: Call<List<ArrivalAndDepartureTimesForRoutesResponse>>, t: Throwable) {
                Log.e("MainActivity", "Failed to fetch arrival times: ${t.message}")
            }
        })
    }


//    private fun fetchArrivalAndDepartureTimesForRoutes(routeShortName: String) {
//            // Prepare the request with the routeShortName
//            val request = ArrivalDepartureRequest(routeShortName = routeShortName)
//            // Call the API to fetch arrival and departure times
//            val call = RetrofitInstance.apiService.getArrivalAndDepartureTimesForRoutes(request)
//            call.enqueue(object : Callback<List<ArrivalAndDepartureTimesForRoutesResponse>> {
//                override fun onResponse(
//                    call: Call<List<ArrivalAndDepartureTimesForRoutesResponse>>,
//                    response: Response<List<ArrivalAndDepartureTimesForRoutesResponse>>
//                ) {
//                if (response.isSuccessful) {
//                    val timeList = response.body()
//                    // Update the ViewModel or directly pass to the fragment if needed
//                    // For example, store in arrivalAndDepartureTimesList or use other methods
//                    arrivalAndDepartureTimesList = timeList
//                    Log.d("FetchedArrivalTimes", "Times for ${routeShortName}: $timeList")
//                } else {
//                    Log.e("MainActivity", "Error: ${response.code()} - ${response.message()}")
//                }
//            }
//
//            override fun onFailure(call: Call<List<ArrivalAndDepartureTimesForRoutesResponse>>, t: Throwable) {
//                Log.e("MainActivity", "Failed to fetch arrival times: ${t.message}")
//            }
//        })
//    }
//    private fun fetchArrivalAndDepartureTimesForRoutes() {
//        val call = RetrofitInstance.apiService.getArrivalAndDepartureTimesForRoutes()
//        call.enqueue(object : Callback<List<ArrivalAndDepartureTimesForRoutesResponse>> {
//            override fun onResponse(
//                call: Call<List<ArrivalAndDepartureTimesForRoutesResponse>>,
//                response: Response<List<ArrivalAndDepartureTimesForRoutesResponse>>
//            ) {
//                if (response.isSuccessful) {
//                    // could also make a function for this to call in each fetch instead of copy/pasting
//                    val timeResponse = response.body()
//                    val jsonMapper = ObjectMapper()
//                    val jsonString = jsonMapper.writeValueAsString(timeResponse)
//                    Log.d("FetchedArriveDepart", "JSON Response: $jsonString")
//
//                    // convert to object
//                    val timeList: List<ArrivalAndDepartureTimesForRoutesResponse> =
//                        jsonMapper.readValue(jsonString, object : TypeReference<List<ArrivalAndDepartureTimesForRoutesResponse>>() {})
//
//                    arrivalAndDepartureTimesList = timeList
//                    routesViewModel.setArrivalAndDepartureTimeList(timeList) // Update ViewModel with data
//
//                } else {
//                    Log.e("FetchedArriveDepart", "Error: ${response.code()} - ${response.message()}")
//                }
//            }
//            override fun onFailure(call: Call<List<ArrivalAndDepartureTimesForRoutesResponse>>, t: Throwable) {
//                Log.e("FetchedArriveDepart", "Failed to fetch Arrival and Departure Times for Routes: ${t.message}")
//            }
//        })
//    }

    private fun fetchBusData() {
        val call = RetrofitInstance.apiService.getCurrentBusInfo()
        call.enqueue(object : Callback<List<BusInfo>> {
            override fun onResponse(call: Call<List<BusInfo>>, response: Response<List<BusInfo>>) {
                if (response.isSuccessful) {
                    response.body()?.let { busInfoList ->
                        mapViewModel.setBusInfoList(busInfoList)
                        busInfoList.forEach { bus ->
                            Log.d("FetchedBus", "Agency Vehicle Name: ${bus.agencyVehicleName}, Latitude: ${bus.latitude}, Longitude: ${bus.longitude}")
                        }
                    }
                    //for color
                    //mapViewModel.getRouteColor(currentRoutesList.toString())// Update ViewModel with data

                } else {
                    Log.e("MainActivity", "Error: ${response.code()} - ${response.message()}")
                }
                /*if (response.isSuccessful) {
                    response.body()?.let { busInfoList ->
                        this@MainActivity.busInfoList = busInfoList // Store fetched bus info
                        busInfoList.forEach { bus ->
                            Log.d("FetchedBus", "Agency Vehicle Name: ${bus.agencyVehicleName}, Latitude: ${bus.latitude}, Longitude: ${bus.longitude}")
                            val customMapFragment = supportFragmentManager.findFragmentById(R.id.nav_map) as? CustomMapFragment
                            customMapFragment?.addBusMarker(bus)
                        }
                    }
                } else {
                    Log.e("MainActivity", "Error: ${response.code()} - ${response.message()}")
                }*/
            }

            override fun onFailure(call: Call<List<BusInfo>>, t: Throwable) {
                Log.e("MainActivity", "Failed to fetch bus info: ${t.message}")
            }
        })
    }

}
