package com.example.btapp.ui.map

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.btapp.BusInfo
import com.example.btapp.R
import com.tomtom.sdk.map.display.marker.BalloonViewAdapter
import com.tomtom.sdk.map.display.marker.Marker

class CustomBalloonViewAdapter(private val context: Context, private val vehicleInfoToMarkerMap: MutableMap<String, BusInfo>) : BalloonViewAdapter {

    @SuppressLint("SetTextI18n")
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

        vehicleNameTextView.text = "Vehicle Number: ${busInfo?.agencyVehicleName}"
        routeShortNameTextView.text = "Route Name: ${busInfo?.routeShortName}"
        percentCapacityTextView.text = "Capacity: ${busInfo?.percentOfCapacity}%"
        latestEventTextView.text = "Last Updated At: ${busInfo?.latestEvent}"
        lastStopNameTextView.text = "Last Stop: ${busInfo?.lastStopName} (# ${busInfo?.stopCode})"

        return view
    }
}