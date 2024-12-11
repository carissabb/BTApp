package com.example.btapp.ui.routes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.btapp.ArrivalAndDepartureTimesForRoutesResponse
import com.example.btapp.CurrentRoutesResponse


// bridge to pass data from main to RoutesFragment
class RoutesViewModel : ViewModel() {
    private val _routesList = MutableLiveData<List<CurrentRoutesResponse>>()
    val routesList: LiveData<List<CurrentRoutesResponse>> = _routesList

    fun setRoutesList(routes: List<CurrentRoutesResponse>) {
        _routesList.value = routes
    }

    val selectedRouteShortName = MutableLiveData<String>()
    private val _arrivalDepartureTimeList = MutableLiveData<List<ArrivalAndDepartureTimesForRoutesResponse>>()
    val arrivalDepartureTimesList: LiveData<List<ArrivalAndDepartureTimesForRoutesResponse>> = _arrivalDepartureTimeList

    fun setArrivalDepartureTimesList(arrivalDepartureTimes: List<ArrivalAndDepartureTimesForRoutesResponse>) {
        _arrivalDepartureTimeList.value = arrivalDepartureTimes
    }
}
