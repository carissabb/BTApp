package com.example.btapp.ui.planTrip

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.btapp.ArrivalAndDepartureTimesForRoutesResponse
import com.example.btapp.RetrofitInstance
import com.example.btapp.ScheduledRoutesResponse
import com.example.btapp.ScheduledStopCodesResponse
import com.example.btapp.databinding.FragmentPlanTripBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class PlanTripFragment : Fragment() {
    private lateinit var planTripViewModel: PlanTripViewModel
    private lateinit var binding: FragmentPlanTripBinding
    var scheduledRouteList: List<ScheduledRoutesResponse>? = null
    private lateinit var routesAdapter: RoutesAdapter
    private var userSpecifiedTime: ZonedDateTime? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlanTripBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        planTripViewModel = ViewModelProvider(requireActivity())[PlanTripViewModel::class.java]

        binding.submitTripButton.setOnClickListener {
            val startDestination = binding.startDestination.text.toString()
            val endDestination = binding.endDestination.text.toString()
            var departureDate = binding.departureDatePicker.text.toString().trim()
            var departureTime = binding.departureTimePicker.text.toString()

            if (departureDate.isEmpty()) {
                val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(System.currentTimeMillis())
                departureDate = currentDate.trim()
                Log.d("PlanTripFragment", "Current Date: $departureDate")
            }

            if (departureTime.isEmpty()) {
                val currentTime = System.currentTimeMillis()
                val defaultTimeInMillis = currentTime + (15 * 60 * 1000) // Add 15 minutes
                val defaultTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(defaultTimeInMillis)
                departureTime = defaultTime
            }

            userSpecifiedTime = try {
                val dateTimeString = "$departureDate $departureTime"
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(java.time.ZoneId.of("America/New_York"))
                ZonedDateTime.parse("$dateTimeString EST", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z"))
            } catch (e: Exception) {
                Log.e("PlanTripFragment", "Invalid date/time format: ${e.message}")
                null
            }


            val timestamp = convertToUnixTimestamp(departureDate, departureTime)

            if (timestamp != null) {
                if (startDestination.isNotEmpty()) {
                    geocoding(startDestination) { latitude, longitude ->
                        planTripViewModel.fetchWeather(latitude, longitude, timestamp)
                        planTripViewModel.fetchNearestStopsForDestination(latitude, longitude, true)
                    }
                }

                if (endDestination.isNotEmpty()) {
                    geocoding(endDestination) { latitude, longitude ->
                        planTripViewModel.fetchNearestStopsForDestination(latitude, longitude, false)
                    }
                }
            } else {
                Log.e("PlanTripFragment", "Invalid date or time format.")
            }
        }

        // Initialize the adapters with mutable lists
        val startAdapter = NearestStopsAdapter(mutableListOf())
        val endAdapter = NearestStopsAdapter(mutableListOf())

        routesAdapter = RoutesAdapter(mutableListOf())
        binding.matchingRoutesRecycler.layoutManager = LinearLayoutManager(context)
        binding.matchingRoutesRecycler.adapter = routesAdapter

        /**
         * So right now I have it so that each time 5 stops are taken, fetchScheduled route is called for each one.
         * Go to fetchScheduledRoutes below for more context
         * Commented line should ping the commented out observer in main
         */
        planTripViewModel.startDestinationNearestStopsList.observe(viewLifecycleOwner) { nearestStops ->
            // Limit to 5 nearest stops
            val limitedStops = nearestStops.take(2) // change for out many results you want
            val stopNames = limitedStops.mapNotNull { stop ->
                stop.stopName?.let { "${it} (#${stop.stopCode})" }
            }
            startAdapter.updateStops(stopNames)
            calculateAndDisplayMatchingRoutes()
        }

        planTripViewModel.endDestinationNearestStopsList.observe(viewLifecycleOwner) { nearestStops ->
            // Limit to 5 nearest stops
            val limitedStops = nearestStops.take(2) // change for out many results you want
            val stopNames = limitedStops.mapNotNull { stop ->
                stop.stopName?.let { "${it} (#${stop.stopCode})" }
            }
            endAdapter.updateStops(stopNames)
            calculateAndDisplayMatchingRoutes()

        }

        // Set the RecyclerView adapters after initializing the adapters
        //.startStopsRecycler.layoutManager = LinearLayoutManager(context)
        //binding.startStopsRecycler.adapter = startAdapter
        //binding.endStopsRecycler.layoutManager = LinearLayoutManager(context)
        //binding.endStopsRecycler.adapter = endAdapter
    }

    private fun isWeekday(): Boolean {
        val dayOfWeek = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK)
        return dayOfWeek != java.util.Calendar.SATURDAY && dayOfWeek != java.util.Calendar.SUNDAY
    }

    private fun fetchEarliestDepartureTimeForStop(
        routeShortName: String,
        stopCode: String,
        callback: (String?) -> Unit // Returns the formatted earliest departure time or null
    ) {
        // Call the API for the given routeShortName
        val noOfTrips = "30"
        val serviceDate: LocalDate = LocalDate.now()

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
                    val times = response.body() ?: emptyList()
                    val currentTime = ZonedDateTime.now()
                    val matchingTimes = times.filter { it.stopCode == stopCode }

                    val validTimes = matchingTimes.filter { time ->
                        val departureTime = ZonedDateTime.parse(
                            time.calculatedDepartureTime,
                            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
                        )
                        val userTime = userSpecifiedTime ?: ZonedDateTime.now()
                        departureTime.isAfter(userTime)
                    }

                    val earliestTime = validTimes.minByOrNull { time ->
                        ZonedDateTime.parse(
                            time.calculatedDepartureTime,
                            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
                        )
                    }

                    val formattedTime = earliestTime?.calculatedDepartureTime?.let { rawTime ->
                        val dateTime = ZonedDateTime.parse(
                            rawTime,
                            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
                        )
                        dateTime.format(DateTimeFormatter.ofPattern("h:mm a")) // Format as 12-hour time
                    }
                    callback(formattedTime)
                } else {
                    Log.e("API Error", "Failed to fetch times for route: $routeShortName")
                    callback(null)
                }
            }
            override fun onFailure(call: Call<List<ArrivalAndDepartureTimesForRoutesResponse>>, t: Throwable) {
                Log.e("API Error", "Error fetching times: ${t.message}")
                callback(null)
            }
        })
    }

    private fun calculateAndDisplayMatchingRoutes() {
        val startStopCodes = planTripViewModel.startDestinationNearestStopsList.value?.mapNotNull { it.stopCode } ?: emptyList()
        val endStopCodes = planTripViewModel.endDestinationNearestStopsList.value?.mapNotNull { it.stopCode } ?: emptyList()

        val originalStopToRoutesMap = planTripViewModel.stopToRoute.value ?: emptyMap()
        val routeToStopsMap = planTripViewModel.routeToStops.value ?: emptyMap()

        val stopToRoutesMap = if (isWeekday()) {
            originalStopToRoutesMap.mapValues { (_, routes) ->
                routes.filter { it.routeShortName !in listOf("HWC", "NMP") }
            }
        } else {
            originalStopToRoutesMap
        }

        findMatchingRoutes(
            startStopCodes,
            endStopCodes,
            stopToRoutesMap,
            routeToStopsMap
        ) { matchingRoutes ->
            // This will be executed once all asynchronous calls are complete
            Log.d("DisplayMatchingRoutes", matchingRoutes.toString())
            routesAdapter.updateRoutes(matchingRoutes)
        }
    }

    private fun findMatchingRoutes(
        startStopCodes: List<Int>,
        endStopCodes: List<Int>,
        stopToRoutesMap: Map<String, List<ScheduledRoutesResponse>>,
        routeToStopsMap: Map<String, List<ScheduledStopCodesResponse>>,
        onComplete: (List<String>) -> Unit
    ) {
        val directMatches = mutableSetOf<String>()
        val transferMatches = mutableSetOf<String>()
        val tasksRemaining = mutableListOf<Unit>()

        val transitHubGroups = mapOf(
            "Transit Hub" to setOf(8002, 8003, 8004, 8005, 8006, 8007, 8110, 8111, 8113, 8114, 8115, 8116)
        )

        fun areStopsInSameHub(stop1: Int, stop2: Int): Boolean {
            return transitHubGroups.values.any { hub ->
                (stop1 in hub) && (stop2 in hub)
            }
        }

        val startRoutes = startStopCodes.flatMap { stopCode ->
            stopToRoutesMap[stopCode.toString()] ?: emptyList()
        }

        val endRoutes = endStopCodes.flatMap { stopCode ->
            stopToRoutesMap[stopCode.toString()] ?: emptyList()
        }


        // Case 1: Direct Routes
        startRoutes.forEach { startRoute ->
            val startRouteShortName = startRoute.routeShortName
            if (startRouteShortName != null && endRoutes.any { it.routeShortName == startRouteShortName }) {
                val startStopCode = startStopCodes.firstOrNull()
                val startStopName = startStopCode?.let { code ->
                    routeToStopsMap[startRouteShortName]?.find { it.stopCode!!.toInt() == code }?.stopName
                }

                val endStopCode = endStopCodes.firstOrNull()
                val endStopName = endStopCode?.let { code ->
                    routeToStopsMap[startRouteShortName]?.find { it.stopCode!!.toInt() == code }?.stopName
                }

                tasksRemaining.add(Unit)
                fetchEarliestDepartureTimeForStop(startRoute.routeShortName, startStopCode.toString()) { earliestTime ->
                    if (earliestTime != null) {
                        val directMessage = "$startRouteShortName\n" +
                                "Start Stop: ${startStopName ?: "Unknown Start Stop"} (#${startStopCode ?: "Unknown"})\n" +
                                "End Stop: ${endStopName ?: "Unknown End Stop"} (#${endStopCode ?: "Unknown"})\n" +
                                "Earliest Departure Time: $earliestTime"

                        directMatches.add(directMessage)

                        Log.d("DirectRouteMessage", directMessage)
                    }
                    tasksRemaining.remove(Unit) // Task completed
                    if (tasksRemaining.isEmpty()) onComplete((directMatches + transferMatches).toList())
                }
            }
        }

        // Case 2: Transfer routes
        startRoutes.forEach { startRoute ->
            val startRouteShortName = startRoute.routeShortName
            val startStops = routeToStopsMap[startRouteShortName]?.mapNotNull { it.stopCode } ?: emptyList()
            endRoutes.forEach { endRoute ->
                val endRouteShortName = endRoute.routeShortName
                val endStops = routeToStopsMap[endRouteShortName]?.mapNotNull { it.stopCode } ?: emptyList()
                val transferStops = startStops.filter { startStop ->
                    endStops.any { endStop ->
                        startStop == endStop || areStopsInSameHub(startStop.toInt(), endStop.toInt())
                    }
                }

                if (transferStops.isNotEmpty()) {
                    val transferStopCode = transferStops.first()
                    val transferStopName = routeToStopsMap[startRouteShortName]?.find { it.stopCode == transferStopCode }?.stopName
                    val nextOnboardingStop = endStops.firstOrNull { endStop ->
                        areStopsInSameHub(transferStopCode.toInt(), endStop.toInt()) || transferStopCode == endStop
                    }
                    val nextOnboardingStopName = nextOnboardingStop?.let { endStop ->
                        routeToStopsMap[endRouteShortName]?.find { it.stopCode == endStop }?.stopName
                    }

                    val startStopCode = startStopCodes.firstOrNull()
                    val startStopName = startStopCode?.let { code ->
                        routeToStopsMap[startRouteShortName]?.find { it.stopCode!!.toInt() == code }?.stopName
                    }

                    val endStopCode = endStopCodes.firstOrNull()
                    val endStopName = endStopCode?.let { code ->
                        routeToStopsMap[endRouteShortName]?.find { it.stopCode!!.toInt() == code }?.stopName
                    }

                    tasksRemaining.add(Unit)
                    fetchEarliestDepartureTimeForStop(startRoute.routeShortName!!, startStopCode.toString()) { earliestStartTime ->
                        fetchEarliestDepartureTimeForStop(startRoute.routeShortName, transferStopCode) { earliestTransferTime ->
                            val transferMessage = if (nextOnboardingStop != null) {
                                "$startRouteShortName ➜ $endRouteShortName\n" +
                                        "Start Stop: ${startStopName ?: "Unknown Start Stop"} (#${startStopCode ?: "Unknown"})\n" +
                                        "End Stop: ${endStopName ?: "Unknown End Stop"} (#${endStopCode ?: "Unknown"})\n" +
                                        "Earliest Departure Time: ${earliestStartTime ?: "Unknown"}\n" +
                                        "Transfer Buses: Get off at ${transferStopName ?: "Unknown Stop"} (#${transferStopCode}), " +
                                        "and walk to ${nextOnboardingStopName ?: "nearby stop"} (#${nextOnboardingStop})\n"
                                        //"Earliest Transfer Departure Time: ${earliestTransferTime ?: "Unknown"}"
                            } else {
                                "$startRouteShortName ➜ $endRouteShortName\n" +
                                        "Start Stop: ${startStopName ?: "Unknown Start Stop"} (#${startStopCode ?: "Unknown"})\n" +
                                        "End Stop: ${endStopName ?: "Unknown End Stop"} (#${endStopCode ?: "Unknown"})\n" +
                                        "Earliest Departure Time: ${earliestStartTime ?: "Unknown"}\n" +
                                        "Transfer Buses: Get off at ${transferStopName ?: "Unknown Stop"} (#${transferStopCode})"
                            }
                            transferMatches.add(transferMessage)

                            tasksRemaining.remove(Unit)
                            if (tasksRemaining.isEmpty()) onComplete((directMatches + transferMatches).toList())
                        }
                    }
                }
            }
        }

        if (tasksRemaining.isEmpty()) onComplete((directMatches + transferMatches).toList())
    }



    private fun convertToUnixTimestamp(date: String, time: String): Long? {
        return try {
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val parsedDate = formatter.parse("$date $time")
            parsedDate?.time?.div(1000) // Convert milliseconds to seconds
        } catch (e: Exception) {
            Log.e("PlanTripFragment", "Error parsing date/time: ${e.message}")
            null
        }
    }

    // get location lat / long
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun geocoding(destination: String, onResult: (Double, Double) -> Unit) {
        val geocoder = context?.let { Geocoder(it) }

        geocoder?.getFromLocationName(destination, 1,  object : Geocoder.GeocodeListener {
            override fun onGeocode(addresses: List<Address>) {
                if (addresses.isNotEmpty()) {
                    val latitude = addresses[0].latitude
                    val longitude = addresses[0].longitude
                    // Call the lambda with latitude and longitude
                    onResult(latitude, longitude)
                }
            }

            override fun onError(errorMessage: String?) {
                Log.e("GeocoderError", "Error: $errorMessage")
            }
        })
    }
}