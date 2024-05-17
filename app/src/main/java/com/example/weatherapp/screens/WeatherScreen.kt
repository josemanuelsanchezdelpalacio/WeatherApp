package com.example.weatherapp.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.weatherapp.data.Forecast
import com.example.weatherapp.data.WeatherResponse
import com.example.weatherapp.models.ViewModel
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(navController: NavController, mvvm: ViewModel) {
    val weather by mvvm.weather.collectAsState()
    val hourlyForecast by mvvm.hourlyForecast.collectAsState()
    val dailyForecast by mvvm.dailyForecast.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        mvvm.getLocationAndFetchWeather(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = weather?.name ?: "WEATHER") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Implement search functionality */ }) {
                        Icon(Icons.Filled.Search, contentDescription = null)
                    }
                }
            )
        }
    ) { paddingValues ->
        WeatherBodyScreen(
            modifier = Modifier.padding(paddingValues),
            weather = weather,
            hourlyForecast = hourlyForecast,
            dailyForecast = dailyForecast
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherBodyScreen(
    modifier: Modifier,
    weather: WeatherResponse?,
    hourlyForecast: List<Forecast>?,
    dailyForecast: List<Forecast>?
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .background(Color.White)
    ) {
        weather?.let {
            Card(modifier = Modifier.padding(vertical = 8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Descripción: ${it.weather[0].description}")
                    Text("Temperatura: ${it.main.temp}ºC")
                    Text("Sensación térmica: ${it.main.feels_like}ºC")
                    Text("Probabilidad de lluvia: ${it.rain?.oneHour ?: 0}%")
                    Image(
                        painter = rememberImagePainter(data = "https://openweathermap.org/img/w/${it.weather[0].icon}.png"),
                        contentDescription = "Weather icon"
                    )
                }
            }

            // Hourly forecast card
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Temperaturas por horas", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    hourlyForecast?.let { forecasts ->
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState())
                        ) {
                            forecasts.take(24).forEach { forecast ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                ) {
                                    Text(text = "${forecast.main.temp}ºC")
                                    Image(
                                        painter = rememberImagePainter(data = "https://openweathermap.org/img/w/${forecast.weather[0].icon}.png"),
                                        contentDescription = "Hourly weather icon"
                                    )
                                    Text(text = "${SimpleDateFormat("HH:mm").format(Date(forecast.dt * 1000))}")
                                }
                            }
                        }
                    }
                }
            }

            // Daily forecast card
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Previsión por días", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    dailyForecast?.let { forecasts ->
                        forecasts.take(7).forEach { forecast ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = SimpleDateFormat("EEE, MMM d").format(Date(forecast.dt * 1000)))
                                Image(
                                    painter = rememberImagePainter(data = "https://openweathermap.org/img/w/${forecast.weather[0].icon}.png"),
                                    contentDescription = "Daily weather icon"
                                )
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "Máx: ${forecast.main.temp_max}ºC")
                                    Text(text = "Mín: ${forecast.main.temp_min}ºC")
                                    Text(text = "Viento: ${forecast.wind.speed} m/s")
                                    Text(text = "Lluvia: ${forecast.rain?.oneHour ?: 0}%")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

