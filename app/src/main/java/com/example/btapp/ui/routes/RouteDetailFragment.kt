package com.example.btapp.ui.routes

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.btapp.CurrentRoutesResponse
import com.example.btapp.databinding.FragmentRouteDetailBinding

// this is the page for when you click on a specific route
class RouteDetailFragment : Fragment() {
    // Interface definition
    interface RouteDetailListener {
        fun fetchArrivalAndDepartureTimes(routeShortName: String)
    }
    // variable definition
    private var route: CurrentRoutesResponse? = null
    private var _binding: FragmentRouteDetailBinding? = null
    private val binding get() = _binding!!
    private var routeDetailListener: RouteDetailListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is RouteDetailListener) {
            routeDetailListener = context
        } else {
            throw RuntimeException("$context must implement RouteDetailListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRouteDetailBinding.inflate(inflater, container, false)

        // Back button listener
        binding.backArrow.setOnClickListener {
            findNavController().popBackStack()  // This will navigate back
        }

        // Get the route object passed from the previous fragment
        val route: CurrentRoutesResponse? = arguments?.getParcelable<CurrentRoutesResponse>("selectedRoute")

        // Extract the routeShortName from the route object
        val routeShortName = route?.routeShortName


        // Call the function in MainActivity to fetch arrival times for this route
        routeShortName?.let {
            routeDetailListener?.fetchArrivalAndDepartureTimes(it)
        }


        // Display route details
        binding.routeDetailTextView.text = "${route?.routeShortName}"

        // Add more details as needed

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
