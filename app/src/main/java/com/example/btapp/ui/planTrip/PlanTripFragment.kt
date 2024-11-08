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
                    planTripViewModel.fetchNearestStopsForDestination(latitude, longitude, true) // true for start destination
                }
            }

            if (endDestination.isNotEmpty()) {
                geocoding(endDestination) { latitude, longitude ->
                    planTripViewModel.fetchNearestStopsForDestination(latitude, longitude, false) // false for end destination
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

        // Observe start and end nearest stops lists
        planTripViewModel.startDestinationNearestStopsList.observe(viewLifecycleOwner) { nearestStops ->
            val stopNames = nearestStops.mapNotNull { it.stopName }  // Use mapNotNull to filter out nulls
            startAdapter.updateStops(stopNames)
        }

        planTripViewModel.endDestinationNearestStopsList.observe(viewLifecycleOwner) { nearestStops ->
            val stopNames = nearestStops.mapNotNull { it.stopName }
            endAdapter.updateStops(stopNames)
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
