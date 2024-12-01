package com.example.btapp

data class WeatherResponse(
    val hourly: HourlyData
)

data class HourlyData(
    val time: List<String>,
    val temperature_2m: List<Double>,
    val precipitation: List<Double>,
    val windspeed_10m: List<Double>,
    val weathercode: List<Int>,
    val relativehumidity_2m: List<Double>,
    val visibility: List<Double>,
    val snowfall: List<Double>,
    val cloudcover: List<Double>
)
