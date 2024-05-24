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
    val cityName by mvvm.nombreCiudad.collectAsState()

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
        ScreenBodyClima(
            modifier = Modifier.padding(paddingValues),
            clima = clima,
            pronostico = pronostico,
            mvvm = mvvm
        )
    }
}

@Composable
fun ScreenBodyClima(
    modifier: Modifier,
    clima: WeatherResponse?,
    pronostico: ForecastResponse?,
    mvvm: ViewModel
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFA7F5FF))
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
                    modifier = Modifier.size(100.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "${clima.main.temp.redondear()}ºC",
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    VientoCard(clima.wind.speed, clima.wind.deg)
                    HumedadCard(clima.main.humidity)
                }
                Spacer(modifier = Modifier.height(24.dp))

                pronostico?.forecastday?.let { forecastDays ->
                    if (forecastDays.isNotEmpty()) {
                        Text(
                            text = "Pronóstico",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        LazyRow {
                            items(forecastDays.take(3)) { forecastDay ->
                                DiaCard(forecastDay)
                            }
                        }
                    } else {
                        Text(
                            text = "No hay información de pronóstico disponible",
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VientoCard(velocidad: Float, grados: Float) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .width(100.dp)
            .height(200.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Velocidad del viento",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            direccionVientoFlecha(degrees = grados)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${velocidad.redondear()} km/h",
                fontSize = 16.sp,
                color = Color.Black
            )
        }
    }
}

@Composable
fun HumedadCard(humedad: Int) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .width(100.dp)
            .height(200.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = "Humedad",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Image(
            painter = rememberImagePainter(data = "https://openweathermap.org/img/wn/09d.png"),
            contentDescription = "Icono de humedad",
            modifier = Modifier.size(50.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$humedad%",
            fontSize = 16.sp,
            color = Color.Black
        )
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
                painter = rememberImagePainter(data = pronostico.day.condition.icon),
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

fun Float.redondear(): Float {
    return BigDecimal(this.toDouble()).setScale(1, RoundingMode.HALF_EVEN).toFloat()
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
