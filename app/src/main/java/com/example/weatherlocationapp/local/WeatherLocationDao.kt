package com.example.weatherlocationapp.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query


@Dao
interface WeatherLocationDao {

    //insert weather location
    @Insert(onConflict = REPLACE)
    suspend fun insertWeatherLocation(weatherLocationModel: WeatherLocationModel)

    // get all weather locations
    @Query("SELECT * FROM WeatherLocationModel")
    fun getAllWeatherLocations(): LiveData<List<WeatherLocationModel>>

    // delete by id
    @Query("DELETE FROM WeatherLocationModel WHERE id=:id")
    suspend fun deleteWeatherLocationById(id: Long)

    // delete all weather locations
    @Query("DELETE FROM WeatherLocationModel")
    suspend fun deleteAll()

    //get row if there is matching city
    @Query("SELECT * FROM WeatherLocationModel WHERE cityName=:cityName")
    suspend fun getWeatherByCityName(cityName: String): WeatherLocationModel?
}