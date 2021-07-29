package com.weatherapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.gson.JsonElement
import com.weatherapp.api.Constants.Companion.API_KEY
import com.weatherapp.databinding.FragmentHomeBinding
import com.weatherapp.ui.home.model.SearchModel
import org.json.JSONObject
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
                getJsonFormat(response.body(), view)
            }
        })
    }

    private fun getJsonFormat(response: JsonElement?, view: View){
        val jsonObj = JSONObject(response.toString())
        val timeZone = jsonObj.getString("timezone").split("/").toTypedArray()
        binding.tvLocation.text = timeZone[1]
        val current = jsonObj.getJSONObject("current")
        val weather = current.getJSONArray("weather").getJSONObject(0)
        val descriptionMain = weather.getString("description")
        binding.tvStatus.text= descriptionMain
        val temp = (current.getDouble("temp") - 273.15).roundToInt()
        binding.tvTemp.text = temp.toString() + "°C"


        val updatedAt:Long = current.getLong("dt")
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