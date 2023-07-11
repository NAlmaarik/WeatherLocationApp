package com.example.weatherlocationapp.domain

data class RequestWeatherLocation(
    val cityName : String?,
    val isCurrentLocation : Boolean,
    val lat : Double,
    val log : Double
)
