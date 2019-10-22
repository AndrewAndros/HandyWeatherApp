package com.androsgames.myweatherapp.ui

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.androsgames.myweatherapp.R
import com.androsgames.myweatherapp.models.WeatherData
import com.androsgames.myweatherapp.utils.WeatherUtils.convertTempFromKeltoCel
import com.androsgames.myweatherapp.utils.WeatherUtils.getIconByCode
import com.androsgames.myweatherapp.utils.WeatherUtils.getWindDirectionDescription
import com.androsgames.myweatherapp.utils.WeatherUtils.nightTime
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_weather.*
import kotlin.math.roundToInt

class WeatherActivity : AppCompatActivity() {

    private lateinit var weatherReport : WeatherData



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        supportActionBar?.hide()

        getIncomingIntent()
        updateUI()
    }

    private fun updateUI() {
        city_name_view.text = weatherReport.name
        temperature_view.text = "${convertTempFromKeltoCel(weatherReport.main.temp)}\u2103"
        wind_text_view.text = getWindDirectionDescription(weatherReport.wind.deg) + " ${weatherReport.wind.speed.roundToInt()} м/с"
        others_text_view.text = "влажность: ${weatherReport.main.humidity.roundToInt()}% " +
                " атм.давление: ${(weatherReport.main.pressure / 1.33322387415).roundToInt()} мм" // 1.333... конвертация к мм ртутного столба

        Glide.with(this)
            .load(getIconByCode(weatherReport.weather.get(0).icon))
            .into(weather_picture)

        wind_picture_view.rotation = weatherReport.wind.deg.toFloat()
        if(nightTime(weatherReport)) weather_root_view.background = ColorDrawable(ContextCompat.getColor(this, R.color.colorDusk))

    }


    private fun getIncomingIntent() {
        if(intent.hasExtra("report")) {
            weatherReport = intent.getParcelableExtra("report")

        }
    }

}
