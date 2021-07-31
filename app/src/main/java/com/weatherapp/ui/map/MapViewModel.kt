package com.weatherapp.ui.map

import androidx.lifecycle.*
import com.weatherapp.repository.interfaces.IWeatherRepository
import com.weatherapp.ui.home.model.SearchModel
import kotlinx.coroutines.Dispatchers

class MapViewModel(private val weatherRepository: IWeatherRepository) : ViewModel() {

    private val weatherParams = MutableLiveData<SearchModel>()

    fun setParameters(searchParameters: SearchModel) {
        weatherParams.value = searchParameters
    }

    val getWeather = weatherParams.switchMap { searchParameters ->
        liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            emit(
                weatherRepository.getWeatherForSpecificMarker(
                    searchParameters.lat,
                    searchParameters.lon,
                    searchParameters.apiKey
                )
            )
        }
    }
}