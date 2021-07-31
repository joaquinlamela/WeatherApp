package com.weatherapp.ui.home

import androidx.lifecycle.*
import com.weatherapp.repository.interfaces.IWeatherRepository
import com.weatherapp.ui.home.model.SearchModel
import kotlinx.coroutines.Dispatchers

class HomeViewModel(private val weatherRepository: IWeatherRepository) : ViewModel() {

    private val weatherParams = MutableLiveData<SearchModel>()

    fun setParameters(searchParameters: SearchModel) {
        weatherParams.value = searchParameters
    }

    val getWeather = weatherParams.switchMap { searchParameters ->
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(
                weatherRepository.getWeather(searchParameters.lat, searchParameters.lon, searchParameters.apiKey)
            )
        }
    }
}