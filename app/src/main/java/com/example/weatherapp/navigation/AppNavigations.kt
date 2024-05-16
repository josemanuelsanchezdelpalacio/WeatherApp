package com.dam2jms.factoriaproyectosfp24retrofit.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dam2jms.factoriaproyectosfp24retrofit.models.ViewModelLogin
import com.dam2jms.factoriaproyectosfp24retrofit.models.ViewModelHome
import com.dam2jms.factoriaproyectosfp24retrofit.models.ViewModelRegister
import com.dam2jms.factoriaproyectosfp24retrofit.screens.AñadirProyecto
import com.dam2jms.factoriaproyectosfp24retrofit.screens.HomeScreen
import com.dam2jms.factoriaproyectosfp24retrofit.screens.LoginScreen
import com.dam2jms.factoriaproyectosfp24retrofit.screens.RegisterScreen

@Composable
fun appNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.LoginScreen.route) {
        composable(route = AppScreens.LoginScreen.route) { LoginScreen(navController, mvvm = ViewModelLogin()) }
        composable(route = AppScreens.RegisterScreen.route) { RegisterScreen(navController, mvvm = ViewModelRegister()) }
        composable(route = AppScreens.HomeScreen.route) { HomeScreen(navController, mvvm = ViewModelHome()) }
        composable(route = AppScreens.AñadirProyecto.route) {AñadirProyecto(navController, mvvm = ViewModelHome())}
    }
}