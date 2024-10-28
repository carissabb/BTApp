package com.example.btapp.ui.planTrip

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlanTripViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is plan trip Fragment"
    }
    val text: LiveData<String> = _text
}