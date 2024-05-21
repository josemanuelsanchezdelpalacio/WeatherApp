package com.example.weatherapp.screens

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.weatherapp.data.WeatherResponse
import com.example.weatherapp.models.ViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(navController: NavController, mvvm: ViewModel) {
    val clima by mvvm.weather.collectAsState()
    val cityName by mvvm.currentCityName.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            var searchOpen by remember { mutableStateOf(false) }

            TopAppBar(
                title = { Text(text = cityName) },
                actions = {
                    if (searchOpen) {
                        TextField(
                            value = mvvm.searchQuery.collectAsState().value,
                            onValueChange = { mvvm.searchQuery.value = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Buscar ciudad") },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = {
                                mvvm.obtenerClimaPorCiudad(mvvm.searchQuery.value)
                                searchOpen = false
                            })
                        )
                    } else {
                        IconButton(onClick = { searchOpen = true }) {
                            Icon(Icons.Filled.Search, contentDescription = null)
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        ScreenBodyClima(modifier = Modifier.padding(paddingValues), clima = clima, mvvm = mvvm)
    }
}


@Composable
fun ScreenBodyClima(modifier: Modifier, clima: WeatherResponse?, mvvm: ViewModel) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF87CEEB)) // Fondo azul claro
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        clima?.let {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = rememberImagePainter(data = "https://openweathermap.org/img/w/${clima.weather[0].icon}.png"),
                    contentDescription = "Icono del clima",
                    modifier = Modifier.size(150.dp) // Icono en grande
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "${clima.main.temp}ºC",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = clima.weather[0].description.capitalize(),
                    fontSize = 24.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(32.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Sensación térmica",
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Text(
                            text = "${clima.main.feels_like}ºC",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Viento",
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Text(
                            text = "${clima.wind.speed} m/s",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Dirección del viento",
                            fontSize = 18.sp,
                            color = Color.White
                        )
                        Text(
                            text = "${clima.wind.deg}º",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
