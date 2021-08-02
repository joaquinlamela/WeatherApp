package com.weatherapp.api.model


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeatherForDaily(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
) : Parcelable