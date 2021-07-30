package com.weatherapp.ui.home

import android.annotation.SuppressLint
import android.content.ContentValues
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.squareup.picasso.Picasso
import com.weatherapp.api.Constants
import com.weatherapp.api.model.Weather
import com.weatherapp.api.model.WeatherResponse
import com.weatherapp.databinding.FragmentHomeBinding
import com.weatherapp.ui.home.adapter.DailyWeatherAdapter
import com.weatherapp.ui.home.model.SearchModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeVM: HomeViewModel by activityViewModels()
    private var lastKnownLocation: Location? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var longitude: Float = -56.164993f
    private var latitude: Float = -34.905895f


    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        initRecyclerViewForDailyWeather()


        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        getDeviceLocation()

        val parameters = SearchModel(latitude.toString(), longitude.toString(), Constants.API_KEY)
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
        setUpRecyclerView(response)
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

    private fun getDeviceLocation() {
        try {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    lastKnownLocation = task.result
                    if (lastKnownLocation != null) {
                        longitude = lastKnownLocation!!.longitude.toFloat()
                        latitude = lastKnownLocation!!.latitude.toFloat()
                    }
                } else {
                    Log.d(ContentValues.TAG, "Current location is null. Using defaults.")
                    Log.e(ContentValues.TAG, "Exception: %s", task.exception)
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun initRecyclerViewForDailyWeather() {
        binding.rvDailyWeather.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false
        )
    }

    private fun setUpRecyclerView(response: WeatherResponse){
        binding.rvDailyWeather.adapter = DailyWeatherAdapter(response.daily)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}