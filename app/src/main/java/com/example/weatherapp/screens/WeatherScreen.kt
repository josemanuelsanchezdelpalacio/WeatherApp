package com.example.weatherapp.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.weatherapp.R
import com.example.weatherapp.data.ForecastResponse
import com.example.weatherapp.data.WeatherResponse
import com.example.weatherapp.models.ViewModel
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(navController: NavController, mvvm: ViewModel) {
    val clima by mvvm.clima.collectAsState()
    val pronostico by mvvm.pronostico.collectAsState()
    val nombreCiudad by mvvm.nombreCiudad.collectAsState()

    Scaffold(
        topBar = {
            var busqueda by remember { mutableStateOf(false) }
            var ciudad by remember { mutableStateOf("") }

            TopAppBar(
                title = { Text(text = nombreCiudad, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Transparent),
                actions = {
                    if (busqueda) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextField(
                                value = ciudad,
                                onValueChange = { ciudad = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Buscar ciudad") },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(onSearch = {
                                    mvvm.obtenerClimaPorCiudad(ciudad)
                                    busqueda = false
                                })
                            )
                        }
                    } else {
                        IconButton(onClick = { busqueda = true }) {
                            Icon(Icons.Filled.Search, contentDescription = null)
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        ScreenBodyClima(modifier = Modifier.padding(paddingValues), clima = clima, pronostico = pronostico, mvvm = mvvm)
    }
}

@Composable
fun ScreenBodyClima(modifier: Modifier, clima: WeatherResponse?, pronostico: ForecastResponse?, mvvm: ViewModel) {
    val gradiente = Brush.verticalGradient(
        colors = listOf(Color(0xFF040569), Color(0xFF141E74), Color(0xFF1C2C99)),
        startY = 0.0f,
        endY = 1000.0f
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = gradiente)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        clima?.let {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.5f))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                            Text(
                                text = "${clima.main.temp.toInt()}ºC",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row {
                                Text(
                                    text = "Max: ${clima.main.temp_max.toInt()}ºC",
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Min: ${clima.main.temp_min.toInt()}ºC",
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Viento: ${clima.wind.speed} km/h",
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                direccionVientoFlecha(clima.wind.deg)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Humedad: ${clima.main.humidity}%",
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Image(
                                painter = rememberImagePainter(data = "https://openweathermap.org/img/w/${clima.weather[0].icon}.png"),
                                contentDescription = "Icono del clima",
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = clima.weather[0].description.capitalize(),
                                fontSize = 16.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
        pronostico?.forecastday?.let { forecastDays ->
            if (forecastDays.isNotEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Pronóstico",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyRow {
                        items(forecastDays) { forecastDay ->
                            DiaCard(forecastDay)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DiaCard(pronostico: ForecastResponse.ForecastDay) {
    Card(
        modifier = Modifier.padding(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = pronostico.date,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = rememberImagePainter(data = "https:${pronostico.day.condition.icon}"),
                contentDescription = "Icono del clima",
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Max: ${pronostico.day.maxtempC}ºC",
                fontSize = 14.sp,
                color = Color.Black
            )
            Text(
                text = "Min: ${pronostico.day.mintempC}ºC",
                fontSize = 14.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
fun direccionVientoFlecha(degrees: Float) {
    Box(modifier = Modifier
            .size(24.dp)
            .rotate(degrees)
    ) {
        Image(
            painter = painterResource(R.drawable.flecha),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}
