package com.example.weatherapp.navigation

sealed class AppScreens (val route: String){
    object WeatherScreen: AppScreens(route = "weather_screen")
}
