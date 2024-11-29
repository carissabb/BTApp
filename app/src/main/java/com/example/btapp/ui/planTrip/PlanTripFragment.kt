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
import com.example.btapp.RetrofitInstance
import com.example.btapp.ScheduledRoutesResponse
import com.example.btapp.databinding.FragmentPlanTripBinding
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

class PlanTripFragment : Fragment() {
    private lateinit var planTripViewModel: PlanTripViewModel
    private lateinit var binding: FragmentPlanTripBinding
    var scheduledRouteList: List<ScheduledRoutesResponse>? = null
    private val scheduledRoutesMap = mutableMapOf<String, List<ScheduledRoutesResponse>>()
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

            if (startDestination.isNotEmpty()) {
                geocoding(startDestination) { latitude, longitude ->
                    planTripViewModel.fetchNearestStopsForDestination(
                        latitude,
                        longitude,
                        true
                    ) // true for start destination
                }
            }

            if (endDestination.isNotEmpty()) {
                geocoding(endDestination) { latitude, longitude ->
                    planTripViewModel.fetchNearestStopsForDestination(
                        latitude,
                        longitude,
                        false
                    ) // false for end destination
                }
            }
        }

        // Initialize the adapters with mutable lists
        val startAdapter = NearestStopsAdapter(mutableListOf())
        val endAdapter = NearestStopsAdapter(mutableListOf())

        /**
         * So right now I have it so that each time 5 stops are taken, fetchScheduled route is called for each one.
         * Go to fetchScheduledRoutes below for more context
         * Commented line should ping the commented out observer in main.
         */
        planTripViewModel.startDestinationNearestStopsList.observe(viewLifecycleOwner) { nearestStops ->
            // Limit to 5 nearest stops
            val limitedStops = nearestStops.take(5) // change for out many results you want
            val stopNames = limitedStops.mapNotNull { stop ->
                stop.stopName?.let { "${it} (#${stop.stopCode})" }
            }
            startAdapter.updateStops(stopNames)

            stopNames.forEach { stopCode ->
                fetchScheduledRoutes(stopCode)
            }
            //planTripViewModel.selectedStopCode.value = stopNames.joinToString(",")
        }

        planTripViewModel.endDestinationNearestStopsList.observe(viewLifecycleOwner) { nearestStops ->
            // Limit to 5 nearest stops
            val limitedStops = nearestStops.take(5) // change for out many results you want
            val stopNames = limitedStops.mapNotNull { stop ->
                stop.stopName?.let { "${it} (#${stop.stopCode})" }
            }
            endAdapter.updateStops(stopNames)

            stopNames.forEach { stopCode ->
                fetchScheduledRoutes(stopCode)
            }
//            planTripViewModel.selectedStopCode.value = stopNames.joinToString(",")

        }

        // Set the RecyclerView adapters after initializing the adapters
        binding.startStopsRecycler.layoutManager = LinearLayoutManager(context)
        binding.startStopsRecycler.adapter = startAdapter
        binding.endStopsRecycler.layoutManager = LinearLayoutManager(context)
        binding.endStopsRecycler.adapter = endAdapter

        routesAdapter = RoutesAdapter(mutableListOf())
        binding.matchingRoutesRecycler.layoutManager = LinearLayoutManager(context)
        binding.matchingRoutesRecycler.adapter = routesAdapter


        //Not sure if this section works yet, theoretically should send the matching routes to the user.
        planTripViewModel.scheduledRoutesList.observe(viewLifecycleOwner){
            routesMap ->
            val startStopCodes = planTripViewModel.startDestinationNearestStopsList.value?.mapNotNull { it.stopCode } ?: emptyList()
            val endStopCodes = planTripViewModel.endDestinationNearestStopsList.value?.mapNotNull { it.stopCode } ?: emptyList()

            val matchingRoutes = findMatchingRoutes(startStopCodes, endStopCodes, routesMap)
            routesAdapter.updateRoutes(matchingRoutes)
        }

    }

    /**
     * Almost identical to the method in Main, just that this one updates a Map which should store the routes related to each stopcode that is put in the map.
     * Just got an idea- if this approach turns out to be a dead end, we could fetch the routes for every single stop on App launch (loop through each route, populate map with each new stop seen),
     * this might make more sense anyways because we wouldn't need to make API calls in the middle of the route finding algorithm.
     * The issue with the method below is that the response object gets a response in the correct format, but just with null values, check logcat
     */
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
                Log.d("Response", " Response: $response") // THIS NEEDS TO GET CORRECT RESPONSE
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

//                  scheduledRouteList = scheduledRoutesList
                    scheduledRoutesMap[stopCode] = scheduledRoutesList
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

    private fun findMatchingRoutes(
        startStopCodes: List<Int>,
        endStopCodes: List<Int>,
        routesMap: List<ScheduledRoutesResponse>
    ): List<ScheduledRoutesResponse> {
        val startRoutes = startStopCodes.flatMap { stopCode ->
            scheduledRoutesMap[stopCode.toString()] ?: emptyList()
        }
        val endRoutes = endStopCodes.flatMap { stopCode ->
            scheduledRoutesMap[stopCode.toString()] ?: emptyList()
        }
        return startRoutes.filter { startRoute ->
            endRoutes.any { it.routeShortName == startRoute.routeShortName }
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