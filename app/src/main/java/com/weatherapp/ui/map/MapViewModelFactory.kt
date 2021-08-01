package com.weatherapp.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.weatherapp.repository.interfaces.IWeatherRepository

class MapViewModelFactory(private val weatherRepository: IWeatherRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MapViewModel(weatherRepository) as T
    }
}

