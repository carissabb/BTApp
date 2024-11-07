package com.example.btapp.ui.map

import com.example.btapp.BusInfo

interface BusDataUpdateListener {
    fun onBusDataUpdated(busInfoList: List<BusInfo>)
}