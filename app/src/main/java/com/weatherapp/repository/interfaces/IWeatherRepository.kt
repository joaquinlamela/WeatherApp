package com.weatherapp.repository.interfaces

import com.google.gson.JsonElement
import retrofit2.Response

interface IWeatherRepository {
    suspend fun getWeather(lat: String, lon: String, apiKey:String): Response<JsonElement>
}