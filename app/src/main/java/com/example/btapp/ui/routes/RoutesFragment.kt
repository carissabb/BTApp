package com.example.btapp.ui.routes

import RouteAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.btapp.CurrentRoutesResponse
import com.example.btapp.R
import com.example.btapp.databinding.FragmentRoutesBinding


class RoutesFragment : Fragment() {
    private var _binding: FragmentRoutesBinding? = null
    private val binding get() = _binding!!
    private lateinit var routesViewModel: RoutesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoutesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize ViewModel
        routesViewModel = ViewModelProvider(requireActivity())[RoutesViewModel::class.java]

        // Set up RecyclerView when data is available
        routesViewModel.routesList.observe(viewLifecycleOwner) { routes ->
            val adapter = RouteAdapter(routes) { route ->
                // Navigate to detail page with the clicked route
                val bundle = Bundle().apply { putParcelable("route", route) }
                findNavController().navigate(R.id.routeDetailFragment, bundle)
            }
            binding.routesRecyclerView.layoutManager = LinearLayoutManager(context)
            binding.routesRecyclerView.adapter = adapter
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
