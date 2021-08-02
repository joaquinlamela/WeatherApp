package com.weatherapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.weatherapp.repository.interfaces.IWeatherRepository

class HomeViewModelFactory(
    private val weatherRepository: IWeatherRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(weatherRepository) as T
    }
}