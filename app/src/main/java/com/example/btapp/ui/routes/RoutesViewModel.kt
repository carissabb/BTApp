package com.example.btapp.ui.routes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.btapp.ArrivalAndDepartureTimesForRoutesResponse
import com.example.btapp.CurrentRoutesResponse
import com.example.btapp.RetrofitInstance
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response

// bridge to pass data from main to RoutesFragment
class RoutesViewModel : ViewModel() {
    private val _routesList = MutableLiveData<List<CurrentRoutesResponse>>()
    val routesList: LiveData<List<CurrentRoutesResponse>> = _routesList

    fun setRoutesList(routes: List<CurrentRoutesResponse>) {
        _routesList.value = routes
    }

//    fun fetchArrivalAndDepartureTimes(route: CurrentRoutesResponse): LiveData<List<ArrivalAndDepartureTimesForRoutesResponse>> {
//        val timesLiveData = MutableLiveData<List<ArrivalAndDepartureTimesForRoutesResponse>>()
//
//        RetrofitInstance.apiService.getArrivalAndDepartureTimesForRoutes(route.routeName).enqueue(object :
//            Callback<List<ArrivalAndDepartureTimesForRoutesResponse>> {
//            override fun onResponse(
//                call: Call<List<ArrivalAndDepartureTimesForRoutesResponse>>,
//                response: Response<List<ArrivalAndDepartureTimesForRoutesResponse>>
//            ) {
//                if (response.isSuccessful) {
//                    timesLiveData.postValue(response.body())
//                }
//            }
//
//            override fun onFailure(call: Call<List<ArrivalAndDepartureTimesForRoutesResponse>>, t: Throwable) {
//                // Log error or handle failure
//            }
//        })
//
//        return timesLiveData
//    }

    private val _arrivalAndDepartureTimeList = MutableLiveData<List<ArrivalAndDepartureTimesForRoutesResponse>>()
    val arrivalAndDepartureTimeList: LiveData<List<ArrivalAndDepartureTimesForRoutesResponse>> = _arrivalAndDepartureTimeList

    fun setArrivalAndDepartureTimeList(arrivalAndDepartureTime: List<ArrivalAndDepartureTimesForRoutesResponse>) {
        _arrivalAndDepartureTimeList.value = arrivalAndDepartureTime
    }
}
