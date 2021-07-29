package com.weatherapp.repository.implementation

import android.util.Log
import com.google.gson.JsonElement
import com.weatherapp.api.RetrofitInstance
import com.weatherapp.repository.interfaces.IWeatherRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class WeatherRepository : IWeatherRepository {

    override suspend fun getWeather(lat: String, lon: String, apiKey: String): Response<JsonElement> {
        return try {
            var parameters: HashMap<String, String> = HashMap<String, String>()
            parameters.put("lat", lat)
            parameters.put("lon", lon)
            parameters.put("exclude", "minute, hourly")
            parameters.put("appid", apiKey)

            RetrofitInstance.weatherEnpoint.getWeather(parameters)
        } catch (ex: Exception) {
            Log.e("Exception on getWeather", ex.message!!)
            Response.error(500, "SERVER_ERROR".toResponseBody("text/plain".toMediaTypeOrNull()))
        }
    }
}