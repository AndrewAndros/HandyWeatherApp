package com.androsgames.myweatherapp.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.androsgames.myweatherapp.api.RetrofitBuilder
import com.androsgames.myweatherapp.database.AppDatabase
import com.androsgames.myweatherapp.database.CityDao
import com.androsgames.myweatherapp.models.CityId
import com.androsgames.myweatherapp.models.WeatherData
import com.androsgames.myweatherapp.utils.Constants
import com.androsgames.myweatherapp.utils.Constants.NETWORK_TIMEOUT
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO


object Repository  {

    var mainJob: CompletableJob? = null
    var coordJob: CompletableJob? = null
    private val weatherReportsList : MutableLiveData<ArrayList<WeatherData>> = MutableLiveData()
    private val errorMessage : MutableLiveData<String> = MutableLiveData()

    lateinit var cityDao : CityDao


    fun getErrorMessage() : LiveData<String> {
        return errorMessage
    }

    fun sendErrorMessage(error : String?) {
        errorMessage.postValue(error)
    }

    fun getWeatherReportsList() : LiveData<ArrayList<WeatherData>> {
        return weatherReportsList
    }


    fun getWeatherByCityName(city: String) {
        mainJob = Job()
        mainJob?.let { theJob ->
        CoroutineScope(IO).launch {
            val job = withTimeoutOrNull(NETWORK_TIMEOUT) {
                try {
                    val result =
                        RetrofitBuilder.apiService.getWeatherByCityName(city, Constants.API_KEY)
                    withContext(Dispatchers.Main) {
                        if (result != null) {
                            addToReportsList(false, result)
                        }
                         theJob.complete()
                    }
                } catch (ex: Exception) {
                    sendErrorMessage("Request failed. Check your network connection")
                }

            }
            if (job == null) {
                sendErrorMessage("Request failed. Network timeout")
            }
        }
    }
    }

    fun getWeatherByCityIds( IDs : String) {
        mainJob = Job()
        mainJob?.let { theJob ->
            CoroutineScope(IO).launch {
                val job = withTimeoutOrNull(NETWORK_TIMEOUT) {
                    try {
                        val result = RetrofitBuilder.apiService.getWeatherReportsByIds(IDs, Constants.API_KEY)
                        withContext(Dispatchers.Main) {
                            if (result != null) {
                                addBunchOfCitiesToReportsList(result.reports)
                            }
                            theJob.complete()
                        }
                    } catch (ex: Exception) {
                        sendErrorMessage("Request failed. Check your network connection")
                    }

                }
                if (job == null) {
                    sendErrorMessage("Request of cached data failed. Network timeout")
                }
            }
        }
    }



    fun getWeatherByCoordinates(lat: Double, lon: Double) {
        coordJob = Job()
        coordJob?.let { theJob ->
            CoroutineScope(IO).launch {
                val job = withTimeoutOrNull(NETWORK_TIMEOUT) {
                    try {
                        val result =
                            RetrofitBuilder.apiService.getWeatherByCoordinates(lat, lon, Constants.API_KEY)
                        withContext(Dispatchers.Main) {
                            if (result != null) {
                                addToReportsList(true, result)
                            }
                            theJob.complete()
                        }
                    } catch (ex: Exception) {
                        errorMessage.postValue("Request for weather in current location failed. Check your network connection and GPS")
                    }

                }
                if (job == null) {
                    errorMessage.postValue("Request failed. Network timeout")
                }
            }
        }
    }


    private fun addToReportsList(firstPriority : Boolean, report : WeatherData) {
        if(weatherReportsList.value == null) {
            weatherReportsList.value = ArrayList()
        } else {
            removeIfReportAlreadyExists(report.id)
        }
        val updatedList = weatherReportsList.value
        if(firstPriority) {
            val newList : ArrayList<WeatherData> = ArrayList()
            newList.add(report)
            newList.addAll(updatedList!!)
            weatherReportsList.value = newList
        } else {
            updatedList!!.add(report)
            weatherReportsList.value = updatedList
        }
        val cityID = CityId(report.id)

        CoroutineScope(Default).launch {
            cityDao.insertCityId(cityID)
        }

    }

    private fun removeIfReportAlreadyExists(checkId : Int) {
        for (report in weatherReportsList.value!!) {
            if(report.id == checkId) {
                weatherReportsList.value!!.remove(report)
            }
        }
    }

    private fun addBunchOfCitiesToReportsList(defaultList : List<WeatherData>) {
        if(weatherReportsList.value == null) {
            weatherReportsList.value = ArrayList()
        }
        for (item in defaultList) {
            removeIfReportAlreadyExists(item.id)
        }
        val updatedList = weatherReportsList.value
        updatedList!!.addAll(defaultList)
        weatherReportsList.value = updatedList
    }


    fun requestCachedCities(context : Context) {
        cityDao = AppDatabase.getInstance(context).cityDao()
       CoroutineScope(Default).launch {
            val listFromCache = cityDao.getAll()
            if (listFromCache != null && listFromCache.isNotEmpty()) {
                var requestString= ""
                    for (item in listFromCache) {
                        requestString = requestString + item.cityId.toString() + ","
                    }
                    requestString = requestString.dropLast(1)
                    getWeatherByCityIds(requestString)
            } else {
                // Ожидание возможного заполнения листа запросом локации. Иначе не желателен к показу пользователю при первом запуске
                delay(4000)
                if(weatherReportsList.value == null) {
                    sendErrorMessage("Cached list of cities is empty")
                }
            }
           }

    }

    fun cancelJobs(){
        mainJob?.cancel()
        coordJob?.cancel()
    }


}
