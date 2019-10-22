package com.androsgames.myweatherapp.models


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize



@Parcelize
data class WeatherData(


var coord : Coordinates,
var main : Main,
var wind : Wind,
var id : Int,
var dt : Long,
var sys : Sys,
var timezone : Long,
var name : String,
var clouds : Clouds,
var weather : Array<Weather>

) : Parcelable {

    @Parcelize
    data class Wind (
        var speed : Double,
        var deg : Double
    ) : Parcelable

    @Parcelize
    data class Main (
        var temp : Double,
        var pressure : Double,
        var humidity : Double
    ) : Parcelable

    @Parcelize
    data class Clouds (
        var all : Int
    ) : Parcelable

    @Parcelize
    data class Coordinates(
        var lon : Double,
        var lat : Double
    ) : Parcelable

    @Parcelize
    data class Weather (
        var id : Int,
        var main : String,
        var description : String,
        var icon : String
    ) : Parcelable

    @Parcelize
    data class Sys (
        var sunrise : Long,
        var sunset : Long
    ) : Parcelable

    override fun toString(): String {
        return "WeatherData(coord=$coord, main=$main, wind=$wind, id=$id, dt=$dt, sys=$sys, timezone=$timezone, name='$name', clouds=$clouds, weather=${weather.contentToString()})"
    }


}