package com.example.weatherapp.models

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.Forecast
import com.example.weatherapp.data.WeatherResponse
import com.example.weatherapp.services.RetrofitInstance
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ViewModel(application: Application) : AndroidViewModel(application) {
    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather: StateFlow<WeatherResponse?> = _weather
    private val _hourlyForecast = MutableStateFlow<List<Forecast>?>(null)
    val hourlyForecast: StateFlow<List<Forecast>?> = _hourlyForecast
    private val _dailyForecast = MutableStateFlow<List<Forecast>?>(null)
    val dailyForecast: StateFlow<List<Forecast>?> = _dailyForecast

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
            val weatherResponse = RetrofitInstance.api.getCurrentWeatherByCoordinates(lat, lon, "4e8c5c3d428b37ea7efd0a54096c1fd8")
            val hourlyResponse = RetrofitInstance.api.getHourlyForecast(lat, lon, "4e8c5c3d428b37ea7efd0a54096c1fd8")
            val dailyResponse = RetrofitInstance.api.getDailyForecast(lat, lon, "4e8c5c3d428b37ea7efd0a54096c1fd8")
            _weather.value = weatherResponse
            _hourlyForecast.value = hourlyResponse.list
            _dailyForecast.value = dailyResponse.list
        }
    }

    fun getLocationAndFetchWeather(context: Context) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                fetchWeatherByLocation(location.latitude, location.longitude)
            }
        }
    }
}
