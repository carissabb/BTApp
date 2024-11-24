package com.example.btapp.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.btapp.BusInfo
import com.example.btapp.R
import com.example.btapp.ui.routes.RoutesViewModel
import com.tomtom.sdk.map.display.marker.BalloonViewAdapter
import com.tomtom.sdk.map.display.marker.Marker
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

// displays info when you click on bus
class CustomBalloonViewAdapter(
    private val context: Context,
    private val vehicleInfoToMarkerMap: MutableMap<String, BusInfo>,
    private var routesViewModel: RoutesViewModel
) : BalloonViewAdapter {

    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreateBalloonView(marker: Marker): View {
        val view = LayoutInflater.from(context).inflate(R.layout.custom_balloon_view, null)

        val busInfo = vehicleInfoToMarkerMap[marker.balloonText]

        Log.d("CustomBalloon", "BusInfo for marker: $busInfo")
        Log.d("CustomBalloon", "Marker info for marker: $marker")

        val vehicleNameTextView = view.findViewById<TextView>(R.id.vehicle_name)
        val routeShortNameTextView = view.findViewById<TextView>(R.id.route_short_name)
        val percentCapacityTextView = view.findViewById<TextView>(R.id.percent_capacity)
        val latestEventTextView = view.findViewById<TextView>(R.id.latest_event)
        val lastStopNameTextView = view.findViewById<TextView>(R.id.last_stop_name)
        val imageView = view.findViewById<ImageView>(R.id.balloon_image)


        // parse event data to display just time
        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
        val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
        val formattedTime = try {
            val departureTime = ZonedDateTime.parse(busInfo?.latestEvent, dateTimeFormatter)
            departureTime.format(timeFormatter)
        } catch (e: Exception) {
            Log.e("CustomBalloon", "Error parsing latest event time: ${busInfo?.latestEvent}", e)
            "N/A"
        }

        vehicleNameTextView.text = "#${busInfo?.agencyVehicleName}"
        routeShortNameTextView.text = "${busInfo?.routeShortName} Bus "
        percentCapacityTextView.text = "Capacity: ${busInfo?.percentOfCapacity}%"
        latestEventTextView.text = "Last Updated At: $formattedTime"
        lastStopNameTextView.text = "Last Stop: ${busInfo?.lastStopName} (# ${busInfo?.stopCode})"

        // Apply the route color to the ImageView's tint
        val routeColor = busInfo?.routeShortName?.let { getRouteColor(it) } ?: "#000000" // Default to black if no route color is found
        try {
            val parsedColor = android.graphics.Color.parseColor(routeColor) // Parse the color
            imageView.setColorFilter(parsedColor) // Apply the tint
        } catch (e: IllegalArgumentException) {
            Log.e("CustomBalloon", "Invalid route color: $routeColor", e)
        }

        return view
    }
    // Helper method to get the route color from RoutesViewModel
    private fun getRouteColor(routeShortName: String): String {
        Log.d("MapViewModel", "Getting color for route: $routeShortName")
        val color = "#${routesViewModel.routesList.value?.find { it.routeShortName == routeShortName }?.routeColor}"
        Log.d("MapViewModel", "Color for route $routeShortName: $color")
        return color
    }
}