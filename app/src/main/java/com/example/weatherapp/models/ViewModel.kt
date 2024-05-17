package com.example.weatherapp.models

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.DailyForecast
import com.example.weatherapp.data.Forecast
import com.example.weatherapp.data.WeatherResponse
import com.example.weatherapp.services.RetrofitInstance
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

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

    // Función para obtener el nombre del día de la semana a partir de una fecha
    fun getDayOfWeekName(date: Date): String {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val dayOfWeekNames = arrayOf("Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado")
        return dayOfWeekNames[dayOfWeek - 1]
    }

    // Función para obtener las previsiones y temperaturas por día a partir de la lista de previsiones
    fun getDailyForecasts(forecasts: List<Forecast>): List<DailyForecast> {
        val today = Calendar.getInstance()
        val dailyForecasts = mutableListOf<DailyForecast>()

        // Obtener el día siguiente a hoy
        today.add(Calendar.DAY_OF_YEAR, 1)

        // Iterar por cada día de la semana
        repeat(7) {
            // Filtrar las previsiones para el día actual
            val dayForecasts = forecasts.filter { forecast ->
                val forecastDate = Calendar.getInstance()
                forecastDate.timeInMillis = forecast.dt * 1000
                forecastDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
            }

            // Si hay previsiones para el día actual, calcular la temperatura promedio
            val avgTemp = if (dayForecasts.isNotEmpty()) {
                dayForecasts.map { it.main.temp }.average().toFloat()
            } else {
                Float.NaN
            }

            // Agregar la previsiones y temperaturas por día a la lista
            dailyForecasts.add(DailyForecast(getDayOfWeekName(today.time), avgTemp))

            // Mover al siguiente día
            today.add(Calendar.DAY_OF_YEAR, 1)
        }

        return dailyForecasts
    }

}
