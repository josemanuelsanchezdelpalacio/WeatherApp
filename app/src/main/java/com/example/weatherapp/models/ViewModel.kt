package com.example.weatherapp.models

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.Coordenadas
import com.example.weatherapp.data.ForecastResponse
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

    private val _clima = MutableStateFlow<WeatherResponse?>(null)
    val clima: StateFlow<WeatherResponse?> = _clima

    private val _pronostico = MutableStateFlow<ForecastResponse?>(null)
    val pronostico: StateFlow<ForecastResponse?> = _pronostico

    private val _nombreCiudad = MutableStateFlow("")
    val nombreCiudad: StateFlow<String> = _nombreCiudad

    init {
        obtenerUbicacionActual()
    }

    //intento obtener la ubicacion real
    fun obtenerUbicacionActual() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplication<Application>().applicationContext)

        if (ActivityCompat.checkSelfPermission(
                getApplication(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getApplication(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                obtenerClimaPorUbicacion(location.latitude, location.longitude)
            } else {
            }
        }.addOnFailureListener { exception ->
        }
    }

    fun obtenerClimaPorCiudad(ciudad: String) {
        viewModelScope.launch {
            try {
                val respuesta = RetrofitInstance.api.obtenerClimaActual(ciudad, "4e8c5c3d428b37ea7efd0a54096c1fd8")
                _clima.value = respuesta
                _nombreCiudad.value = respuesta.name
            } catch (e: Exception) {}
        }
    }

    fun obtenerClimaPorUbicacion(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val respuestaClima = RetrofitInstance.api.obtenerClimaActualPorCoordenadas(lat, lon, "4e8c5c3d428b37ea7efd0a54096c1fd8")
                _clima.value = respuestaClima
                _nombreCiudad.value = respuestaClima.name
            } catch (e: Exception) {}
        }
    }
}
