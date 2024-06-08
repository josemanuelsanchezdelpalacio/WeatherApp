package com.dam2jms.weatherapp.services

import android.telecom.Call
import com.dam2jms.weatherapp.data.ForecastResponse
import com.dam2jms.weatherapp.data.WeatherResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {
    //obtengo info del clima de la ciudad
    @GET("weather")
    suspend fun obtenerClimaActual(
        @Query("q") ciudad: String,
        @Query("appid") ID_API: String,
        @Query("units") unidades: String = "metric",
        @Query("lang") idioma: String = "es"
    ): WeatherResponse

    //obtengo el clima según las coordenadas
    @GET("weather")
    suspend fun obtenerClimaActualPorCoordenadas(
        @Query("lat") latitud: Double,
        @Query("lon") longitud: Double,
        @Query("appid") ID_API: String,
        @Query("units") unidades: String = "metric",
        @Query("lang") idioma: String = "es"
    ): WeatherResponse

    //obtengo el pronostico del clima segun la ciudad
    @GET("forecast")
    suspend fun obtenerPronosticoPorCiudad(
        @Query("q") ciudad: String,
        @Query("appid") ID_API: String,
        @Query("units") unidades: String = "metric",
        @Query("lang") idioma: String = "es"
    ): ForecastResponse

    //obtengo el pronostico del clima segun las coordenadas
    @GET("forecast")
    suspend fun obtenerPronosticoPorCoordenadas(
        @Query("lat") latitud: Double,
        @Query("lon") longitud: Double,
        @Query("appid") ID_API: String,
        @Query("units") unidades: String = "metric",
        @Query("lang") idioma: String = "es"
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