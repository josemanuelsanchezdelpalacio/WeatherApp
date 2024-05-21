package com.example.weatherapp.services

import com.example.weatherapp.data.WeatherResponse
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
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

    //obtengo el clima segun las coordenadas
    @GET("weather")
    suspend fun obtenerClimaActualPorCoordenadas(
        @Query("lat") latitud: Double,
        @Query("lon") longitud: Double,
        @Query("appid") ID_API: String,
        @Query("units") unidades: String = "metric",
        @Query("lang") idioma: String = "es"
    ): WeatherResponse
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
