package com.example.btapp

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
//import com.example.btapp.ui.routes.RouteDetailViewModel
import com.example.btapp.ui.routes.RoutesViewModel
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale
import kotlin.time.Duration

class MainActivity : AppCompatActivity(){
    // declare variables
    private lateinit var binding: ActivityMainBinding
    var currentRoutesList: List<CurrentRoutesResponse>? = null
    var arrivalDepartureTimeList: List<ArrivalAndDepartureTimesForRoutesResponse>? = null
    var scheduledRouteList: List<ScheduledRoutesResponse>? = null
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
            //showNotification()
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
        routesViewModel.selectedRouteShortName.observe(this) { routeShortName ->
            routeShortName?.let {
                Log.d("MainActivity", "route times updated: $it") // Add a log to verify
                fetchArrivalAndDepartureTimesForRoutes(it)
            }
        }

        planTripViewModel.selectedStopCode.observe(this) { stopCode ->
            stopCode?.let {
                Log.d("MainActivity", "scheduled routes updated: $it") // Add a log to verify
                fetchScheduledRoutes(it)
            }
        }

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
            //showNotification()
        }
    }

    @SuppressLint("MissingPermission")
    private fun scheduleNotificationWithDelay(delayInMillis: Long, routeName: String) {
        // Use a Handler to introduce the delay
        Handler(Looper.getMainLooper()).postDelayed({
            showNotification(routeName)
        }, delayInMillis)
    }


    @SuppressLint("MissingPermission")
    private fun showNotification(routeName: String) {
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
            .setContentText("Last bus on $routeName leaves in an hour!")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        // Check for permissions and display the notification
        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, builder.build())
        }
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
                    routesViewModel.setRoutesList(routesList) // Update routesViewModel with data
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
        val noOfTrips = "100" // Example value, adjust as needed
        val serviceDate: LocalDate = LocalDate.now() // get today's date

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

                    var latestTime: LocalTime? = null
                    var routeName: String? = null

                    for (responseItem in arrivalDepartureTimeList!!){
                        val routeNotes = responseItem.routeNotes
                        routeName = responseItem.patternName
                        val timeRegex = Regex("""(\d{1,2}):(\d{2})\s?([APMapM]{2})""")
                        val matchResult = timeRegex.find(routeNotes ?: "")

                        matchResult?.let { match ->
                            val hour = match.groupValues[1].toInt()
                            val minute = match.groupValues[2].toInt()
                            val amPm = match.groupValues[3].uppercase()
                            val timeString: String = if (amPm == "PM" && hour < 12) {
                                "${hour + 12}:$minute"
                            } else if (amPm == "AM" && hour == 12) {
                                "00:$minute"
                            } else {
                                "$hour:$minute"
                            }
                            val time = LocalTime.parse(timeString)
                            if (latestTime == null || time.isAfter(latestTime)) {
                                latestTime = time
                            }
                        }
                    }
                    latestTime?.let { latest ->
                        val notificationTime = latest.minusHours(1)
                        val now = LocalTime.now()

                        if (notificationTime.isAfter(now)){
                            val delay = now.until(notificationTime, ChronoUnit.MILLIS)
                            //Hardcode a 0 if you want to test with notification appearing immediately
                            scheduleNotificationWithDelay(delay, routeName ?: "Unknown Route")
                        }
                    }
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

    private fun fetchScheduledRoutes(stopCode: String) {
        // Define the parameters for the request
        val serviceDate: LocalDate = LocalDate.now() // get today's date

        val call = RetrofitInstance.apiService.getScheduledRoutes(
            stopCode,
            serviceDate.toString()
        )

        call.enqueue(object : Callback<List<ScheduledRoutesResponse>> {
            override fun onResponse(
                call: Call<List<ScheduledRoutesResponse>>,
                response: Response<List<ScheduledRoutesResponse>>
            ) {
                if (response.isSuccessful) {
                    val inputResponse = response.body()
                    val jsonMapper = ObjectMapper()
                    val jsonString = jsonMapper.writeValueAsString(inputResponse)
                    Log.d("FetchScheduledRoutes", "JSON Response: $jsonString")

                    // convert to object
                    val scheduledRoutesList: List<ScheduledRoutesResponse> =
                        jsonMapper.readValue(
                            jsonString,
                            object :
                                TypeReference<List<ScheduledRoutesResponse>>() {})
                    Log.d("FetchedScheduledRoutes", "JSON Response object: $jsonString")

                    scheduledRouteList = scheduledRoutesList
                    planTripViewModel.setScheduledRoutesList(scheduledRoutesList) // Update ViewModel with data
                } else {
                    Log.e("FetchedScheduledRoutes", "Error: ${response.code()} - ${response.message()}")
                }
            }
            override fun onFailure(
                call: Call<List<ScheduledRoutesResponse>>,
                t: Throwable
            ) {
                Log.e("FetchedScheduledRoutes", "Failed to fetch scheduled routes: ${t.message}")
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
