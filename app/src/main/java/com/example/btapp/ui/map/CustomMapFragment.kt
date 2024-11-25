package com.example.btapp.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.btapp.BTApiService
import com.example.btapp.BuildConfig
import com.example.btapp.BusInfo
import com.example.btapp.LocationUtil
import com.example.btapp.databinding.FragmentMapBinding
import com.tomtom.sdk.map.display.MapOptions
import com.tomtom.sdk.map.display.ui.MapFragment as TomTomMapFragment
import com.tomtom.sdk.map.display.TomTomMap
import com.tomtom.sdk.map.display.camera.CameraOptions
import com.tomtom.sdk.location.GeoPoint
import com.example.btapp.R
import com.example.btapp.ui.routes.RoutesViewModel
import com.tomtom.sdk.location.GeoLocation
import com.tomtom.sdk.map.display.gesture.MapPanningListener
import com.tomtom.sdk.map.display.image.ImageFactory
import com.tomtom.sdk.map.display.location.LocationMarkerOptions
import com.tomtom.sdk.map.display.marker.Marker
import com.tomtom.sdk.map.display.marker.MarkerOptions

class CustomMapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var tomTomMap: TomTomMap
    private lateinit var btApiService: BTApiService
    private lateinit var mapViewModel: MapViewModel
    private lateinit var routesViewModel: RoutesViewModel
    private lateinit var tomTomMapFragment: TomTomMapFragment
    private val vehicleInfoToMarkerMap = mutableMapOf<String, BusInfo>()

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
        // Create an instance of LocationUtil
        val locationUtil = LocationUtil(requireContext())

        // Initialize ViewModels
        routesViewModel = ViewModelProvider(requireActivity())[RoutesViewModel::class.java]
        mapViewModel = ViewModelProvider(requireActivity())[MapViewModel::class.java] // add val?
        // Initialize TomTom Map
        val mapOptions = MapOptions(mapKey = BuildConfig.TOMTOM_API_KEY)
        tomTomMapFragment = TomTomMapFragment.newInstance(mapOptions)

        childFragmentManager.beginTransaction()
            .replace(R.id.map_fragment_container, tomTomMapFragment)
            .commit()

        tomTomMapFragment.getMapAsync { map ->
            Log.d("CustomMapFragment", "TomTom Map is initialized")
            tomTomMap = map

            // Setup map functions
            initializeMap()
            setupMapListeners()
            enableGestures()

            // Observe bus info list and add markers
            mapViewModel.busInfoList.observe(viewLifecycleOwner) { busInfoList ->
                busInfoList.forEach { bus ->
                    val routeColor = getRouteColor(bus.routeShortName ?: "")
                    addBusMarker(bus, routeColor)
                }
            }
            // Add user location marker
            locationUtil.getUserCurrentLocation { userLocation ->
                addUserLocationMarker(userLocation)
            }
            // Set the BalloonViewAdapter
            tomTomMapFragment.markerBalloonViewAdapter = CustomBalloonViewAdapter(
                requireContext(),
                vehicleInfoToMarkerMap,
                routesViewModel
            )
        }
    }

    // Helper method to get the route color from RoutesViewModel
    private fun getRouteColor(routeShortName: String): String? {
        Log.d("MapViewModel", "Getting color for route: $routeShortName")
        val color = "#${routesViewModel.routesList.value?.find { it.routeShortName == routeShortName }?.routeColor}"
        Log.d("MapViewModel", "Color for route $routeShortName: $color")
        return color
    }

    // add user location icon
    private fun addUserLocationMarker(location: GeoPoint) {
        val markerOptions = MarkerOptions(
            coordinate = location,
            pinImage = ImageFactory.fromResource(R.drawable.ic_user_location),
            balloonText = "You are here"
        )
        tomTomMap.addMarker(markerOptions)
        Log.d("CustomMapFragment", "User location marker added at Lat=${location.latitude}, Long=${location.longitude}")
    }

    private fun initializeMap() {
        val blacksburg = GeoPoint(37.2249991, -80.4249983)
        val cameraOptions =
            CameraOptions(
                position = blacksburg,
                zoom = 12.0,
                tilt = 45.0,
            )
        tomTomMap.moveCamera(cameraOptions)
    }

    private var selectedMarker: Marker? = null // Declare the selectedMarker variable
    private fun setupMapListeners() {
        // Single Tap Listener
        tomTomMap.addMapClickListener { coordinate: GeoPoint ->
            Log.d("MapEvent", "Single Tap at: $coordinate")
            // Close the balloon if a marker is selected (deselect the marker)
            selectedMarker?.let {
                it.deselect() // Deselect the marker to hide the balloon
            }
            selectedMarker = null // Reset selectedMarker to null
            true // Event consumed
        }

        // Double Tap Listener
        tomTomMap.addMapDoubleClickListener { coordinate: GeoPoint ->
            Log.d("MapEvent", "Double Tap at: $coordinate")
            // Close the balloon if a marker is selected (deselect the marker)
            selectedMarker?.let {
                it.deselect() // Deselect the marker to hide the balloon
            }
            selectedMarker = null // Reset selectedMarker to null
            true // Event consumed
        }

        // Long Click Listener
        tomTomMap.addMapLongClickListener { coordinate: GeoPoint ->
            Log.d("MapEvent", "Long Click at: $coordinate")
            // Close the balloon if a marker is selected (deselect the marker)
            selectedMarker?.let {
                it.deselect() // Deselect the marker to hide the balloon
            }
            selectedMarker = null // Reset selectedMarker to null
            true // Event consumed
        }

        // Panning Listener
        tomTomMap.addMapPanningListener(object : MapPanningListener {
            override fun onMapPanningStarted() {
                Log.d("MapEvent", "Map Panning Started")
            }

            override fun onMapPanningOngoing() {
                Log.d("MapEvent", "Map Panning Ongoing")
            }

            override fun onMapPanningEnded() {
                Log.d("MapEvent", "Map Panning Ended")
            }
        })

        // When a marker is clicked, show its balloon
        tomTomMap.addMarkerClickListener { marker ->
            // If another marker is selected, deselect it
            selectedMarker?.let {
                it.deselect() // Deselect the previous marker
            }
            // Select the clicked marker to show its balloon
            marker.select()
            // Update the selectedMarker to the clicked marker
            selectedMarker = marker
            Log.d("MapEvent", "Marker clicked: ${marker.balloonText}")
        }
    }

    private fun enableGestures() {
        tomTomMap.isZoomEnabled = true
        tomTomMap.isRotationEnabled = true
        tomTomMap.isScrollEnabled = true
        tomTomMap.isTiltEnabled = true
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private fun addBusMarker(bus: BusInfo, routeColor: String?) {
        Log.d("Add Marker Parameter", "Latitude: ${bus.latitude}, Longitude: ${bus.longitude}")
        val position = GeoPoint(bus.latitude ?: 0.0, bus.longitude ?: 0.0)

       // val routeColor = bus.routeShortName?.let { mapViewModel.getRouteColor(it) } ?: "#000000" // Default to black if no route color is provided
        // Get the bus icon drawable
        val originalDrawable = context?.getDrawable(R.drawable.ic_marker)?.mutate()

        // Apply the route color tint if available
        val tintedDrawable = originalDrawable?.let { drawable ->
            try {
                val parsedColor = Color.parseColor(routeColor ?: "#000000") // Default to black if no route color is provided
                drawable.mutate().setTint(parsedColor) // Apply tint
            } catch (e: IllegalArgumentException) {
                Log.e("AddBusMarker", "Invalid route color: $routeColor", e)
            }
            drawable
        }

        // Convert the tinted drawable to a Bitmap
        val tintedBitmap = tintedDrawable?.let { drawable ->
            // Ensure the drawable has intrinsic dimensions
            val width = drawable.intrinsicWidth
            val height = drawable.intrinsicHeight
            if (width <= 0 || height <= 0) {
                drawable.setBounds(0, 0, 24, 24) // Default size
            } else {
                drawable.setBounds(0, 0, width, height)
            }

            // Create a Bitmap with the same size as the drawable
            val bitmap = android.graphics.Bitmap.createBitmap(drawable.bounds.width(), drawable.bounds.height(), android.graphics.Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.draw(canvas) // Draw the drawable onto the canvas
            bitmap
        }

        // add bus marker
        val markerOptions = MarkerOptions(
            coordinate = position,
            pinImage = ImageFactory.fromBitmap(tintedBitmap ?: android.graphics.Bitmap.createBitmap(1, 1, android.graphics.Bitmap.Config.ARGB_8888)), // Fallback if bitmap is null
            balloonText = bus.agencyVehicleName ?: "Unknown"
        )
        val marker = tomTomMap.addMarker(markerOptions)
        Log.d("MarkerInfo", "Adding marker: $marker with BusInfo: $bus")
        vehicleInfoToMarkerMap[bus.agencyVehicleName ?: "Unknown"] = bus
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
