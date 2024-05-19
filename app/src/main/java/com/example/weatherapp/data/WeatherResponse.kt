package com.example.weatherapp.data

import com.google.gson.annotations.SerializedName

//clase que representa la info del clima
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
    //temperatura actual
    val temp: Float,
    //sensacion termica
    val feels_like: Float,
    val temp_min: Float,
    val temp_max: Float,
    val humidity: Int
)

data class Viento(
    val speed: Float,
    //direccion del viento
    val deg: Float
)

data class Lluvia(
    //cantidad de lluvia en la ultima hora
    @SerializedName("1h") val unaHora: Float?,
    //cantidad de lluvia en las ultimas 3 horas
    @SerializedName("3h") val tresHoras: Float?
)

data class PronosticoSemanalResponse(
    val city: Ciudad,
    val list: List<PronosticoDiario>
)

data class Ciudad(
    val name: String,
    val coord: Coordenadas
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

