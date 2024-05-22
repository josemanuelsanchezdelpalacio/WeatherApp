package com.example.weatherapp.data

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val coord: Coordenadas,
    val weather: List<Clima>,
    val main: Principal,
    val name: String,
    val wind: Viento,
    val rain: Lluvia?,
    val pronostico: List<PronosticoDiario>?
)

data class Coordenadas(
    val lon: Double,
    val lat: Double
)

data class Clima(
    val description: String,
    val icon: String
)

data class Principal(
    val temp: Float,
    val feels_like: Float,
    val temp_min: Float,
    val temp_max: Float,
    val humidity: Int
)

data class Viento(
    val speed: Float,
    val deg: Float
)

data class Lluvia(
    @SerializedName("1h") val unaHora: Float?,
    @SerializedName("3h") val tresHoras: Float?
)

data class PronosticoDiario(
    val dt: Long,
    val temp: Temperatura,
    val weather: List<Clima>,
    val rain: Float?
)

data class Temperatura(
    val day: Float,
    val min: Float,
    val max: Float
)