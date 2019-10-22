package com.androsgames.myweatherapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.androsgames.myweatherapp.models.WeatherData
import com.androsgames.myweatherapp.repository.Repository

class MainViewModel(application: Application) : AndroidViewModel(application) {

    // Поиск данных погоды по локации и кэшированных городов только при первом запуске, не повторять при изменении конфигурации
    var firstLaunchOfActivityCompleted = false

    val weatherReportsList : LiveData<ArrayList<WeatherData>>
     get() = Repository.getWeatherReportsList()


     fun getWeatherByCoordinates(lat : Double, lon : Double)  {
         Repository.getWeatherByCoordinates(lat, lon)
         firstLaunchOfActivityCompleted = true
     }

     fun getWeatherByCityName(city : String) {
         if (weatherReportsList.value != null) {
             for (weatherReport in weatherReportsList.value!!) {
                 if(weatherReport.name == city) {
                     Repository.sendErrorMessage("The weather report is already in the list")
                     return
                 }
             }
         }
         Repository.getWeatherByCityName(city)
         firstLaunchOfActivityCompleted = true
     }

    fun getErrorMessage() : LiveData<String> {
        return Repository.getErrorMessage()
    }

    fun consumeErrorMessage() {
        Repository.sendErrorMessage(null)
    }

    fun requestCachedCities() {
        Repository.requestCachedCities(getApplication())
    }

     fun cancelJobs(){
        Repository.cancelJobs()
     }

}