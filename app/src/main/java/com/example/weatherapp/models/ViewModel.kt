package com.example.weatherapp.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.WeatherResponse
import com.example.weatherapp.services.RetrofitInstance
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ViewModel(application: Application) : AndroidViewModel(application) {
    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather: StateFlow<WeatherResponse?> = _weather

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)

    init {
        fetchWeather("Jaca")
    }

    private fun fetchWeather(city: String) {
        viewModelScope.launch {
            val response = RetrofitInstance.api.getCurrentWeather(city, "4e8c5c3d428b37ea7efd0a54096c1fd8")
            _weather.value = response
        }
    }

    fun fetchWeatherByLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            val response = RetrofitInstance.api.getCurrentWeatherByCoordinates(lat, lon, "4e8c5c3d428b37ea7efd0a54096c1fd8")
            _weather.value = response
        }
    }

    fun getLocationAndFetchWeather() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                fetchWeatherByLocation(location.latitude, location.longitude)
            }
        }
    }
}