package com.dam2jms.factoriaproyectosfp24retrofit.navigation

sealed class AppScreens (val route: String){
    object LoginScreen: AppScreens(route = "login_screen")
    object RegisterScreen: AppScreens(route = "register_screen")
    object HomeScreen: AppScreens(route = "home_screen")
    object AñadirProyecto: AppScreens(route = "añadir_proyecto")
}
