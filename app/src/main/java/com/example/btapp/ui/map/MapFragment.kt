package com.example.btapp.ui.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.example.btapp.databinding.FragmentMapBinding
import com.tomtom.sdk.map.display.MapOptions
import com.tomtom.sdk.map.display.ui.MapFragment
import com.tomtom.sdk.map.display.ui.MapReadyCallback
import com.tomtom.sdk.map.display.TomTomMap
import com.example.btapp.R


class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
     override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.d("MapFragment", "MapFragment is loaded")

        val mapOptions = MapOptions(mapKey = "Z8uScvfGWAph6kefoGIYKRNeUYJGhi8G")
        val mapFragment = MapFragment.newInstance(mapOptions)

        childFragmentManager.beginTransaction()
            .replace(R.id.map_fragment, mapFragment)
            .commit()

         mapFragment.getMapAsync { tomtomMap: TomTomMap ->
             val tomtomMap = tomtomMap
         }
         _binding = FragmentMapBinding.inflate(inflater, container, false)
         return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Your map initialization logic here
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}