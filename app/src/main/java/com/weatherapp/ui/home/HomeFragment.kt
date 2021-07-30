package com.weatherapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.squareup.picasso.Picasso
import com.weatherapp.api.Constants.Companion.API_KEY
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val parameters: SearchModel = SearchModel("-34.905895", "-56.164993", API_KEY)
        homeVM.setParameters(parameters)

        homeVM.getWeather.observe(viewLifecycleOwner, androidx.lifecycle.Observer { response ->
            if(response.isSuccessful){
                visualizeResponse(response.body(), view)
            }
        })
    }

    private fun visualizeResponse(response: WeatherResponse?, view: View){
        val timeZoneSplitted = response!!.timezone.split("/").toTypedArray()
        binding.tvLocation.text = timeZoneSplitted[1]
        val weather = response.current.weather[0]
        var descriptionOfStatus = weather.description
        descriptionOfStatus = descriptionOfStatus.substring(0,1).uppercase() + descriptionOfStatus.substring(1).lowercase()
        binding.tvStatus.text= descriptionOfStatus
        val temp = (response.current.temp - 273.15).roundToInt()
        binding.tvTemp.text = temp.toString() + "°C"
        var iconUrl =  "https://openweathermap.org/img/w/" + weather.icon+ ".png";
        Picasso.get().load(iconUrl).into(binding.imgActualWeather)

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