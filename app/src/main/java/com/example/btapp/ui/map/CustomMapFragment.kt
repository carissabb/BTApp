package com.example.btapp.ui.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.btapp.BuildConfig
import com.example.btapp.databinding.FragmentMapBinding
import com.tomtom.sdk.map.display.MapOptions
import com.tomtom.sdk.map.display.ui.MapFragment as TomTomMapFragment
import com.tomtom.sdk.map.display.TomTomMap
import com.tomtom.sdk.map.display.camera.CameraOptions
import com.tomtom.sdk.location.GeoPoint
import com.example.btapp.R

class CustomMapFragment : Fragment() {

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

        val mapOptions = MapOptions(mapKey = BuildConfig.TOMTOM_API_KEY)
        val tomTomMapFragment = TomTomMapFragment.newInstance(mapOptions)

        childFragmentManager.beginTransaction()
            .replace(R.id.map_fragment_container, tomTomMapFragment)
            .commit()

        tomTomMapFragment.getMapAsync { tomtomMap: TomTomMap ->
            Log.d("CustomMapFragment", "TomTom Map is initialized")
            initializeMap(tomtomMap)
        }
    }

    private fun initializeMap(tomTomMap: TomTomMap) {

        val blacksburg = GeoPoint(37.2249991, -80.4249983)
        val cameraOptions =
            CameraOptions(
                position = blacksburg,
                zoom = 10.0,
                tilt = 45.0,
            )
        tomTomMap.moveCamera(cameraOptions)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
