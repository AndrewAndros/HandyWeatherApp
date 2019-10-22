package com.androsgames.myweatherapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city_id_collection")


data class CityId (

    @PrimaryKey
    val cityId : Int
)






