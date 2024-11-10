package com.example.btapp.ui.routes

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.btapp.ArrivalAndDepartureTimesForRoutesResponse
import com.example.btapp.BTApiService
import com.example.btapp.CurrentRoutesResponse
import com.example.btapp.NotificationReceiver
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
