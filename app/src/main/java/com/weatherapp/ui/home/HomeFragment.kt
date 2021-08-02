package com.weatherapp.ui.home

import android.Manifest
import android.content.ContentValues
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.squareup.picasso.Picasso
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import com.weatherapp.api.Constants
import com.weatherapp.api.model.Weather
import com.weatherapp.api.model.WeatherResponse
import com.weatherapp.databinding.FragmentHomeBinding
import com.weatherapp.ui.home.adapter.DailyWeatherAdapter
import com.weatherapp.ui.home.model.SearchModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class HomeFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeVM: HomeViewModel by activityViewModels()
    private var lastKnownLocation: Location? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        initRecyclerViewForDailyWeather()

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        if (hasLocationPermission()) {
            getDeviceLocation()

        } else {
            requestLocationPermission()
        }

        return binding.root
    }

    private fun visualizeResponse(response: WeatherResponse?) {
        setTimeZone(response!!)
        setWeather(response.current.weather[0])
        setWeatherValues(response)
        setSunsetAndSunrise(response)
        setUpdateAt(response)
        setUpRecyclerView(response)
    }

    private fun setTimeZone(response: WeatherResponse) {
        val timeZoneSplitted = response.timezone.split("/").toTypedArray()
        binding.tvLocation.text = timeZoneSplitted[1]
    }

    private fun setWeather(weather: Weather) {
        var descriptionOfStatus = weather.description
        descriptionOfStatus =
            descriptionOfStatus.substring(0, 1).uppercase() + descriptionOfStatus.substring(1)
                .lowercase()
        binding.tvStatus.text = descriptionOfStatus

        var iconUrl = "https://openweathermap.org/img/w/" + weather.icon + ".png";
        Picasso.get().load(iconUrl).into(binding.imgActualWeather)
    }

    private fun setWeatherValues(response: WeatherResponse) {
        val temp = (response.current.temp - 273.15).roundToInt()
        binding.tvTemp.text = temp.toString() + "°C"

        val pressure = response.current.pressure.toString() + " mbar"
        binding.tvPressure.text = pressure

        val humidity = response.current.humidity.toString() + "%"
        binding.tvHumidity.text = humidity

        val wind = (response.current.wind_speed).toString() + " km/h"
        binding.tvWind.text = wind
    }

    private fun setSunsetAndSunrise(response: WeatherResponse) {
        val calendar = Calendar.getInstance()

        calendar.time = Date(response.current.sunrise * 1000)
        calendar.add(Calendar.HOUR, -3)
        val sunrise = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(calendar.time)
        binding.tvSunrise.text = sunrise

        calendar.time = Date(response.current.sunset * 1000)
        calendar.add(Calendar.HOUR, -3)
        val sunset = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(calendar.time)
        binding.tvSunset.text = sunset
    }

    private fun setUpdateAt(response: WeatherResponse) {
        val updatedAt: Long = response.current.dt
        val updatedAtText = SimpleDateFormat("EEE, d MMM yyyy", Locale.ENGLISH).format(
            Date(updatedAt * 1000)
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
                        getWeatherCall(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude)
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

    private fun setUpRecyclerView(response: WeatherResponse) {
        binding.rvDailyWeather.adapter = DailyWeatherAdapter(response.daily)
    }

    private fun hasLocationPermission() = EasyPermissions.hasPermissions(
        requireContext(),
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private fun requestLocationPermission() {
        EasyPermissions.requestPermissions(
            this,
            "This app need to get your location",
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if (perms.isNotEmpty()) {
                    getDeviceLocation()
                }
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(requireContext()).build().show()
        } else {
            requestLocationPermission()
        }
    }

    private fun getWeatherCall(latitude: Double, longitude: Double) {
        val parameters = SearchModel(latitude.toString(), longitude.toString(), Constants.API_KEY)
        homeVM.setParameters(parameters)

        homeVM.getWeather.observe(viewLifecycleOwner, androidx.lifecycle.Observer { response ->
            if (response.isSuccessful) {
                visualizeResponse(response.body())
            }else{
                Toast.makeText(
                    context,
                    "An error occurred while trying to get the weather for your location.",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}