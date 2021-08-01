package com.weatherapp.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.weatherapp.R
import com.weatherapp.api.model.Daily
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class DailyWeatherAdapter(val dailyWeatherList: List<Daily>) :
    GenericAdapter<Daily>(dailyWeatherList) {

    inner class DailyWeatherHolder(val view: View) : BaseViewHolder<Daily>(view) {
        var iconWeather: ImageView = view.findViewById(R.id.img_IconWeather)
        var tvDate: TextView = view.findViewById(R.id.tv_Date)
        var tv_MaxTemp: TextView = view.findViewById(R.id.tv_MaxTemp)
        var tv_MinTemp: TextView = view.findViewById(R.id.tv_MinTemp)

        override fun render(daily: Daily) {
            var icon = daily.weather[0].icon
            var iconUrl = "https://openweathermap.org/img/w/$icon.png";
            Picasso.get().load(iconUrl).into(iconWeather)

            val updatedAt: Long = daily.dt
            val updatedAtText = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(
                Date(updatedAt * 1000)
            )
            tvDate.text = updatedAtText

            val tempMin = (daily.temp.min - 273.15).roundToInt()
            tv_MinTemp.text = tempMin.toString() + "°C"

            val tempMax = (daily.temp.max - 273.15).roundToInt()
            tv_MaxTemp.text = tempMax.toString() + "°C"
        }
    }

    override fun setViewHolder(parent: ViewGroup): BaseViewHolder<Daily> {
        val layoutInflater = LayoutInflater.from(parent.context)
        return DailyWeatherHolder(
            layoutInflater.inflate(
                R.layout.item_daily_weather,
                parent,
                false
            )
        )
    }
}