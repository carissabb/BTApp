package com.example.btapp.ui.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.btapp.RetrofitInstance
import com.example.btapp.ui.routes.RoutesViewModel
import com.squareup.okhttp.*
import com.tomtom.sdk.map.display.gesture.MapPanningListener
import com.tomtom.sdk.map.display.image.ImageFactory
import com.tomtom.sdk.map.display.marker.Label
import com.tomtom.sdk.map.display.marker.MarkerOptions
import okhttp3.*
import okhttp3.OkHttpClient
import okio.IOException
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import retrofit2.Retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.jackson.JacksonConverterFactory
import java.io.StringReader

class CustomMapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var tomTomMap: TomTomMap
    private lateinit var btApiService: BTApiService

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
        val tomTomMapFragment = TomTomMapFragment.newInstance(mapOptions)

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
            //fetchBusData()
        }
    }

    private fun initializeMap() {

        val blacksburg = GeoPoint(37.2249991, -80.4249983)
        val cameraOptions =
            CameraOptions(
                position = blacksburg,
                zoom = 10.0,
                tilt = 45.0,
                rotation = 90.0,
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
    }

    private fun enableGestures() {
        tomTomMap.isZoomEnabled = true
        tomTomMap.isRotationEnabled = true
        tomTomMap.isScrollEnabled = true
        tomTomMap.isTiltEnabled = true
    }


    /*private fun fetchBusData() {
        btApiService.getCurrentBusInfo().enqueue(Callback<List<BusInfo>> {
            override fun onFailure(call: Call<List<BusInfo>>, t: Throwable) {
                Log.e("CustomMapFragment", "Failed to fetch bus info", t)
            }

            override fun onResponse(
                call: Call<List<BusInfo>>,
                response: Response<List<BusInfo>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { busInfoList ->
                        busInfoList.forEach { busInfo ->
                            Log.d("FetchedBus", "Agency Vehicle Name: ${busInfo.agencyVehicleName}, Latitude: ${busInfo.latitude}, Longitude: ${busInfo.longitude}")
                            // Add your logic to display the bus on the map
                            addBusMarker(busInfo)
                        }
                    }
                } else {
                    Log.e("CustomMapFragment", "Error: ${response.errorBody()?.string()}")
                }
            }
        })
    }*/

    fun addBusMarker(bus: BusInfo) {
        Log.d("Add Marker Parameter", "Latitude: ${bus.latitude}, Longitude: ${bus.longitude}")
        val position = GeoPoint(bus.latitude ?: 0.0, bus.longitude ?: 0.0)
        val markerOptions = MarkerOptions(
            coordinate = position,
            pinImage = ImageFactory.fromResource(R.drawable.ic_marker)
        )
        //markerOptions.label = Label(bus.agencyVehicleName ?: "Unknown Bus") // Adjusting for nullable String
        tomTomMap.addMarker(markerOptions)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
