package com.example.weatherapp.models

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.Coordenadas
import com.example.weatherapp.data.PronosticoSemanalResponse
import com.example.weatherapp.data.WeatherResponse
import com.example.weatherapp.services.RetrofitInstance
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class ViewModel(application: Application) : AndroidViewModel(application) {
    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather: StateFlow<WeatherResponse?> = _weather
    val searchQuery = MutableStateFlow("")

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var locationRequired: Boolean = false


    private val _currentLocation = MutableStateFlow<Coordenadas?>(null)
    val currentLocation: StateFlow<Coordenadas?> = _currentLocation

    @SuppressLint("MissingPermission")
    fun ubicacionInicial() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 100
        )
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(3000)
            .setMaxUpdateDelayMillis(100)
            .build()

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun initLocationClient(context: Context) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            for (location in p0.locations) {
                _currentLocation.value = Coordenadas(location.latitude, location.longitude)
                obtenerClimaPorUbicacion(location.latitude, location.longitude)
            }
        }
    }

    fun obtenerClimaPorCiudad(ciudad: String) {
        viewModelScope.launch {
            val respuesta = RetrofitInstance.api.obtenerClimaActual(ciudad, "4e8c5c3d428b37ea7efd0a54096c1fd8")
            _weather.value = respuesta
        }
    }

    fun obtenerClimaPorUbicacion(lat: Double, lon: Double) {
        viewModelScope.launch {
            val respuestaClima = RetrofitInstance.api.obtenerClimaActualPorCoordenadas(lat, lon, "4e8c5c3d428b37ea7efd0a54096c1fd8")
            _weather.value = respuestaClima
        }
    }
}



