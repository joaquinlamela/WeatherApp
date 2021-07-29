package com.weatherapp.api

import com.weatherapp.api.Constants.Companion.BASE_URL
import com.weatherapp.api.endpoints.ActualWeatherWebService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val weatherEnpoint: ActualWeatherWebService by lazy {
        retrofit.create(ActualWeatherWebService::class.java)
    }

}