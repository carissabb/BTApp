package com.example.btapp.ui.planTrip

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.btapp.ArrivalAndDepartureTimesForRoutesResponse
import com.example.btapp.NearestStopsResponse
import com.example.btapp.RetrofitInstance
import com.example.btapp.ScheduledRoutesResponse
import com.example.btapp.ScheduledStopCodesResponse
import com.example.btapp.WeatherResponse
import okhttp3.Callback
import retrofit2.Call
import retrofit2.Response
import java.time.LocalDate

class PlanTripViewModel : ViewModel() {

    var onFetchNearestStops: ((Double, Double, Boolean) -> Unit)? = null

    fun fetchNearestStopsForDestination(latitude: Double, longitude: Double, isStart: Boolean) {
        // Call the callback in MainActivity to trigger fetching nearest stops
        onFetchNearestStops?.invoke(latitude, longitude, isStart)
    }

    var onFetchWeatherData: ((Double, Double, Long) -> Unit)? = null

    fun fetchWeather(latitude: Double, longitude: Double, timestamp: Long) {
        onFetchWeatherData?.invoke(latitude, longitude, timestamp)
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

    private val _stopToRoute = MutableLiveData<Map<String, List<ScheduledRoutesResponse>>>()
    val stopToRoute: LiveData<Map<String, List<ScheduledRoutesResponse>>> get() = _stopToRoute

    fun setStopToRoutesMap(map: Map<String, List<ScheduledRoutesResponse>>) {
        _stopToRoute.postValue(map)
    }

    private val _routeToStop = MutableLiveData<Map<String, List<ScheduledStopCodesResponse
            >>>()
    val routeToStops: LiveData<Map<String, List<ScheduledStopCodesResponse>>> get() = _routeToStop

    fun setRouteToStopsMap(routeToStop: Map<String, List<ScheduledStopCodesResponse>>) {
        _routeToStop.value = routeToStop
    }

    private val _arrivalDepartureTimeList = MutableLiveData<List<ArrivalAndDepartureTimesForRoutesResponse>>()
    val arrivalDepartureTimesList: LiveData<List<ArrivalAndDepartureTimesForRoutesResponse>> = _arrivalDepartureTimeList

    fun setArrivalDepartureTimesList(arrivalDepartureTimes: List<ArrivalAndDepartureTimesForRoutesResponse>) {
        _arrivalDepartureTimeList.value = arrivalDepartureTimes
    }

    // for get scheduled routes
    val selectedStopCode = MutableLiveData<String>()
    private val _scheduledRouteList = MutableLiveData<List<ScheduledRoutesResponse>>()
    val scheduledRoutesList: LiveData<List<ScheduledRoutesResponse>> get()= _scheduledRouteList

    fun setScheduledRoutesList(scheduledRoutes: List<ScheduledRoutesResponse>) {
        _scheduledRouteList.value = scheduledRoutes
    }
}
