package com.weatherapp.ui.map

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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import com.weatherapp.R
import com.weatherapp.api.Constants
import com.weatherapp.api.model.Weather
import com.weatherapp.api.model.WeatherResponseForMarker
import com.weatherapp.databinding.FragmentMapBinding
import com.weatherapp.ui.home.model.SearchModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private var map: GoogleMap? = null
    private var lastKnownLocation: Location? = null
    private val DEFAULT_ZOOM = 13
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val defaultLocation = LatLng(-34.905895, -56.164993)
    private var longitude: Float = -56.164993f
    private var latitude: Float = -34.905895f
    private var marker: Marker? = null
    private val mapVM: MapViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        val mMapFragment = SupportMapFragment.newInstance()
        childFragmentManager.beginTransaction().add(R.id.map, mMapFragment).commit()
        mMapFragment.view?.isClickable = true
        mMapFragment.getMapAsync(this)
        getWeatherForMarker(
            defaultLocation.latitude.toString(),
            defaultLocation.longitude.toString()
        )
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        updateLocationUI()
        getDeviceLocation()
        marker = map!!.addMarker(
            MarkerOptions()
                .position(defaultLocation)
        )
        map!!.setOnMapLongClickListener(OnMapLongClickListener { latLng ->
            val latitude = latLng.latitude.toString()
            val longitude = latLng.longitude.toString()
            getWeatherForMarker(latitude, longitude)
            marker!!.position = latLng
        })
    }

    private fun getWeatherForMarker(lat: String, lon: String) {
        val parameters = SearchModel(lat, lon, Constants.API_KEY)
        mapVM.setParameters(parameters)

        mapVM.getWeather.observe(viewLifecycleOwner, androidx.lifecycle.Observer { response ->
            if (response.isSuccessful) {
                visualizeResponse(response.body()!!)
            }else{
                Toast.makeText(
                    context,
                    "An error has occurred while trying to get the time for the selected marker.",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun visualizeResponse(response: WeatherResponseForMarker?) {
        setTimeZone(response!!)
        setWeather(response.current.weather[0])
        setWeatherValues(response)
        setSunsetAndSunrise(response)
        setUpdateAt(response)
    }

    private fun setTimeZone(response: WeatherResponseForMarker) {
        val timeZoneSplitted = response.timezone.split("/").toTypedArray()
        binding.tvTimeZone.text = timeZoneSplitted[1]
    }

    private fun setWeatherValues(response: WeatherResponseForMarker) {
        val temp = (response.current.temp - 273.15).roundToInt()
        binding.tvTempForLocation.text = temp.toString() + "°C"
    }

    private fun setSunsetAndSunrise(response: WeatherResponseForMarker) {
        val calendar = Calendar.getInstance()

        calendar.time = Date(response.current.sunrise * 1000)
        calendar.add(Calendar.HOUR, -3)
        val sunrise =
            SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(calendar.time)
        binding.tvSunriseForLocation.text = sunrise

        calendar.time = Date(response.current.sunset * 1000)
        calendar.add(Calendar.HOUR, -3)
        val sunset = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(calendar.time)
        binding.tvSunsetForLocation.text = sunset
    }

    private fun setUpdateAt(response: WeatherResponseForMarker) {
        val updatedAt: Long = response.current.dt
        val updatedAtText = SimpleDateFormat("EEE, d MMM yyyy", Locale.ENGLISH).format(
            Date(updatedAt * 1000)
        )
        binding.tvDateForLocation.text = updatedAtText
    }

    private fun setWeather(weather: Weather) {
        var descriptionOfStatus = weather.description
        descriptionOfStatus =
            descriptionOfStatus.substring(0, 1).uppercase() + descriptionOfStatus.substring(1)
                .lowercase()
        binding.tvStatusForLocation.text = descriptionOfStatus

        var iconUrl = "https://openweathermap.org/img/w/" + weather.icon + ".png";
        Picasso.get().load(iconUrl).into(binding.ivIconForLocation)
    }

    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            map?.isMyLocationEnabled = true
            map?.uiSettings?.isMyLocationButtonEnabled = true
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun getDeviceLocation() {
        try {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    lastKnownLocation = task.result
                    if (lastKnownLocation != null) {
                        map?.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    lastKnownLocation!!.latitude,
                                    lastKnownLocation!!.longitude
                                ), DEFAULT_ZOOM.toFloat()
                            )
                        )
                        longitude = lastKnownLocation!!.longitude.toFloat()
                        latitude = lastKnownLocation!!.latitude.toFloat()
                    }
                } else {
                    Log.d(ContentValues.TAG, "Current location is null. Using defaults.")
                    Log.e(ContentValues.TAG, "Exception: %s", task.exception)
                    map?.moveCamera(
                        CameraUpdateFactory
                            .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat())
                    )
                    map?.uiSettings?.isMyLocationButtonEnabled = false
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
}