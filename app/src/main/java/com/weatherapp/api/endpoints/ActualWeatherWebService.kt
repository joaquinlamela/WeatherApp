package com.weatherapp.api.endpoints

import com.google.gson.JsonElement
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface ActualWeatherWebService {
    @GET("onecall?")
    suspend fun getWeather(@QueryMap options:Map<String, String>): Response<JsonElement>
}