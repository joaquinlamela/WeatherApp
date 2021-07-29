package com.weatherapp.ui.home.model

import com.weatherapp.api.Constants

data class SearchModel(
    var lat: String = Constants.DEFAULT_LAT,
    var lon: String = Constants.DEFAULT_LON,
    var apiKey: String = Constants.API_KEY
)