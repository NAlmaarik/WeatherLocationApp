package com.example.weatherlocationapp.weatherDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherlocationapp.WeatherLocationsRepository
import com.example.weatherlocationapp.local.WeatherLocationDao
import kotlinx.coroutines.launch

class WeatherDetailsViewModel(private val dataBase : WeatherLocationDao) : ViewModel() {

    private val _navigateBack = MutableLiveData<Boolean>()
    val navigateBack : LiveData<Boolean>
        get() = _navigateBack

    fun deleteItem(id : Long){
        viewModelScope.launch {
            WeatherLocationsRepository(dataBase).deleteWeatherLocationById(id)
            _navigateBack.value = true
        }
    }

    fun navigateBackCompleted(){
        _navigateBack.value = null
    }
}

class WeatherDetailsViewModelFactory(
    private val dataSource: WeatherLocationDao,
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherDetailsViewModel::class.java)) {
            return WeatherDetailsViewModel(dataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}