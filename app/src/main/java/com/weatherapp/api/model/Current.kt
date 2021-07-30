package com.weatherapp.api.model


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Current(
    val dt: Long,
    val humidity: Int,
    val pressure: Int,
    val sunrise: Int,
    val sunset: Int,
    val temp: Double,
    val weather: List<Weather>
) : Parcelable