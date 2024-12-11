package com.example.btapp.ui.routes

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.btapp.ArrivalAndDepartureTimesForRoutesResponse
import com.example.btapp.CurrentRoutesResponse
import com.example.btapp.R
import com.example.btapp.databinding.FragmentRouteDetailBinding
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


// this is the page for when you click on a specific route
class RouteDetailFragment : Fragment() {
    //variable definition
    private var _binding: FragmentRouteDetailBinding? = null
    private val binding get() = _binding!!
    private val routesViewModel: RoutesViewModel by activityViewModels()


    private fun onRouteClicked(routeShortName: String) {
        routesViewModel.selectedRouteShortName.value = routeShortName
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRouteDetailBinding.inflate(inflater, container, false)
        // Set up the RecyclerView
        binding.departureTimesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        // Back button listener
        binding.backArrow.setOnClickListener {
            findNavController().popBackStack()  // This will navigate back
        }

        // Get the route object passed from the previous fragment
        val route: CurrentRoutesResponse? =
            arguments?.getParcelable<CurrentRoutesResponse>("selectedRoute")
        // Display route details
        binding.routeDetailTextView.text = "${route?.routeShortName}"

        binding.routeDetailTextView.setOnClickListener {
            route?.routeShortName?.let { onRouteClicked(it) }
        }

        // Get the selected routeShortName passed from previous fragment
        val selectedRouteShortName = arguments?.getString("selectedRouteShortName") ?: ""

        // Define pattern names for each routeShortName
        val patternNamesForRoutes = when (selectedRouteShortName) {
            "CAS" -> listOf("CAS to Orange", "CAS to Maroon")
            "CRB" -> listOf("CRB", "CRB FR", "CRB LR")
            "CRC" -> listOf("CRC_OB_ALT", "CRC_IB_ALT", "CRC OB", "CRC IB BE", "CRC IB", "CRC IB LR", "CRC OB FR")
            "BLU" -> listOf("Explorer Blue")
            "GLD" -> listOf("Explorer Gold")
            "HDG" -> listOf("HDG", "HDG LR", "HDG FR")
            "HWA" -> listOf("HWA FR", "HWA")
            "HWB" -> listOf("HWB FR", "HWB")
            "HWC" -> listOf("HWC")
            "HXP" -> listOf("HXP FR", "HXP LR", "HXP")
            "NMG" -> listOf("NMG FR", "NMG")
            "NMP" -> listOf("NMP")
            "PHB" -> listOf("PHB LR", "PHB FR", "PHB")
            "PHD" -> listOf("PHD FR", "PHD LR", "PHD")
            "PRG" -> listOf("PRG FR", "PRG")
            "SMA" -> listOf("SMA FR", "SMA")
            "SME" -> listOf("SME")
            "SMS" -> listOf("SMS", "SMS FR")
            "TCR" -> listOf("TCR RT", "TCR")
            "TCP" -> listOf("TCP", "TCP LR")
            "TTT" -> listOf("TTT LR", "TTT FR", "TTT")
            "UCB" -> listOf("UCB LR", "UCB")
            else -> listOf(selectedRouteShortName) // Fallback if no specific patterns are defined
        }

        // to display departure times
        routesViewModel.arrivalDepartureTimesList.observe(viewLifecycleOwner) { timesList ->
            // for matching, not hardcoding DO NOT DELETE COMMENTED CODE
            // Filter the list for the selected route
            //val routeTimes = timesList.filter { it.patternName == selectedRouteShortName }

            // Filter the list to include times with matching pattern names
            val routeTimes = timesList.filter { it.patternName in patternNamesForRoutes }

            // Update the UI with route-specific times
            if (routeTimes.isNotEmpty()) {
                val adapter = RouteTimeAdapter(routeTimes)
                binding.departureTimesRecyclerView.adapter = adapter
            } else {
                Log.e("RouteDetailFragment", "no times available")
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    class RouteTimeAdapter(private val times: List<ArrivalAndDepartureTimesForRoutesResponse>) :
        RecyclerView.Adapter<RouteTimeAdapter.TimeViewHolder>() {

        class TimeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val stopNameText: TextView = itemView.findViewById(R.id.stopNameText)
            val stopCodeText: TextView = itemView.findViewById(R.id.stopCodeText)
            val departureTimeText: TextView = view.findViewById(R.id.departureTimeText)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_time, parent, false)
            return TimeViewHolder(view)
        }

        override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
            val time = times[position]

            // Parse and format departure time
            val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
            val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
            val departureTime = ZonedDateTime.parse(time.calculatedDepartureTime, dateTimeFormatter)
            val formattedTime = departureTime.format(timeFormatter)

            holder.stopNameText.text = time.stopName  // Set the stop name
            holder.stopCodeText.text =  "(#${time.stopCode})" // Set the stop code
            holder.departureTimeText.text = formattedTime // Set departure time
        }
        override fun getItemCount() = times.size
    }
}

