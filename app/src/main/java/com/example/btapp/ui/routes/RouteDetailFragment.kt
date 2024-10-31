package com.example.btapp.ui.routes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.btapp.databinding.FragmentRouteDetailBinding
import com.example.btapp.ui.routes.RouteDetailFragmentArgs

class RouteDetailFragment : Fragment() {
    private var _binding: FragmentRouteDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRouteDetailBinding.inflate(inflater, container, false)

        // Get the route object passed from the previous fragment
        val args: RouteDetailFragmentArgs by navArgs()
        val route = args.route

        // Display route details
        binding.routeDetailTextView.text = "Route Name: ${route.routeName}\nRoute Short Name: ${route.routeShortName}"
        // Add more details as needed

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
