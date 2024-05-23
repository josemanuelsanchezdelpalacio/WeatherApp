package com.example.weatherapp.data

import com.google.gson.annotations.SerializedName

/** Representa la respuesta de la API con toda la información. */
data class WeatherResponse(
    val coord: Coordenadas,
    val weather: List<Clima>,
    val main: Principal,
    val name: String,
    val wind: Viento,
    val rain: Lluvia?
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

/** Representa la información sobre las precipitaciones de la última hora. */
data class Lluvia(
    @SerializedName("1h") val unaHora: Float?,
)