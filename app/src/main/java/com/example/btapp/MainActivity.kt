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
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.btapp.databinding.ActivityMainBinding
import com.example.btapp.ui.map.MapViewModel
import com.example.btapp.ui.planTrip.PlanTripViewModel
import com.example.btapp.ui.routes.RoutesViewModel
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.tomtom.sdk.map.display.MapOptions
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.Locale


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
    var stopToRoute = mutableMapOf<String, List<ScheduledRoutesResponse>>()

    var weatherCodeReasons = hashMapOf(
        /*0 to "Clear sky detected",
        1 to "Mainly clear sky detected",
        2 to "Partly cloudy sky detected",
        3 to "Overcast sky detected",*/
        45 to "Fog detected",
        48 to "Depositing rime fog detected",
        51 to "Light drizzle detected",
        53 to "Moderate drizzle detected",
        55 to "Dense drizzle detected",
        56 to "Light freezing drizzle detected",
        57 to "Dense freezing drizzle detected",
        61 to "Slight rain detected",
        63 to "Moderate rain detected",
        65 to "Heavy rain detected",
        66 to "Light freezing rain detected",
        67 to "Heavy freezing rain detected",
        71 to "Slight snowfall detected",
        73 to "Moderate snowfall detected",
        75 to "Heavy snowfall detected",
        77 to "Snow grains detected",
        80 to "Slight rain showers detected",
        81 to "Moderate rain showers detected",
        82 to "Violent rain showers detected",
        85 to "Slight snow showers detected",
        86 to "Heavy snow showers detected",
        95 to "Thunderstorm detected",
        96 to "Thunderstorm with slight hail detected",
        99 to "Thunderstorm with heavy hail detected"
    )


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
                Log.d("MainActivity", "route times updated: $it")
                fetchArrivalAndDepartureTimesForRoutes(it)
            }
        }

        planTripViewModel.selectedStopCode.observe(this) { stopCode ->
            stopCode?.let {
                Log.d("MainActivity", "scheduled routes updated: $it")
                fetchScheduledRoutes(it)
            }
        }
        /*
        This section is supposed to handle the changes in the stopcode list fetch the related routes.
        You'll see the corresponsing lines in PlanTripFragment
         */
//        planTripViewModel.selectedStopCode.observe(this) { stopCodes ->
//            stopCodes?.split(",")?.forEach { stopCode ->
//                fetchScheduledRoutes(stopCode.trim())
//            }
//        }

        planTripViewModel.onFetchNearestStops = { latitude, longitude, isStart ->
            fetchNearestStops(latitude, longitude, isStart)
        }

        planTripViewModel.onFetchWeatherData = { latitude, longitude, timestamp ->
            fetchWeatherData(latitude, longitude, timestamp)
        }

        // Call the fetchTrafficFlow function
        lifecycleScope.launch {
            try {
                fetchTrafficFlow()
            } catch (e: Exception) {
                // Handle exceptions
                Log.e("TrafficFlowError", "Error fetching traffic flow: ${e.message}")
            }
        }
        // Call the fetchTrafficIncidents function
        lifecycleScope.launch {
            try {
                fetchTrafficIncidents()
            } catch (e: Exception) {
                // Handle exceptions
                Log.e("TrafficIncidentError", "Error fetching traffic incidents: ${e.message}")
            }
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

    // last call notification function
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

    // traffic flow delay notification
    @SuppressLint("MissingPermission")
    private fun showTrafficSlowdownNotification(currentSpeed: Double, freeFlowSpeed: Double) {
        // this needs to say the name of the routeShortName
        // need to iterate through the stop lat/long to get slowdown data
        // then determine what route the slowdown is on and output it here
        val notificationContent = "Slowdown on routeShortName! Current Speed: $currentSpeed km/h, Free Flow Speed: $freeFlowSpeed km/h"

        val intent = Intent(this, AlertDetails::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_traffic)
            .setContentTitle("Traffic Slowdown Alert")
            .setContentText(notificationContent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        // Show the notification
        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, builder.build())
        }
    }

    // traffic incident notification
    @SuppressLint("MissingPermission")
    private fun showTrafficIncidentNotification(incident: IncidentDetails) {
        // Customize this notification based on incident details
        val notificationContent = """
            Incident ID: ${incident.properties.iconCategory}
        """

        val intent = Intent(this, AlertDetails::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_incident)
            .setContentTitle("Traffic Incident Alert")
            .setContentText(notificationContent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        // Show the notification
        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, builder.build())
        }
    }

    @SuppressLint("MissingPermission")
    private fun showWeatherNotification(delayReason: String) {
        val notificationContent = "Delay expected due to weather: $delayReason"

        val intent = Intent(this, AlertDetails::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_weather)
            .setContentTitle("Weather Alert")
            .setContentText(notificationContent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        // Show the notification
        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, builder.build())
        }
    }

// TOM TOM API FETCH FUNCTIONS
    // fetch traffic flow data
    // this function gets the traffic flow at each stop(lat/long) and alerts if the traffic is going more than 10 under the limit
    private suspend fun fetchTrafficFlow() {
        val mapOptions = MapOptions(mapKey = BuildConfig.TOMTOM_API_KEY)
        val response = api.getFlowData(mapOptions.mapKey, "37.2249991,-80.4249983") // need to loop through route stop lat/longs
        val data = response.flowSegmentData
        Log.d("TrafficFlow", "Speed: ${data.currentSpeed}, Confidence: ${data.confidence}," +
                " freeFlowSpeed:${data.freeFlowSpeed}, freeFlowTravelTime: ${data.freeFlowTravelTime}, " +
                "currentTravelTime: ${data.currentTravelTime}")

        // Slowdown Criteria (e.g., if speed is 10 or more below free-flow speed)
        val slowdownThreshold = 10 // threshold in km/h (can be adjusted)
        if (data.freeFlowSpeed - data.currentSpeed >= slowdownThreshold) {
            // Trigger notification if there's a significant slowdown
            showTrafficSlowdownNotification(data.currentSpeed, data.freeFlowSpeed)
        }
    }
    // fetch traffic
    private suspend fun fetchTrafficIncidents() {
        try {
            val bbox = "37.2249991,-80.4249983,37.2255000,-80.4235000"
            val response = api.getIncidentData(BuildConfig.TOMTOM_API_KEY, bbox)

            // Check if incidents exist
            val incidents = response.incidentData ?: emptyList()
            if (incidents.isEmpty()) {
                Log.d("TrafficIncidents", "No traffic incidents found.")
                return
            }

            // Process incidents
            for (incident in incidents) {
                Log.d("TrafficIncidents","Type: ${incident.properties.iconCategory}, Location: ${incident.geometry.coordinates}")
                showTrafficIncidentNotification(incident) // call display incident notification function
            }
        } catch (e: Exception) {
            Log.e("TrafficIncidentError", "Error fetching traffic incidents: ${e.message}")
        }
    }


// BT API FETCH FUNCTIONS
    private fun fetchBusRoutes() {
        val call = RetrofitInstance.apiService.getCurrentRoutes()

        call.enqueue(object : Callback<List<CurrentRoutesResponse>> {
            override fun onResponse(
                call: Call<List<CurrentRoutesResponse>>,
                response: Response<List<CurrentRoutesResponse>>
            ) {
                if (response.isSuccessful) {
                    val routeResponse = response.body()
                    routeResponse?.forEach { route ->
                        fetchStops(route, stopToRoute)
                    }
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

    private fun fetchStops(route: CurrentRoutesResponse, stopToRoute: MutableMap<String, List<ScheduledRoutesResponse>>){
        val call = route.routeShortName?.let { RetrofitInstance.apiService.getScheduledStopCodes(it) }
        call?.enqueue(object : Callback<List<ScheduledStopCodesResponse>> {
            override fun onResponse(
                call: Call<List<ScheduledStopCodesResponse>>,
                response: Response<List<ScheduledStopCodesResponse>>
            ) {
                if (response.isSuccessful) {
                    val stopCodes = response.body() ?: emptyList()

                    stopCodes.forEach { stopCode ->
                        stopCode.stopCode?.let { fetchScheduledRoutes(it) }
                    }
                    planTripViewModel.setStopToRoutesMap(stopToRoute)
                } else {
                    Log.e("MainActivity", "Failed to fetch stops for ${route.routeShortName}: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<ScheduledStopCodesResponse>>, t: Throwable) {
                Log.e("MainActivity", "Error fetching stops for ${route.routeShortName}: ${t.message}")
            }
        })
    }

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
        val trimmedStopCode = stopCode.substringAfterLast("#").substringBefore(")").trim()

        val call = RetrofitInstance.apiService.getScheduledRoutes(
            trimmedStopCode,
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

                    stopToRoute[stopCode] = scheduledRoutesList
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

    /*private fun fetchAllPlaces() {
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
    }*/


    private fun fetchWeatherData(latitude: Double, longitude: Double, timestamp: Long) {
        val dateTime = SimpleDateFormat("yyyy-MM-dd'T'HH:00", Locale.getDefault()).format(timestamp * 1000)

        val call = RetrofitInstance.weatherApiService.getWeatherForecast(
            latitude,
            longitude,
            start = dateTime,
            end = dateTime
        )

        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()

                    weatherResponse?.hourly?.let { hourly ->
                        val closestIndex = getClosestHourIndex(hourly.time, timestamp)
                        if (closestIndex != -1) {
                            val temperatureFahrenheit =
                                convertCelsiusToFahrenheit(hourly.temperature_2m[closestIndex])
                            val precipitationInches = convertMmToInches(hourly.precipitation[closestIndex])
                            val windSpeedMph = convertKmHToMph(hourly.windspeed_10m[closestIndex])
                            val visibilityMeters = hourly.visibility[closestIndex]
                            val visibilityMiles = convertMetersToMiles(visibilityMeters)
                            val weatherCode = hourly.weathercode[closestIndex]
                            //val humidity = hourly.relativehumidity_2m[closestIndex]
                            //val snowfall = hourly.snowfall[closestIndex]
                            //val cloudCover = hourly.cloudcover[closestIndex]

                            val delayReason = getDelayReason(
                                temperatureFahrenheit,
                                precipitationInches,
                                visibilityMiles,
                                windSpeedMph,
                                weatherCode
                            )

                            if (delayReason != null) {
                                showWeatherNotification(delayReason)
                            } else {

                            }
                        } else {
                            Log.e(
                                "MainActivity",
                                "Error: ${response.code()} - ${response.message()}"
                            )
                        }
                    }
                }
            }
            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                Log.e("MainActivity", "Failed to fetch weather info: ${t.message}")
            }
        })
    }

    private fun getDelayReason(
        temperatureFahrenheit: Double,
        precipitationInches: Double,
        visibilityMiles: Double,
        windSpeedMph: Double,
        weatherCode: Int
    ): String? {
        val lowTemperatureThreshold = 32.0
        val highWindSpeedThreshold = 19.0
        val lowVisibilityThreshold = 1.0
        val significantPrecipitationThreshold = 0.3

        return when {
            weatherCode in weatherCodeReasons -> weatherCodeReasons[weatherCode]
            temperatureFahrenheit <= lowTemperatureThreshold -> "Low temperature detected"
            precipitationInches >= significantPrecipitationThreshold -> "Heavy precipitation detected"
            visibilityMiles <= lowVisibilityThreshold -> "Low visibility detected"
            windSpeedMph >= highWindSpeedThreshold -> "High wind speed detected"
            else -> null
        }
    }

    private fun convertCelsiusToFahrenheit(celsius: Double): Double {
        return (celsius * 9 / 5) + 32
    }

    private fun getClosestHourIndex(hourlyTimes: List<String>, targetTimestamp: Long): Int {
        val targetDateTime = SimpleDateFormat("yyyy-MM-dd'T'HH:00", Locale.getDefault()).format(targetTimestamp * 1000)

        hourlyTimes.forEachIndexed { index, time ->
            if (time == targetDateTime) {
                return index
            }
        }
        return -1
    }

    private fun convertKmHToMph(kmh: Double): Double {
        return kmh * 0.621371
    }

    private fun convertMmToInches(mm: Double): Double {
        return mm * 0.0393701
    }

    private fun convertMetersToMiles(meters: Double): Double {
        return meters * 0.000621371
    }
}
