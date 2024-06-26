package com.dam2jms.weatherapp.screens

import android.app.Activity
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.dam2jms.weatherapp.R
import com.dam2jms.weatherapp.data.ForecastResponse
import com.dam2jms.weatherapp.data.WeatherResponse
import com.dam2jms.weatherapp.models.ViewModel
import java.util.jar.Manifest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(navController: NavController, mvvm: ViewModel) {
    val clima by mvvm.clima.collectAsState()
    val pronostico by mvvm.pronostico.collectAsState()
    val nombreCiudad by mvvm.nombreCiudad.collectAsState()

    val context = LocalContext.current

    mvvm.obtenerUbicacionActual(context)

    Scaffold(
        topBar = {
            var busqueda by remember { mutableStateOf(false) }
            var ciudad by remember { mutableStateOf("") }

            TopAppBar(
                title = {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = nombreCiudad,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color.Transparent),
                actions = {
                    //cuando se pulsa el icono de la lupa sale un textfield para buscar la ciudad
                    if (busqueda) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextField(
                                value = ciudad,
                                onValueChange = { ciudad = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Introduce la ciudad a buscar") },
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(onSearch = {
                                    mvvm.obtenerClimaPorCiudad(ciudad, context)
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

    //para el fondo de la app
    val gradiente = Brush.verticalGradient(
        colors = listOf(Color(0xFF040569), Color(0xFF141E74), Color(0xFF1C2C99)),
        startY = 0.0f,
        endY = 1000.0f
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(brush = gradiente)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        cajaClima(clima)
        Spacer(modifier = Modifier.height(50.dp))
        cajaPronostico(pronostico)
    }
}

@Composable
fun cajaClima(clima: WeatherResponse?) {
    clima?.let {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.5f))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterStart)
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
                    text = clima.weather[0].description.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                    fontSize = 16.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


@Composable
fun cajaPronostico(pronostico: ForecastResponse?) {
    pronostico?.forecastday?.let { forecastDays ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.5f))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterStart)
            ) {
                if (forecastDays.isNotEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Pronóstico",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        LazyRow {
                            items(forecastDays) { forecastDay ->
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = forecastDay.date,
                                        fontSize = 16.sp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Image(
                                        painter = rememberImagePainter(data = "https://openweathermap.org/img/w/${forecastDay.condition.firstOrNull()?.icon}.png"),
                                        contentDescription = "Icono del clima",
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Max: ${forecastDay.day.maxtempC}ºC",
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Min: ${forecastDay.day.mintempC}ºC",
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun direccionVientoFlecha(degrees: Float) {
    Box(
        modifier = Modifier
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

