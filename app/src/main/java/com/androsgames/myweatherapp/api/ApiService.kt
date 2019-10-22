package com.androsgames.myweatherapp.api

import com.androsgames.myweatherapp.models.BulkResponse
import com.androsgames.myweatherapp.models.WeatherData
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {


    @GET("weather?")
    suspend fun getWeatherByCityName(
        @Query("q") cityName: String, @Query("APPID") apiKey: String
    ): WeatherData?

    @GET("weather?")
    suspend fun getWeatherByCoordinates(
        @Query("lat") lat: Double,  @Query("lon") lon: Double, @Query("APPID") apiKey: String
    ): WeatherData?

    @GET("group?")
    suspend fun getWeatherReportsByIds(
        @Query("id") cities: String, @Query("APPID") apiKey: String
    ): BulkResponse?

}