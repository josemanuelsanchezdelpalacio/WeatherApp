package com.example.weatherapp.models

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.Coordenadas
import com.example.weatherapp.data.WeatherResponse
import com.example.weatherapp.services.RetrofitInstance
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ViewModel(application: Application) : AndroidViewModel(application) {

    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather: StateFlow<WeatherResponse?> = _weather

    private val _currentCityName = MutableStateFlow("")
    val currentCityName: StateFlow<String> = _currentCityName

//    init {
//        getCurrentLocation()
//    }

    fun getCurrentLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplication<Application>().applicationContext)

        if (ActivityCompat.checkSelfPermission(
                getApplication(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getApplication(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Handle the case where location permissions are not granted
            // You might want to notify the user or request permissions
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                // Use location.latitude and location.longitude
                obtenerClimaPorUbicacion(location.latitude, location.longitude)
            } else {
                // Handle case where last known location is not available
                // You can request a new location update here if needed
            }
        }.addOnFailureListener { exception ->
            // Handle any errors that might occur when obtaining the location
        }
    }

    fun obtenerClimaPorCiudad(ciudad: String) {
        viewModelScope.launch {
            try {
                val respuesta = RetrofitInstance.api.obtenerClimaActual(ciudad, "4e8c5c3d428b37ea7efd0a54096c1fd8")
                _weather.value = respuesta
                _currentCityName.value = respuesta.name
            } catch (e: Exception) {
                // Handle errors
            }
        }
    }

    private fun obtenerClimaPorUbicacion(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val respuestaClima = RetrofitInstance.api.obtenerClimaActualPorCoordenadas(lat, lon, "4e8c5c3d428b37ea7efd0a54096c1fd8")
                _weather.value = respuestaClima
                _currentCityName.value = respuestaClima.name
            } catch (e: Exception) {
                // Handle errors
            }
        }
    }
}
