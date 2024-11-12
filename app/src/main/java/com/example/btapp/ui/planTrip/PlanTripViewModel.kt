package com.example.btapp.ui.planTrip

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.btapp.ArrivalAndDepartureTimesForRoutesResponse
import com.example.btapp.NearestStopsResponse
import com.example.btapp.ScheduledRoutesResponse

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

    // for get scheduled routes
    val selectedStopCode = MutableLiveData<String>()
    private val _scheduledRouteList = MutableLiveData<List<ScheduledRoutesResponse>>()
    val scheduledRoutesList: LiveData<List<ScheduledRoutesResponse>> = _scheduledRouteList

    fun setScheduledRoutesList(scheduledRoutes: List<ScheduledRoutesResponse>) {
        _scheduledRouteList.value = scheduledRoutes
    }
}
