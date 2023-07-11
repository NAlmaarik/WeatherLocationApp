package com.example.weatherlocationapp.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase



@Database(entities = [WeatherLocationModel::class], version = 1)
abstract class WeatherLocationDatabase : RoomDatabase() {

    abstract val weatherLocationDao:WeatherLocationDao
    companion object {
        @Volatile
        private var INSTANCE: WeatherLocationDatabase? = null
        fun getInstance(context: Context): WeatherLocationDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        WeatherLocationDatabase::class.java,
                        "WeatherLocationDatabase"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}