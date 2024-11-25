package com.example.btapp.ui.planTrip

import RouteAdapter
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
import com.example.btapp.RetrofitInstance.apiService
import com.example.btapp.databinding.FragmentPlanTripBinding
import com.example.btapp.ui.routes.ScheduledRouteAdapter

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

//        planTripViewModel.startDestinationNearestStopsList.observe(viewLifecycleOwner) { nearestStops ->
//            nearestStops.firstOrNull()?.stopCode?.let { stopCode ->
//                planTripViewModel.selectedStopCode.value =
//                    stopCode.toString()  // Trigger fetch for start stop
//            }
//        }

        // Initialize the adapters with mutable lists
        val startAdapter = NearestStopsAdapter(mutableListOf())
        val endAdapter = NearestStopsAdapter(mutableListOf())

        planTripViewModel.startDestinationNearestStopsList.observe(viewLifecycleOwner) { nearestStops ->
            // Limit to 5 nearest stops
            val limitedStops = nearestStops.take(1) // change for out many results you want
            val stopNames = limitedStops.mapNotNull { stop ->
                stop.stopName?.let { "${it} (#${stop.stopCode})" }
            }
            startAdapter.updateStops(stopNames)
        }

        planTripViewModel.endDestinationNearestStopsList.observe(viewLifecycleOwner) { nearestStops ->
            // Limit to 5 nearest stops
            val limitedStops = nearestStops.take(1) // change for out many results you want
            val stopNames = limitedStops.mapNotNull { stop ->
                stop.stopName?.let { "${it} (#${stop.stopCode})" }
            }
            endAdapter.updateStops(stopNames)
        }

        // Set the RecyclerView adapters after initializing the adapters
        binding.startStopsRecycler.layoutManager = LinearLayoutManager(context)
        binding.startStopsRecycler.adapter = startAdapter
        binding.endStopsRecycler.layoutManager = LinearLayoutManager(context)
        binding.endStopsRecycler.adapter = endAdapter  // You can later switch the adapters




//        planTripViewModel.endDestinationNearestStopsList.observe(viewLifecycleOwner) { nearestStops ->
//            nearestStops.firstOrNull()?.stopCode?.let { stopCode ->
//                planTripViewModel.selectedStopCode.value =
//                    stopCode.toString()  // Trigger fetch for end stop
//            }
//        }

//        planTripViewModel.scheduledRoutesList.observe(viewLifecycleOwner) { scheduledRoutes ->
//            val routeNames = scheduledRoutes.mapNotNull { it.routeName }
//            val adapter = ScheduledRouteAdapter(routeNames)  // Create the adapter with the route names
//            binding.stopsRecycler.layoutManager = LinearLayoutManager(context)  // Set the LayoutManager
//            binding.stopsRecycler.adapter = adapter  // Set the adapter to RecyclerView
//        }
//
//        // Initialize the adapters with mutable lists
//        val startAdapter = NearestStopsAdapter(mutableListOf())
//        val endAdapter = NearestStopsAdapter(mutableListOf())
//
//        // observe start and end nearest stops lists
//        planTripViewModel.startDestinationNearestStopsList.observe(viewLifecycleOwner) { nearestStops ->
//            val stopNames = nearestStops.mapNotNull { stop ->
//                stop.stopName?.let { "${it} (#${stop.stopCode})" }
//            }  // Use mapNotNull to filter out nulls
//            startAdapter.updateStops(stopNames)
//        }
//
//        planTripViewModel.endDestinationNearestStopsList.observe(viewLifecycleOwner) { nearestStops ->
//            val stopNames = nearestStops.mapNotNull { stop ->
//                stop.stopName?.let { "${it} (#${stop.stopCode})" }
//            }
//            endAdapter.updateStops(stopNames)
//       }
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