package com.weatherapp.api.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeatherResponseForMarker (
    val current: Current,
    val timezone: String
): Parcelable

