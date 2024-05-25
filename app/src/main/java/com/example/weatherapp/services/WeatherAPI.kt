package com.example.weatherapp.services

import com.example.weatherapp.data.ForecastResponse
import com.example.weatherapp.data.WeatherResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {
    // Obtengo info del clima de la ciudad
    @GET("weather")
    suspend fun obtenerClimaActual(
        @Query("q") ciudad: String,
        @Query("appid") ID_API: String,
        @Query("units") unidades: String = "metric",
        @Query("lang") idioma: String = "es"
    ): WeatherResponse

    // Obtengo el clima según las coordenadas
    @GET("weather")
    suspend fun obtenerClimaActualPorCoordenadas(
        @Query("lat") latitud: Double,
        @Query("lon") longitud: Double,
        @Query("appid") ID_API: String,
        @Query("units") unidades: String = "metric",
        @Query("lang") idioma: String = "es"
    ): WeatherResponse

    // Obtengo el pronóstico por ciudad
    @GET("forecast/daily")
    suspend fun obtenerPronosticoPorCiudad(
        @Query("q") ciudad: String,
        @Query("appid") apiKey: String,
        @Query("units") unidades: String = "metric",
        @Query("lang") idioma: String = "es",
        @Query("cnt") dias: Int = 7  // Número de días para el pronóstico
    ): ForecastResponse

    // Obtengo el pronóstico por coordenadas
    @GET("forecast/daily")
    suspend fun obtenerPronosticoPorCoordenadas(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") unidades: String = "metric",
        @Query("lang") idioma: String = "es",
        @Query("cnt") dias: Int = 7  // Número de días para el pronóstico
    ): ForecastResponse
}

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: WeatherAPI by lazy {
        retrofit.create(WeatherAPI::class.java)
    }
}
