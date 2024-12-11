package com.example.btapp

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.tomtom.sdk.location.GeoPoint

class LocationUtil(private val context: Context) {

    @SuppressLint("MissingPermission")
    fun getUserCurrentLocation(onLocationFetched: (GeoPoint) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val userLocation = GeoPoint(location.latitude, location.longitude)
                onLocationFetched(userLocation)
            } else {
                // Default to a fallback location if unavailable
                val fallbackLocation = GeoPoint(37.2249991, -80.4249983) // Blacksburg, VA
                onLocationFetched(fallbackLocation)
            }
        }.addOnFailureListener {
            // Handle location fetch failure
            val fallbackLocation = GeoPoint(37.2249991, -80.4249983) // Blacksburg, VA
            onLocationFetched(fallbackLocation)
        }
    }
}

