package com.example.weatherapp.models

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.ForecastResponse
import com.example.weatherapp.data.WeatherResponse
import com.example.weatherapp.services.RetrofitInstance
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ViewModel() : ViewModel() {

    private val _clima = MutableStateFlow<WeatherResponse?>(null)
    val clima: StateFlow<WeatherResponse?> = _clima

    private val _pronostico = MutableStateFlow<ForecastResponse?>(null)
    val pronostico: StateFlow<ForecastResponse?> = _pronostico

    private val _nombreCiudad = MutableStateFlow("")
    val nombreCiudad: StateFlow<String> = _nombreCiudad


    /**para obtener el clima de la ciudad de la ubicacion actual del usuario y el pronostico de los siguientes dias**/
    fun obtenerClimaActual(context: Context) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) { return }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                viewModelScope.launch {
                    try {
                        val lat = location.latitude
                        val lon = location.longitude

                        val respuestaClima = RetrofitInstance.api.obtenerClimaActualPorCoordenadas(lat, lon, "4e8c5c3d428b37ea7efd0a54096c1fd8")
                        _clima.value = respuestaClima
                        _nombreCiudad.value = respuestaClima.name

                        val respuestaPronostico = RetrofitInstance.api.obtenerPronosticoPorCoordenadas(lat, lon, "4e8c5c3d428b37ea7efd0a54096c1fd8")
                        _pronostico.value = respuestaPronostico

                    } catch (e: Exception) {
                        // Maneja errores aquí
                        Log.e("ViewModel", "Error obteniendo el clima", e)
                    }
                }
            }
        }.addOnFailureListener {
            Log.e("ViewModel", "Error obteniendo la ubicación", it)
        }
    }


    /**para obtener el clima de la ciudad buscandola por el nombre y el pronostico de los siguientes dias**/
    fun obtenerClimaPorCiudad(ciudad: String) {
        viewModelScope.launch {

            val respuestaClima = RetrofitInstance.api.obtenerClimaActual(ciudad, "4e8c5c3d428b37ea7efd0a54096c1fd8")
            _clima.value = respuestaClima
            _nombreCiudad.value = ciudad

            val respuestaPronostico = RetrofitInstance.api.obtenerPronosticoPorCiudad(ciudad, "4e8c5c3d428b37ea7efd0a54096c1fd8")
            _pronostico.value = respuestaPronostico

        }
    }
}
