package com.example.btapp

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.btapp.databinding.ActivityMainBinding
import com.example.btapp.ui.map.MapViewModel
import com.example.btapp.ui.planTrip.PlanTripViewModel
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
    var arrivalDepartureTimeList: List<ArrivalAndDepartureTimesForRoutesResponse>? = null
    private lateinit var routesViewModel: RoutesViewModel
    private lateinit var mapViewModel: MapViewModel
    private lateinit var planTripViewModel: PlanTripViewModel
    private val channelId = "BTAppChannel"
    private val notificationId = 1;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permission (for Android 13+)
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        } else {
            showNotification()
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up NavHostFragment and NavController (for bottom navigation)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)

        // Initialize ViewModel (Sends fetchBusRoute data for RoutesViewModel)
        routesViewModel = ViewModelProvider(this)[RoutesViewModel::class.java]
        mapViewModel = ViewModelProvider(this)[MapViewModel::class.java]
        planTripViewModel = ViewModelProvider(this)[PlanTripViewModel::class.java]

        // call fetch functions
        fetchBusRoutes()
        fetchBusData()

        planTripViewModel.onFetchNearestStops = { latitude, longitude, isStart ->
            fetchNearestStops(latitude, longitude, isStart)
        }


    }

    private fun createNotificationChannel() {
        val name = "BTApp Notifications"
        val descriptionText = "Channel for BTApp notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance)
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, show the notification
            showNotification()
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotification() {
        // Create an intent for an activity in your app
        val intent = Intent(this, AlertDetails::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_marker)
            .setContentTitle("Last Call!")
            .setContentText("Last bus leaves in an hour!")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        // Check for permissions and display the notification
        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, builder.build())
        }

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
                        jsonMapper.readValue(
                            jsonString,
                            object : TypeReference<List<CurrentRoutesResponse>>() {})

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

        val call = RetrofitInstance.apiService.getArrivalAndDepartureTimes(
            routeShortName, noOfTrips,
            serviceDate.toString()
        )

        call.enqueue(object : Callback<List<ArrivalAndDepartureTimesForRoutesResponse>> {
            override fun onResponse(
                call: Call<List<ArrivalAndDepartureTimesForRoutesResponse>>,
                response: Response<List<ArrivalAndDepartureTimesForRoutesResponse>>
            ) {
                if (response.isSuccessful) {
                    val arrivalDepartureResponse = response.body()
                    val jsonMapper = ObjectMapper()
                    val jsonString = jsonMapper.writeValueAsString(arrivalDepartureResponse)
                    Log.d("FetchedRoutes", "JSON Response: $jsonString")

                    // convert to object
                    val arrivalDepartureTimesList: List<ArrivalAndDepartureTimesForRoutesResponse> =
                        jsonMapper.readValue(
                            jsonString,
                            object : TypeReference<List<ArrivalAndDepartureTimesForRoutesResponse>>() {})
                    Log.d("FetchedRoutes","JSON Response object: $jsonString")

                    arrivalDepartureTimeList = arrivalDepartureTimesList
                    routesViewModel.setArrivalDepartureTimesList(arrivalDepartureTimesList) // Update ViewModel with data
//                    arrivalDepartureTimesList.forEach { route ->
//                        Log.d("MainActivity", "Route Short Name: ${route.patternName}")
//                    }
                } else {
                    Log.e("MainActivity", "Error: ${response.code()} - ${response.message()}")
                }
            }
            override fun onFailure(
                call: Call<List<ArrivalAndDepartureTimesForRoutesResponse>>,
                t: Throwable
            ) {
                Log.e("MainActivity", "Failed to fetch arrival/departure times: ${t.message}")
            }
        })
    }

    private fun fetchBusData() {
        val call = RetrofitInstance.apiService.getCurrentBusInfo()
        call.enqueue(object : Callback<List<BusInfo>> {
            override fun onResponse(call: Call<List<BusInfo>>, response: Response<List<BusInfo>>) {
                if (response.isSuccessful) {
                    response.body()?.let { busInfoList ->
                        mapViewModel.setBusInfoList(busInfoList)
                        busInfoList.forEach { bus ->
                            Log.d(
                                "FetchedBus",
                                "Agency Vehicle Name: ${bus.agencyVehicleName}, Latitude: ${bus.latitude}, Longitude: ${bus.longitude}"
                            )
                        }
                    }
                } else {
                    Log.e("MainActivity", "Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<BusInfo>>, t: Throwable) {
                Log.e("MainActivity", "Failed to fetch bus info: ${t.message}")
            }
        })
    }

    private fun fetchNearestStops(latitude: Double, longitude: Double, isStart: Boolean) {
        // Define the parameters for the request
        val noOfStops = "5" // Example value, adjust as needed
        val serviceDate: LocalDate = LocalDate.now() // Example date

        val call = RetrofitInstance.apiService.getNearestStops(
            latitude, longitude, noOfStops,
            serviceDate.toString()
        )

        call.enqueue(object : Callback<List<NearestStopsResponse>> {
            override fun onResponse(
                call: Call<List<NearestStopsResponse>>,
                response: Response<List<NearestStopsResponse>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { nearestStopsList ->
                        if(isStart) planTripViewModel.setStartDestinationNearestStopsList(nearestStopsList)
                        else planTripViewModel.setEndDestinationNearestStopsList(nearestStopsList)
                        Log.d("FetchedNearestStops", "JSON Response: ${response.body()}")
                    }
                } else {
                    Log.e("MainActivity", "Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(
                call: Call<List<NearestStopsResponse>>,
                t: Throwable
            ) {
                Log.e("MainActivity", "Failed to fetch arrival/departure times: ${t.message}")
            }
        })
    }

    private fun fetchAllPlaces() {
        val call = RetrofitInstance.apiService.getAllPlaces()
        call.enqueue(object : Callback<List<AllPlacesResponse>> {
            override fun onResponse(call: Call<List<AllPlacesResponse>>, response: Response<List<AllPlacesResponse>>) {
                if (response.isSuccessful) {
                    response.body()?.let { places ->
                        Log.d("MainActivity", "All places fetched: $places")
                    }
                } else {
                    Log.e("MainActivity", "Error fetching places: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<AllPlacesResponse>>, t: Throwable) {
                Log.e("MainActivity", "Failed to fetch places: ${t.message}")
            }
        })
    }
}
