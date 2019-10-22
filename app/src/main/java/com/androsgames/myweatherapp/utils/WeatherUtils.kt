package com.androsgames.myweatherapp.utils

import com.androsgames.myweatherapp.R
import com.androsgames.myweatherapp.models.WeatherData
import kotlin.math.roundToInt

object WeatherUtils {


    fun convertTempFromKeltoCel(kelvinTemp : Double) : Int {
        return (kelvinTemp - 273.15).roundToInt()
    }



    fun getWindDirectionDescription(degrees : Double) : String {
        return when (degrees.roundToInt()) {
            in 25..65 -> "сев. восточный ветер"
            in 66..115 -> "восточный ветер"
            in 116..155 -> "юго-восточный ветер"
            in 156..205 -> "южный ветер"
            in 206..245 -> "юго-западный ветер"
            in 246..295 -> "западный ветер"
            in 296..336 -> "сев. западный ветер"
            else -> "северный ветер"
        }
    }

    fun getIconByCode(iconCode : String) : Int {
        return when (iconCode) {
            "01d" -> R.drawable.clear
            "02d" -> R.drawable.fewclouds
            "03d", "04d", "03n", "04n" -> R.drawable.clouds
            "09d", "10d", "09n", "10n"  -> R.drawable.rain
            "11d", "11n" -> R.drawable.thunderstorm
            "13d", "13n" -> R.drawable.snow
            "50d", "50n" -> R.drawable.mist
            "01n" -> R.drawable.clearnight
            "02n" -> R.drawable.fewcloudsnight
            else -> R.color.colorBlue
        }
    }

    fun nightTime(weatherReport : WeatherData) : Boolean {
        if (weatherReport.sys.sunrise <= weatherReport.dt && weatherReport.dt < weatherReport.sys.sunset) {
            return false
        }
        return true
    }

}