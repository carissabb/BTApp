package com.example.btapp.ui.routes


// delete this, same as RouteDetailFragment





//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.navigation.fragment.navArgs
//import com.example.btapp.databinding.ItemBusInfoBinding
//
//class BusInfoFragment : Fragment() {
//    private var _binding: ItemBusInfoBinding? = null
//    private val binding get() = _binding!!
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = ItemBusInfoBinding.inflate(inflater, container, false)
//
//        // Get the route object passed from the previous fragment
//        val args: BusInfoFragmentArgs by navArgs()
//        val route = args.route
//
//        // Display bus information
//        binding.routeInfoTextView.text = "Route Name: ${route.routeName}\nRoute Short Name: ${route.routeShortName}"
//        // Add more details as needed
//
//        return binding.root
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}
