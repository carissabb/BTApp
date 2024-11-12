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
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.btapp.databinding.FragmentPlanTripBinding

class PlanTripFragment : Fragment() {
    private lateinit var planTripViewModel: PlanTripViewModel
    private lateinit var binding: FragmentPlanTripBinding

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

        binding.startNearestStopsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.endNearestStopsRecycler.layoutManager = LinearLayoutManager(requireContext())

        binding.startNearestStopsRecycler.adapter = startAdapter
        binding.endNearestStopsRecycler.adapter = endAdapter


        fun checkIfStopIsCorrectRoute(stopCode: String): Boolean {

            // I want to check the number of stops from the current stop to the destination stop. The route with the shortest number of stops will be the best route
           return stopCode.toBoolean() //in validRouteCodes // `validRouteCodes` could be a list of codes for the route you're trying to take.
        }

        // Observe start and end nearest stops lists
        planTripViewModel.startDestinationNearestStopsList.observe(viewLifecycleOwner) { nearestStops ->
            val stopNames = nearestStops.mapNotNull { stop ->
                stop.stopName?.let { "${it} (#${stop.stopCode})" }
            }  // Use mapNotNull to filter out nulls
            startAdapter.updateStops(stopNames)

            if (nearestStops.isNotEmpty()) {
                var bestStopCode: String? = null
                var bestRouteMatch: Boolean =
                    false // You can track whether the stop matches your route requirements

                // Iterate through all the nearest stops
                for (i in nearestStops.indices) {
                    val stopCode = (nearestStops[i].stopCode).toString()
                    // Check if this stop is on the correct route or closest to the destination
                    val isCorrectRoute = checkIfStopIsCorrectRoute(stopCode) // You define this function

                    // If it's a correct route, check if it is closer or better for the trip
                    if (isCorrectRoute) {
                        bestStopCode = stopCode // You can also add additional logic to select the best stop (e.g., proximity, travel time)
                        bestRouteMatch = true
                        break // Stop once you find the best match
                    }
                }

                // Update the selected stop code if a valid stop is found
                if (bestRouteMatch) {
                    planTripViewModel.selectedStopCode.value = bestStopCode
                }
            }
        }

        planTripViewModel.endDestinationNearestStopsList.observe(viewLifecycleOwner) { nearestStops ->
            val stopNames = nearestStops.mapNotNull { stop ->
                stop.stopName?.let { "${it} (#${stop.stopCode})" }
            }
            endAdapter.updateStops(stopNames)

            if (nearestStops.isNotEmpty()) {
                var bestStopCode: String? = null
                var bestRouteMatch: Boolean =
                    false // You can track whether the stop matches your route requirements

                // Iterate through all the nearest stops
                for (i in nearestStops.indices) {
                    val stopCode = (nearestStops[i].stopCode).toString()
                    // Check if this stop is on the correct route or closest to the destination
                    val isCorrectRoute = checkIfStopIsCorrectRoute(stopCode) // You define this function

                    // If it's a correct route, check if it is closer or better for the trip
                    if (isCorrectRoute) {
                        bestStopCode = stopCode // You can also add additional logic to select the best stop (e.g., proximity, travel time)
                        bestRouteMatch = true
                        break // Stop once you find the best match
                    }
                }

                // Update the selected stop code if a valid stop is found
                if (bestRouteMatch) {
                    planTripViewModel.selectedStopCode.value = bestStopCode
                }
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun geocoding(destination: String, onResult: (Double, Double) -> Unit) {
        val geocoder = context?.let { Geocoder(it) }

        geocoder?.getFromLocationName(destination, 1, @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        object : Geocoder.GeocodeListener {
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

    interface GeocodingCallback {
        fun onGeocodeSuccess(latitude: Double, longitude: Double)
        fun onGeocodeError(errorMessage: String?)
    }

