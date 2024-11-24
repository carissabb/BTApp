package com.example.btapp.ui.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.btapp.BusInfo
import com.example.btapp.ui.routes.RoutesViewModel

class MapViewModel() : ViewModel() {

    private val _busInfoList = MutableLiveData<List<BusInfo>>()
    val busInfoList: LiveData<List<BusInfo>> get() = _busInfoList

    fun setBusInfoList(busInfo: List<BusInfo>) {
        _busInfoList.value = busInfo
    }
}