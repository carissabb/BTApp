package com.example.btapp.ui.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.btapp.databinding.FragmentMapBinding
import com.tomtom.sdk.map.display.MapOptions
import com.tomtom.sdk.map.display.ui.MapFragment as TomTomMapFragment
import com.tomtom.sdk.map.display.TomTomMap
import com.example.btapp.R

class CustomMapFragment : Fragment() { // renamed to avoid name conflict

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("CustomMapFragment", "MapFragment is loaded")

        val mapOptions = MapOptions(mapKey = "Z8uScvfGWAph6kefoGIYKRNeUYJGhi8G")
        val tomTomMapFragment = TomTomMapFragment.newInstance(mapOptions)

        // Add the TomTomMapFragment to the container in FragmentMap layout
        childFragmentManager.beginTransaction()
            .replace(R.id.map_fragment_container, tomTomMapFragment) // use correct ID from FragmentMap
            .commit()

        // Initialize the map once the TomTomMapFragment is ready
        tomTomMapFragment.getMapAsync { tomtomMap: TomTomMap ->
            Log.d("CustomMapFragment", "TomTom Map is initialized")
            initializeMap(tomtomMap)
        }
    }

    private fun initializeMap(tomTomMap: TomTomMap) {
        // Example coordinates and zoom level
        val latitude = 37.7749
        val longitude = -122.4194
        val zoomLevel = 12.0

        // Center the map
        tomTomMap.centerOn(latitude, longitude)

        // Set the zoom level
        tomTomMap.zoomTo(zoomLevel)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
