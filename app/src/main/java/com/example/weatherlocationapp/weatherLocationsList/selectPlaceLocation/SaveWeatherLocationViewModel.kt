package com.example.weatherlocationapp.weatherLocationsList.selectPlaceLocation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherlocationapp.WeatherLocationsRepository
import com.example.weatherlocationapp.domain.RequestWeatherLocation
import com.example.weatherlocationapp.local.WeatherLocationDao
import kotlinx.coroutines.launch

class SaveWeatherLocationViewModel(val dataBase: WeatherLocationDao) : ViewModel() {

    private val repository = WeatherLocationsRepository(dataBase)

    private val _navigateBack = MutableLiveData<Boolean>()
    val navigateBack: LiveData<Boolean>
        get() = _navigateBack

    private val _loadingStatus = MutableLiveData<Boolean>(false)
    val loadingStatus: LiveData<Boolean>
        get() = _loadingStatus

    init {
        _navigateBack.value = null
    }

    fun validateAndSaveLocation(item: RequestWeatherLocation) {
        if (validateEnteredData(item)) {
            viewModelScope.launch {
                _loadingStatus.value = true
                item.cityName?.let {
                    val result = repository.getWeatherByCityName(it)
                    if (result != null)
                        _navigateBack.value = true
                    else {
                        repository.refreshWeather(item)
                        _navigateBack.value = true
                        _loadingStatus.value = false
                    }
                }

            }

        }

    }

    fun navigationCompleted() {
        _navigateBack.value = null
    }

    private fun validateEnteredData(item: RequestWeatherLocation): Boolean {
        return !item.cityName.isNullOrBlank() && item.lat.isNotZero() && item.log.isNotZero()
    }


    private fun Double.isNotZero(): Boolean {
        return this > 0.0
    }

}

class SaveWeatherLocationViewModelFactory(
    private val dataSource: WeatherLocationDao,
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SaveWeatherLocationViewModel::class.java)) {
            return SaveWeatherLocationViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}