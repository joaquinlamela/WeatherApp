package com.weatherapp.repository.interfaces

import com.weatherapp.api.model.WeatherResponse
import com.weatherapp.api.model.WeatherResponseForMarker
import retrofit2.Response

interface IWeatherRepository {
    suspend fun getWeather(lat: String, lon: String, apiKey: String): Response<WeatherResponse>

    suspend fun getWeatherForSpecificMarker(
        lat: String,
        lon: String,
        apiKey: String
    ): Response<WeatherResponseForMarker>
}