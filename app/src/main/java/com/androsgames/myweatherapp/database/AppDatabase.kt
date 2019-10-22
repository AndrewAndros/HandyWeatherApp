package com.androsgames.myweatherapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.androsgames.myweatherapp.models.CityId
import com.androsgames.myweatherapp.utils.Constants.DATABASE_NAME


@Database(entities = arrayOf(CityId::class), version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cityDao(): CityDao


   companion object {
       private var instance: AppDatabase? = null

       fun getInstance(context: Context): AppDatabase {
           if (instance == null) {
               instance = Room.databaseBuilder(
                   context.applicationContext,
                   AppDatabase::class.java,
                   DATABASE_NAME
               ).build()
           }
           return instance!!
       }
   }




}