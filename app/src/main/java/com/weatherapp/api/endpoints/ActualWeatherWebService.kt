package com.weatherapp.api.endpoints

import com.weatherapp.api.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ActualWeatherWebService {

    /*
    @GET("onecall?")
    Suspend fun getWeather(@QueryMap options:Map<String, String>): Response<JsonElement>
    */

    @GET("onecall")
    suspend fun getWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("exclude") exclude: String,
        @Query("appid") appiid: String
    ): Response<WeatherResponse>


}