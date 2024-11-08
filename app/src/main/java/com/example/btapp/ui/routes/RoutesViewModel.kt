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

    /*fun fetchLastBusTime(apiService: BTApiService, onResult: (Date?) -> Unit){
        apiService.getArrivalAndDepartureTimesForRoutes().enqueue(object : Callback<ArrivalAndDepartureTimesForRoutesResponse> {
            override fun onResponse(call: Call<ArrivalAndDepartureTimesForRoutesResponse>, response: Response<ArrivalAndDepartureTimesForRoutesResponse>) {
                if (response.isSuccessful) {
                    val calculatedDepartureTime = response.body()?.calculatedDepartureTime
                    val lastBusTime = calculatedDepartureTime?.let { parseTime(it) }
                    onResult(lastBusTime)
                } else {
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<ArrivalAndDepartureTimesForRoutesResponse>, t: Throwable) {
                onResult(null)
            }
        })
    }*/

    private fun parseTime(timeStr: String): Date? {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        return try {
            format.parse(timeStr)
        } catch (e: ParseException) {
            null
        }
    }

    fun scheduleBusNotification(context: Context, lastBusTime: Date?) {
        lastBusTime?.let {
            val calendar = Calendar.getInstance().apply {
                time = it
                add(Calendar.HOUR, -1)
            }
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, NotificationReceiver::class.java)
            val pendingIntent =
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }


}
