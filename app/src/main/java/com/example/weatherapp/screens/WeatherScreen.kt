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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.weatherapp.data.PronosticoSemanalResponse
import com.example.weatherapp.data.WeatherResponse
import com.example.weatherapp.models.ViewModel
import com.example.weatherapp.navigation.AppScreens
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(navController: NavController, mvvm: ViewModel) {
    val clima by mvvm.weather.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        mvvm.initLocationClient(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = clima?.name ?: "WEATHER") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    var busquedaAbierta by remember { mutableStateOf(false) }
                    if (busquedaAbierta) {
                        TextField(
                            value = mvvm.searchQuery.collectAsState().value,
                            onValueChange = { mvvm.searchQuery.value = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Buscar ciudad") },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = {
                                mvvm.obtenerClimaPorCiudad(mvvm.searchQuery.value)
                                busquedaAbierta = false
                            })
                        )
                    } else {
                        IconButton(onClick = { busquedaAbierta = true }) {
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        clima?.let {
            // Tarjeta con información actual del clima
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF6597CC))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = clima.weather[0].description,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Image(
                            painter = rememberImagePainter(data = "https://openweathermap.org/img/w/${clima.weather[0].icon}.png"),
                            contentDescription = "Icono del clima",
                            modifier = Modifier.size(100.dp)
                        )
                        Text("${clima.main.temp}ºC")
                        Text("Sensación térmica: ${clima.main.feels_like}ºC")
                    }
                    Column {
                        Text("Viento: ${clima.wind.speed} m/s")
                        Text("Dirección del viento: ${clima.wind.deg}º")
                    }
                }
            }
        }
    }
}
