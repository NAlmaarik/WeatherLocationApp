package com.example.weatherlocationapp.weatherLocationsList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherlocationapp.WeatherLocationsRepository
import com.example.weatherlocationapp.local.WeatherLocationDao
import com.example.weatherlocationapp.local.WeatherLocationModel
import kotlinx.coroutines.launch


class WeatherLocationsListViewModel(private val dataBase: WeatherLocationDao) : ViewModel() {

    private val repository = WeatherLocationsRepository(dataBase)
    val weatherLocationsList = repository.getAllWeatherLocationsList()

    private val _navigateToWeatherDetails = MutableLiveData<WeatherLocationModel>()
    val navigateToDetails: LiveData<WeatherLocationModel>
        get() = _navigateToWeatherDetails

    fun navToDetails(item: WeatherLocationModel) {
        _navigateToWeatherDetails.value = item
    }

    fun navToDetailsCompleted() {
        _navigateToWeatherDetails.value = null
    }

    fun deleteAll(){
        viewModelScope.launch {
            repository.deleteAllWeatherLocations()
        }
    }

}

class WeatherLocationsListViewModelFactory(
    private val dataSource: WeatherLocationDao,
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherLocationsListViewModel::class.java)) {
            return WeatherLocationsListViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}