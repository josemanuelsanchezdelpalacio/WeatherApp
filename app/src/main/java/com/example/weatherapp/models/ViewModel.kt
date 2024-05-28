package com.example.weatherapp.models

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.ForecastResponse
import com.example.weatherapp.data.WeatherResponse
import com.example.weatherapp.services.RetrofitInstance
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

    /**para obtener la ubicacion actual**/
    fun obtenerUbicacionActual() {
        val ubicacion = LocationServices.getFusedLocationProviderClient(getApplication<Application>().applicationContext)

        //compruebo los permisos para la ubicacion
        if (ActivityCompat.checkSelfPermission(
                getApplication(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getApplication(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) { return }

        //obtengo la ubicacion por la longitud y latitud
        ubicacion.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                obtenerClimaPorUbicacion(location.latitude, location.longitude)
            }
        }.addOnFailureListener {}
    }

    /**para obtener el clima de la ciudad buscandola por el nombre y el pronostico de los siguientes dias**/
    fun obtenerClimaPorCiudad(ciudad: String) {
        viewModelScope.launch {
            try {
                val respuestaClima = RetrofitInstance.api.obtenerClimaActual(ciudad, "4e8c5c3d428b37ea7efd0a54096c1fd8")
                _clima.value = respuestaClima
                _nombreCiudad.value = ciudad

                val respuestaPronostico = RetrofitInstance.api.obtenerPronosticoPorCiudad(ciudad, "4e8c5c3d428b37ea7efd0a54096c1fd8")
                _pronostico.value = respuestaPronostico
            } catch (e: Exception) { }
        }
    }

    /**para obtener el clima de la ciudad de la ubicacion actual del usuario y el pronostico de los siguientes dias**/
    fun obtenerClimaPorUbicacion(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val respuestaClima = RetrofitInstance.api.obtenerClimaActualPorCoordenadas(lat, lon, "4e8c5c3d428b37ea7efd0a54096c1fd8")
                _clima.value = respuestaClima
                _nombreCiudad.value = respuestaClima.name

                val respuestaPronostico = RetrofitInstance.api.obtenerPronosticoPorCoordenadas(lat, lon, "4e8c5c3d428b37ea7efd0a54096c1fd8")
                _pronostico.value = respuestaPronostico
            } catch (e: Exception) {}
        }
    }
}
