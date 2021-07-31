package com.weatherapp.repository.interfaces

import com.weatherapp.api.model.WeatherResponse
import retrofit2.Response

interface IWeatherRepository {
    suspend fun getWeather(lat: String, lon: String, apiKey:String): Response<WeatherResponse>
}