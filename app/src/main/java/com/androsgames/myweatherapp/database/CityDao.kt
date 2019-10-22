package com.androsgames.myweatherapp.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.androsgames.myweatherapp.models.CityId


@Dao
interface CityDao {

    @Query("SELECT * FROM city_id_collection")
    fun getAll(): List<CityId>?

    @Insert (onConflict = REPLACE)
    fun insertCityId(cityId : CityId)

    @Delete
    fun deleteCityFromDbCache(cityId : CityId)
}