package com.weatherapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.squareup.picasso.Picasso
import com.weatherapp.api.Constants.Companion.API_KEY
import com.weatherapp.api.model.Weather
import com.weatherapp.api.model.WeatherResponse
import com.weatherapp.databinding.FragmentHomeBinding
import com.weatherapp.ui.home.model.SearchModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeVM: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val parameters: SearchModel = SearchModel("-34.905895", "-56.164993", API_KEY)
        homeVM.setParameters(parameters)

        homeVM.getWeather.observe(viewLifecycleOwner, androidx.lifecycle.Observer { response ->
            if(response.isSuccessful){
                visualizeResponse(response.body())
            }
        })
        return binding.root
    }

    private fun visualizeResponse(response: WeatherResponse?){
        setTimeZone(response!!)
        setWeather(response.current.weather[0])
        setWeatherValues(response)
        setSunsetAndSunrise(response)
        setUpdateAt(response)
    }

    private fun setTimeZone(response: WeatherResponse){
        val timeZoneSplitted = response.timezone.split("/").toTypedArray()
        binding.tvLocation.text = timeZoneSplitted[1]
    }

    private fun setWeather(weather: Weather){
        var descriptionOfStatus = weather.description
        descriptionOfStatus = descriptionOfStatus.substring(0,1).uppercase() + descriptionOfStatus.substring(1).lowercase()
        binding.tvStatus.text= descriptionOfStatus

        var iconUrl =  "https://openweathermap.org/img/w/" + weather.icon+ ".png";
        Picasso.get().load(iconUrl).into(binding.imgActualWeather)
    }

    private fun setWeatherValues(response: WeatherResponse){
        val temp = (response.current.temp - 273.15).roundToInt()
        binding.tvTemp.text = temp.toString() + "°C"

        val pressure = response.current.pressure.toString() + " mbar"
        binding.tvPressure.text = pressure

        val humidity = response.current.humidity.toString() + "%"
        binding.tvHumidity.text = humidity

        val wind = (response.current.wind_speed).toString() + " km/h"
        binding.tvWind.text = wind
    }

    private fun setSunsetAndSunrise(response: WeatherResponse){
        val calendar = Calendar.getInstance()

        calendar.time = Date(response.current.sunrise * 1000)
        calendar.add(Calendar.HOUR, -3)
        val sunrise = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(calendar.time)
        binding.tvSunrise.text = sunrise

        calendar.time = Date(response.current.sunset * 1000 )
        calendar.add(Calendar.HOUR, -3)
        val sunset = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(calendar.time)
        binding.tvSunset.text = sunset
    }

    private fun setUpdateAt(response: WeatherResponse){
        val updatedAt: Long = response.current.dt
        val updatedAtText = "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
            Date(updatedAt*1000)
        )
        binding.tvUpdatedAt.text = updatedAtText
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}