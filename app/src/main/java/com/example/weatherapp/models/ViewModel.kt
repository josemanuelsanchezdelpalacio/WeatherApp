package com.example.weatherapp.models

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.PronosticoSemanalResponse
import com.example.weatherapp.data.WeatherResponse
import com.example.weatherapp.services.RetrofitInstance
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class ViewModel(application: Application) : AndroidViewModel(application) {
    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather: StateFlow<WeatherResponse?> = _weather

    private val _pronosticoSemanal = MutableStateFlow<PronosticoSemanalResponse?>(null)
    val pronosticoSemanal: StateFlow<PronosticoSemanalResponse?> = _pronosticoSemanal

    val searchQuery = MutableStateFlow("")

    //obtener ubicacion actual del usuario
    private val clienteUbicacionFused = LocationServices.getFusedLocationProviderClient(application)

    //obtener el clima de una ciudad especifica
    fun obtenerClima(ciudad: String) {
        viewModelScope.launch {
            //llamada a la API para obtener la informacion
            val respuesta = RetrofitInstance.api.obtenerClimaActual(ciudad, "4e8c5c3d428b37ea7efd0a54096c1fd8")
            _weather.value = respuesta
        }
    }

    //para obtener el clima usando coordenadas
    fun obtenerClimaPorUbicacion(lat: Double, lon: Double) {
        viewModelScope.launch {
            val respuestaClima = RetrofitInstance.api.obtenerClimaActualPorCoordenadas(lat, lon, "4e8c5c3d428b37ea7efd0a54096c1fd8")
            _weather.value = respuestaClima
        }
    }

    //obtener la ubicacion actual y obtener su clima correspondiente
    fun obtenerUbicacionYClima(context: Context) {
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
        clienteUbicacionFused.lastLocation.addOnSuccessListener { location ->
            location?.let {
                obtenerClimaPorUbicacion(it.latitude, it.longitude)
            }
        }
    }

    fun obtenerPronosticoSemanal(ciudad: String) {
        viewModelScope.launch {
            val respuesta = RetrofitInstance.api.obtenerPronosticoSemanal(ciudad, "4e8c5c3d428b37ea7efd0a54096c1fd8")
            _pronosticoSemanal.value = respuesta
        }
    }

    fun obtenerPronosticoSemanalPorUbicacion(lat: Double, lon: Double) {
        viewModelScope.launch {
            val respuesta = RetrofitInstance.api.obtenerPronosticoSemanalPorCoordenadas(lat, lon, "4e8c5c3d428b37ea7efd0a54096c1fd8")
            _pronosticoSemanal.value = respuesta
        }
    }

}
