package com.example.btapp.ui.routes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.btapp.CurrentRoutesResponse
import com.example.btapp.databinding.FragmentRouteDetailBinding

class RouteDetailFragment : Fragment() {
    private var _binding: FragmentRouteDetailBinding? = null
    private val binding get() = _binding!!

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
        val route: CurrentRoutesResponse? = arguments?.getParcelable<CurrentRoutesResponse>("route")

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
