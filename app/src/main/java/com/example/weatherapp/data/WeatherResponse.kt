package com.example.weatherapp.data

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val coord: Coord,
    val weather: List<Weather>,
    val main: Main,
    val name: String,
    val wind: Wind,
    val rain: Rain?
)

data class Coord(
    val lon: Double,
    val lat: Double
)

data class Weather(
    val description: String,
    val icon: String
)

data class Main(
    val temp: Float,
    val feels_like: Float,
    val temp_min: Float,
    val temp_max: Float,
    val humidity: Int
)

data class Wind(
    val speed: Float,
    val deg: Float
)

data class Rain(
    @SerializedName("1h") val oneHour: Float?,
    @SerializedName("3h") val threeHours: Float?
)

data class ForecastResponse(
    val list: List<Forecast>
)

data class Forecast(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind,
    val rain: Rain?
)

data class DailyForecast(
    val dayOfWeek: String,
    val avgTemperature: Float,
    val weatherIcon: String,
    val avgRain: Float
)
