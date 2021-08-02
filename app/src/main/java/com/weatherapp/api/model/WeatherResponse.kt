package com.weatherapp.api.model


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeatherResponse(
    val current: Current,
    val daily: List<Daily>,
    val timezone: String
) : Parcelable