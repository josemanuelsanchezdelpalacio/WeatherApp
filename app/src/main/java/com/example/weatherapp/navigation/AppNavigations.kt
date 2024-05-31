package com.example.weatherapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.models.ViewModel
import com.example.weatherapp.screens.WeatherScreen
import com.example.weatherapp.screens.PrincipalScreen

@Composable
fun appNavigation() {
    val navController = rememberNavController()
    val mvvm: ViewModel = ViewModel()

    NavHost(navController = navController, startDestination = AppScreens.PrincipalScreen.route) {
        composable(route = AppScreens.PrincipalScreen.route) { PrincipalScreen(navController, mvvm) }
        composable(route = "${AppScreens.WeatherScreen.route}/{ciudad}") { backStackEntry ->
            val ciudad = backStackEntry.arguments?.getString("ciudad") ?: ""
            WeatherScreen(navController, mvvm, ciudad)
        }
    }
}