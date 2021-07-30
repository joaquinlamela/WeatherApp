package com.weatherapp.api.model


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Daily(
    val dt: Long,
    val humidity: Int,
    val pressure: Int,
    val wind_speed: Double,
    val sunrise: Long,
    val sunset: Long,
    val temp: Temp,
    val weather: List<WeatherForDaily>
) : Parcelable