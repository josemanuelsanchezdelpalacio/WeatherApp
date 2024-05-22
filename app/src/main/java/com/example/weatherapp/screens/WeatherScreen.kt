package com.example.weatherapp.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.weatherapp.data.WeatherResponse
import com.example.weatherapp.models.ViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(navController: NavController, mvvm: ViewModel) {
    val clima by mvvm.weather.collectAsState()
    val cityName by mvvm.currentCityName.collectAsState()

    Scaffold(
        topBar = {
            var searchOpen by remember { mutableStateOf(false) }
            var cityToSearch by remember { mutableStateOf("") }

            CenterAlignedTopAppBar(
                title = { Text(text = cityName, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                actions = {
                    if (searchOpen) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextField(
                                value = cityToSearch,
                                onValueChange = { cityToSearch = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Buscar ciudad") },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(onSearch = {
                                    mvvm.obtenerClimaPorCiudad(cityToSearch)
                                    searchOpen = false
                                })
                            )
                        }
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
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = rememberImagePainter(data = "https://openweathermap.org/img/w/${clima.weather[0].icon}.png"),
                    contentDescription = "Icono del clima",
                    modifier = Modifier.size(150.dp) // Icono grande
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
                Spacer(modifier = Modifier.height(24.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Sensación térmica: ${clima?.main?.feels_like}ºC",
                        fontSize = 18.sp,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Text(
                        text = "Velocidad del viento: ${(clima.wind.speed * 3.6).roundToInt()} km/h",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Dirección del viento",
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    WindDirectionArrow(degrees = clima.wind.deg)
                }
            }
        }
    }
}

@Composable
fun WindDirectionArrow(degrees: Float) {
    Canvas(
        modifier = Modifier.size(24.dp),
        onDraw = {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val arrowLength = size.width / 2

            // Calcular el punto final de la flecha según los grados
            val endX = centerX + arrowLength * cos(Math.toRadians(degrees.toDouble())).toFloat()
            val endY = centerY - arrowLength * sin(Math.toRadians(degrees.toDouble())).toFloat()

            // Dibujar la línea de la flecha
            drawLine(
                color = Color.White,
                start = Offset(centerX, centerY),
                end = Offset(endX, endY),
                strokeWidth = 2.dp.toPx()
            )
        }
    )
}