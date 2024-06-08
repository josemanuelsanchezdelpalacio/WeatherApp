package com.dam2jms.weatherapp.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dam2jms.weatherapp.models.ViewModel
import com.dam2jms.weatherapp.screens.WeatherScreen

@Composable
fun appNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.WeatherScreen.route) {
        composable(route = AppScreens.WeatherScreen.route) { WeatherScreen(navController, mvvm = ViewModel()) }
    }
}