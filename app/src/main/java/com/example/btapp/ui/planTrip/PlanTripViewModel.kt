package com.example.btapp.ui.planTrip

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.btapp.NearestStopsResponse

class PlanTripViewModel : ViewModel() {

    var onFetchNearestStops: ((Double, Double, Boolean) -> Unit)? = null

    fun fetchNearestStopsForDestination(latitude: Double, longitude: Double, isStart: Boolean) {
        // Call the callback in MainActivity to trigger fetching nearest stops
        onFetchNearestStops?.invoke(latitude, longitude, isStart)
    }

    private val _startDestinationNearestStopsList = MutableLiveData<List<NearestStopsResponse>>()
    val startDestinationNearestStopsList: LiveData<List<NearestStopsResponse>> get() = _startDestinationNearestStopsList

    fun setStartDestinationNearestStopsList(startDestinationNearestStopsResponse: List<NearestStopsResponse>) {
        _startDestinationNearestStopsList.value = startDestinationNearestStopsResponse
    }

    private val _endDestinationNearestStopsList = MutableLiveData<List<NearestStopsResponse>>()
    val endDestinationNearestStopsList: LiveData<List<NearestStopsResponse>> get() = _endDestinationNearestStopsList

    fun setEndDestinationNearestStopsList(endDestinationNearestStopsResponse: List<NearestStopsResponse>) {
        _endDestinationNearestStopsList.value = endDestinationNearestStopsResponse
    }
}
