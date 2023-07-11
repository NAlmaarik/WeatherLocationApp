package com.example.weatherlocationapp

import androidx.lifecycle.LiveData
import com.example.weatherlocationapp.domain.RequestWeatherLocation
import com.example.weatherlocationapp.local.WeatherLocationDao
import com.example.weatherlocationapp.local.WeatherLocationModel
import com.example.weatherlocationapp.remote.WeatherApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeatherLocationsRepository(val database: WeatherLocationDao) {

    suspend fun refreshWeather(weatherItem: RequestWeatherLocation) {
        withContext(Dispatchers.IO) {
            val result = WeatherApi.retrofitService.getWeatherInfo(weatherItem.lat, weatherItem.log)
            database.insertWeatherLocation(
                WeatherLocationModel(
                    weatherItem.cityName,
                    result.sys?.country,
                    weatherItem.isCurrentLocation,
                    result.weather?.get(0)?.main,
                    result.weather?.get(0)?.description,
                    result.main?.temp
                )
            )
        }
    }

     fun getAllWeatherLocationsList(): LiveData<List<WeatherLocationModel>> =
         database.getAllWeatherLocations()

    suspend fun getWeatherByCityName(cityName:String): WeatherLocationModel? {
        val result : WeatherLocationModel?
        withContext(Dispatchers.IO){
             result = database.getWeatherByCityName(cityName)
        }
        return result
    }


    suspend fun deleteWeatherLocationById(id:Long) {
        withContext(Dispatchers.IO) {
            database.deleteWeatherLocationById(id)
        }
    }

    suspend fun deleteAllWeatherLocations() {
        withContext(Dispatchers.IO) {
            database.deleteAll()
        }
    }
}