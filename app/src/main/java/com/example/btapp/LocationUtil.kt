//package com.example.btapp.utils
//
//
//import kotlinx.coroutines.withContext
//import android.content.Context
//import android.location.Location
//import android.os.Looper
//import android.util.Log
//import com.tomtom.sdk.map.display.TomTomMap
//import com.tomtom.sdk.map.display.image.Image
//import com.tomtom.sdk.map.display.image.ImageFactory
//import com.tomtom.sdk.map.display.options.LocationMarkerOptions
//import com.tomtom.sdk.map.display.options.LocationMarkerOptions.Type
//import com.tomtom.sdk.maps.MapOptions
//import com.tomtom.sdk.location.DefaultLocationProviderFactory
//import com.tomtom.sdk.location.GeoLocation
//import com.tomtom.sdk.location.GeoPoint
//import com.tomtom.sdk.location.LocationProvider
//import com.tomtom.sdk.location.LocationProviderConfig
//import com.tomtom.sdk.location.OnLocationUpdateListener
//import com.tomtom.sdk.map.display.camera.CameraOptions
//import com.tomtom.sdk.map.display.location.LocationMarkerOptions
//import kotlinx.coroutines.Dispatchers
//
//class LocationUtil(private val context: Context) {
//
//    private lateinit var locationProvider: LocationProvider
//    private lateinit var tomTomMap: TomTomMap
//
//    // Initialize the location provider
//    fun initializeLocationProvider() {
//        val locationProviderConfig = LocationProviderConfig(
//            minTimeInterval = 250L, // Time interval between location updates in milliseconds
//            minDistance = 20.0    // Minimum distance for location update in meters
//        )
//
//        // Create and configure the location provider
//        locationProvider = DefaultLocationProviderFactory.create(
//            context = context,
//            dispatcher = Dispatchers.Default,  // Use background dispatcher
//            config = locationProviderConfig
//        )
//
//        // Enable location updates
//        locationProvider.enable()
//    }
//
//    // Set TomTomMap object (this would typically come from the fragment or activity)
//    fun setTomTomMap(map: TomTomMap) {
//        this.tomTomMap = map
//        tomTomMap.setLocationProvider(locationProvider)
//
//        // Enable location marker
//        val locationMarkerOptions = LocationMarkerOptions(
//            type = LocationMarkerOptions.Type.Chevron // You can change the type here (Pointer, Circle, etc.)
//        )
//        tomTomMap.enableLocationMarker(locationMarkerOptions)
//
//        // Listen for location updates
//        listenForLocationUpdates()
//    }
//
//    // Listen for location updates and update map
//    private fun listenForLocationUpdates() {
//        val onLocationUpdateListener = OnLocationUpdateListener { location: GeoLocation ->
//            // Handle location updates here (e.g., move map camera, update markers)
//            val geoPoint = GeoPoint(location.latitude, location.longitude)
//
//            // Optionally move the camera to the user's location
//            tomTomMap.moveCamera(
//                CameraOptions(position = geoPoint, zoom = 14.0)
//            )
//
//            // Log the updated location (for debugging)
//            Log.d("LocationUtil", "New Location: Lat: ${location.latitude}, Long: ${location.longitude}")
//        }
//
//        // Add location update listener
//        locationProvider.addOnLocationUpdateListener(onLocationUpdateListener)
//    }
//
//    // Get the current location from the LocationProvider
//    suspend fun getCurrentLocation(): GeoLocation? {
//        return withContext(Dispatchers.IO) {
//            locationProvider.lastKnownLocation
//        }
//    }
//
//    // Disable location updates when no longer needed
//    fun stopLocationUpdates() {
//        locationProvider.disable()
//        locationProvider.close()
//    }
//}
