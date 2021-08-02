package com.weatherapp.repository.implementation

import android.util.Log
import com.weatherapp.api.RetrofitInstance
import com.weatherapp.api.model.WeatherResponse
import com.weatherapp.api.model.WeatherResponseForMarker
import com.weatherapp.repository.interfaces.IWeatherRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class WeatherRepository : IWeatherRepository {

    override suspend fun getWeather(
        lat: String,
        lon: String,
        apiKey: String
    ): Response<WeatherResponse> {
        return try {
            val exclude = "minute, hourly"
            RetrofitInstance.weatherEnpoint.getWeather(lat, lon, exclude, apiKey)
        } catch (ex: Exception) {
            Log.e("Exception on getWeather", ex.message!!)
            Response.error(500, "SERVER_ERROR".toResponseBody("text/plain".toMediaTypeOrNull()))
        }
    }

    override suspend fun getWeatherForSpecificMarker(
        lat: String,
        lon: String,
        apiKey: String
    ): Response<WeatherResponseForMarker> {
        return try {
            val exclude = "minute, hourly, daily"
            RetrofitInstance.weatherEnpoint.getWeatherForSpecificMarker(lat, lon, exclude, apiKey)
        } catch (ex: Exception) {
            Log.e("Exception on marker get", ex.message!!)
            Response.error(500, "SERVER_ERROR".toResponseBody("text/plain".toMediaTypeOrNull()))
        }

    }
}