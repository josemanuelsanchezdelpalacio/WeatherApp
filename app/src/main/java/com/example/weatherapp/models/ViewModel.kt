package com.example.weatherapp.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.WeatherResponse
import com.example.weatherapp.services.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ViewModel : ViewModel() {
    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather: StateFlow<WeatherResponse?> = _weather

    init{
        fetchWeather("Jaca")
    }

    private fun fetchWeather(city: String){
        viewModelScope.launch {
            val response = RetrofitInstance.api.getCurrentWeather(city, "4e8c5c3d428b37ea7efd0a54096c1fd8")
            _weather.value = response
        }
    }


}

