package com.example.weatherapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.models.ViewModel
import com.example.weatherapp.screens.WeatherScreen

@Composable
fun appNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppScreens.WeatherScreen.route) {
        composable(route = AppScreens.WeatherScreen.route) {
            WeatherScreen(navController, mvvm = ViewModel())
        }
    }
}