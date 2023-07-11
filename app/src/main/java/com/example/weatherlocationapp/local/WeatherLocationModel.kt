package com.example.weatherlocationapp.local

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class WeatherLocationModel(
    val cityName : String?,
    val country : String?,
    val isCurrentLocation : Boolean = false,
    val weatherMain : String?,
    val weatherDescription : String?,
    val weatherTemperature : Double?
):Parcelable{
    @PrimaryKey(autoGenerate = true)
    var id : Long = 0L
}

