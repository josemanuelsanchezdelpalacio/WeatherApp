package com.dam2jms.weatherapp.navigation

sealed class AppScreens (val route: String){
    object WeatherScreen: AppScreens(route = "weather_screen")
}
