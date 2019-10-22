package com.androsgames.myweatherapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BulkResponse (

    @SerializedName("cnt")
    @Expose
    var count : Int,

    @SerializedName("list")
    @Expose
    var reports : List<WeatherData>
)