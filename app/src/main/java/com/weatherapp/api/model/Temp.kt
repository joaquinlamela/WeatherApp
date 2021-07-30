package com.weatherapp.api.model


import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Temp(
    val day: Double,
    val eve: Double,
    val max: Double,
    val min: Double,
    val morn: Double,
    val night: Double
) : Parcelable