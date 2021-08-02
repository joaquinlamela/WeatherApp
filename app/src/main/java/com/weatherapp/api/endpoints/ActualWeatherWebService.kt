package com.weatherapp.api.endpoints

import com.weatherapp.api.model.WeatherResponse
import com.weatherapp.api.model.WeatherResponseForMarker
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ActualWeatherWebService {

    @GET("onecall")
    suspend fun getWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("exclude") exclude: String,
        @Query("appid") appiid: String
    ): Response<WeatherResponse>

    @GET("onecall")
    suspend fun getWeatherForSpecificMarker(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("exclude") exclude: String,
        @Query("appid") appiid: String
    ): Response<WeatherResponseForMarker>
}