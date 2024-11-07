package com.example.btapp.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.btapp.BusInfo
import com.example.btapp.CurrentRoutesResponse

class MapViewModel : ViewModel() {

    private val _busInfoList = MutableLiveData<List<BusInfo>>()
    val busInfoList: LiveData<List<BusInfo>> get() = _busInfoList

    fun setBusInfoList(busInfo: List<BusInfo>) {
        _busInfoList.value = busInfo
    }

    // for color
//    private val _routesList = MutableLiveData<List<CurrentRoutesResponse>>()
//    val routesList: LiveData<List<CurrentRoutesResponse>> = _routesList
//
//    fun setRoutesList(routes: List<CurrentRoutesResponse>) {
//        _routesList.value = routes
//    }
//
//    // Helper function to get color by route ID
//    fun getRouteColor(routeName: String): String? {
//        return _routesList.value?.find { it.routeName == routeName }?.routeColor
//    }
}