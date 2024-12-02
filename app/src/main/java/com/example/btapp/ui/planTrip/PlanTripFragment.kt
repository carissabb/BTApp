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
import com.example.btapp.ScheduledRoutesResponse
import com.example.btapp.ScheduledStopCodesResponse
import com.example.btapp.databinding.FragmentPlanTripBinding
import java.text.SimpleDateFormat
import java.util.Locale

class PlanTripFragment : Fragment() {
    private lateinit var planTripViewModel: PlanTripViewModel
    private lateinit var binding: FragmentPlanTripBinding
    var scheduledRouteList: List<ScheduledRoutesResponse>? = null
    private lateinit var routesAdapter: RoutesAdapter


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
         * Commented line should ping the commented out observer in main.
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
        binding.startStopsRecycler.layoutManager = LinearLayoutManager(context)
        binding.startStopsRecycler.adapter = startAdapter
        binding.endStopsRecycler.layoutManager = LinearLayoutManager(context)
        binding.endStopsRecycler.adapter = endAdapter
    }

    private fun isWeekday(): Boolean {
        val dayOfWeek = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK)
        return dayOfWeek != java.util.Calendar.SATURDAY && dayOfWeek != java.util.Calendar.SUNDAY
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

        val matchingRoutes = findMatchingRoutes(startStopCodes, endStopCodes, stopToRoutesMap, routeToStopsMap)
        Log.d("DisplayMatchingRoutes", matchingRoutes.toString())
        routesAdapter.updateRoutes(matchingRoutes)
    }

    private fun findMatchingRoutes(
        startStopCodes: List<Int>,
        endStopCodes: List<Int>,
        stopToRoutesMap: Map<String, List<ScheduledRoutesResponse>>,
        routeToStopsMap: Map<String, List<ScheduledStopCodesResponse>>
    ): List<String> {
        val directMatches = mutableSetOf<String>()
        val transferMatches = mutableSetOf<String>()

        val transitHubGroups = mapOf(
            "Transit Hub" to setOf(8002, 8003, 8004, 8005, 8006, 8007, 8110, 8111, 8113, 8114, 8115, 8116)
        )

        fun areStopsInSameHub(stop1: Int, stop2: Int): Boolean {
            val inSameHub = transitHubGroups.values.any { hub ->
                val isStop1InHub = stop1 in hub
                val isStop2InHub = stop2 in hub
                isStop1InHub && isStop2InHub
            }
            return inSameHub
        }


        val startRoutes = startStopCodes.flatMap { stopCode ->
            stopToRoutesMap[stopCode.toString()] ?: emptyList()
        }
        val endRoutes = endStopCodes.flatMap { stopCode ->
            stopToRoutesMap[stopCode.toString()] ?: emptyList()
        }

        // Case 1: Direct routes
        startRoutes.forEach { startRoute ->
            if (endRoutes.any { it.routeShortName == startRoute.routeShortName }) {
                directMatches.add(startRoute.routeShortName!!)
            }
        }
        // Case 2: Transfer routes
        startRoutes.forEach { startRoute ->
            val startStops = routeToStopsMap[startRoute.routeShortName!!]?.mapNotNull { it.stopCode } ?: emptyList()
            endRoutes.forEach { endRoute ->
                val endStops = routeToStopsMap[endRoute.routeShortName!!]?.mapNotNull { it.stopCode } ?: emptyList()
                val transferStops = startStops.filter { startStop ->
                    endStops.any { endStop ->
                        //Log.d("TransferCheck", "StartStop: $startStop, EndStop: $endStop, Match: ${startStop == endStop}, SameHub: ${areStopsInSameHub(startStop.toInt(), endStop.toInt())}")
                        startStop == endStop || areStopsInSameHub(startStop.toInt(), endStop.toInt())
                    }
                }

                if (transferStops.isNotEmpty()) {
                    val transferStop = transferStops.first()

                    val nextOnboardingStop = endStops.firstOrNull { endStop ->
                        areStopsInSameHub(transferStop.toInt(), endStop.toInt()) || transferStop == endStop
                    }

                    val transferMessage = if (nextOnboardingStop != null) {
                        "${startRoute.routeShortName} -> ${endRoute.routeShortName} (Transfer at $transferStop, Next onboarding at $nextOnboardingStop)"
                    } else {
                        "${startRoute.routeShortName} -> ${endRoute.routeShortName} (Transfer at $transferStop)"
                    }
                    transferMatches.add(transferMessage)
                }
            }
        }
        return (directMatches + transferMatches).toList()
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