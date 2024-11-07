package com.example.btapp.ui.map

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.DrawableCompat.setTint
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.btapp.BTApiService
import com.example.btapp.BuildConfig
import com.example.btapp.BusInfo
import com.example.btapp.databinding.FragmentMapBinding
import com.tomtom.sdk.map.display.MapOptions
import com.tomtom.sdk.map.display.ui.MapFragment as TomTomMapFragment
import com.tomtom.sdk.map.display.TomTomMap
import com.tomtom.sdk.map.display.camera.CameraOptions
import com.tomtom.sdk.location.GeoPoint
import com.example.btapp.R
import com.example.btapp.ui.routes.RoutesViewModel
import com.google.ar.core.Config
import com.tomtom.sdk.common.graphics.Bitmap
import com.tomtom.sdk.common.graphics.BitmapFactory
import com.tomtom.sdk.map.display.gesture.MapPanningListener
import com.tomtom.sdk.map.display.image.ImageFactory
import com.tomtom.sdk.map.display.marker.MarkerOptions

class CustomMapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var tomTomMap: TomTomMap
    private lateinit var btApiService: BTApiService
    private lateinit var mapViewModel: MapViewModel
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

        val mapViewModel = ViewModelProvider(requireActivity())[MapViewModel::class.java]

        val mapOptions = MapOptions(mapKey = BuildConfig.TOMTOM_API_KEY)
        tomTomMapFragment = TomTomMapFragment.newInstance(mapOptions)

        childFragmentManager.beginTransaction()
            .replace(R.id.map_fragment_container, tomTomMapFragment)
            .commit()

        tomTomMapFragment.getMapAsync { map ->
            Log.d("CustomMapFragment", "TomTom Map is initialized")
            tomTomMap = map

            initializeMap()
            setupMapListeners()
            enableGestures()
            mapViewModel.busInfoList.observe(viewLifecycleOwner) { busInfoList ->
                busInfoList.forEach { bus ->
                    addBusMarker(bus)
                }
            }
            tomTomMapFragment.markerBalloonViewAdapter = CustomBalloonViewAdapter(requireContext(), vehicleInfoToMarkerMap)
        }
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

    private fun setupMapListeners() {
        // Single Tap Listener
        tomTomMap.addMapClickListener { coordinate: GeoPoint ->
            Log.d("MapEvent", "Single Tap at: $coordinate")
            true // Event consumed
        }

        // Double Tap Listener
        tomTomMap.addMapDoubleClickListener { coordinate: GeoPoint ->
            Log.d("MapEvent", "Double Tap at: $coordinate")
            true // Event consumed
        }

        // Long Click Listener
        tomTomMap.addMapLongClickListener { coordinate: GeoPoint ->
            Log.d("MapEvent", "Long Click at: $coordinate")
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
            marker.select()
            Log.d("MapEvent", "Marker clicked: ${marker.balloonText}")
        }
    }

    private fun enableGestures() {
        tomTomMap.isZoomEnabled = true
        tomTomMap.isRotationEnabled = true
        tomTomMap.isScrollEnabled = true
        tomTomMap.isTiltEnabled = true
    }

    private fun addBusMarker(bus: BusInfo) {
        Log.d("Add Marker Parameter", "Latitude: ${bus.latitude}, Longitude: ${bus.longitude}")
        val position = GeoPoint(bus.latitude ?: 0.0, bus.longitude ?: 0.0)

//        // Fetch the route color from mapViewModel
//        val routeColor = bus.routeShortName?.let { mapViewModel.getRouteColor(it) } ?: "#000000" // Default to black if no color
//
//        val coloredIcon = ImageFactory.fromResource(R.drawable.ic_marker).apply {
//            // Always append '#' to the color string
//            val routeHexColor = "#${routeColor}" // Append # regardless
//            // Apply the tint using the correctly formatted color
//            setTint(Color.parseColor(routeHexColor))
//        }
//        // Create a tinted icon manually
//        val originalIcon = BitmapFactory.decodeResource(resources, R.drawable.ic_marker).copy(Bitmap.Config.ARGB_8888, true)
//
//        val paint = Paint().apply {
//            colorFilter = PorterDuffColorFilter(Color.parseColor(routeColor), PorterDuff.Mode.SRC_IN)
//        }
//        val canvas = Canvas(originalIcon)
//        canvas.drawBitmap(originalIcon, 0f, 0f, paint)



        val markerOptions = MarkerOptions(
            coordinate = position,
            pinImage = ImageFactory.fromResource(R.drawable.ic_marker),
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
