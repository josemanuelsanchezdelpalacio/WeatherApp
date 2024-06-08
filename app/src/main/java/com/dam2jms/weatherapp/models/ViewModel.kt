package com.dam2jms.weatherapp.models

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dam2jms.weatherapp.data.ForecastResponse
import com.dam2jms.weatherapp.data.WeatherResponse
import com.dam2jms.weatherapp.services.RetrofitInstance
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

    fun obtenerUbicacionActual(context: Context) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

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
                viewModelScope.launch {
                    try {
                        val respuestaClima = RetrofitInstance.api.obtenerClimaActualPorCoordenadas(location.latitude, location.longitude, "4e8c5c3d428b37ea7efd0a54096c1fd8")
                        _clima.value = respuestaClima
                        _nombreCiudad.value = respuestaClima.name

                        val respuestaPronostico = RetrofitInstance.api.obtenerPronosticoPorCoordenadas(location.latitude, location.longitude, "4e8c5c3d428b37ea7efd0a54096c1fd8")
                        _pronostico.value = respuestaPronostico
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(context, "No se puede obtener la informacion de la ubicacion actual", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(context, "No se puede obtener la ubicacion actual", Toast.LENGTH_LONG).show()
        }
    }

    fun obtenerClimaPorCiudad(ciudad: String, context: Context) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.obtenerClimaActual(ciudad, "4e8c5c3d428b37ea7efd0a54096c1fd8")
                _clima.value = response
                _nombreCiudad.value = ciudad

                val forecastResponse = RetrofitInstance.api.obtenerPronosticoPorCiudad(ciudad, "4e8c5c3d428b37ea7efd0a54096c1fd8")
                _pronostico.value = forecastResponse
            } catch (e: Exception) {
                Toast.makeText(context, "No se puede obtener la informacion de la ubicacion buscada", Toast.LENGTH_LONG).show()
            }
        }
    }
}
