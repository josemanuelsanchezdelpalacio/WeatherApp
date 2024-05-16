package com.example.numbermind.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.numbermind.models.ViewModelJuego
import com.example.numbermind.states.UiState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(navController: NavController, mvvm: ViewModelJuego) {

    val uiState by mvvm.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "NUMBERMIND") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    Text(text = "Intento ${uiState.intentosAnteriores.size + 1}/10")
                }
            )
        }
    ) { paddingValues ->
        GameBodyScreen(modifier = Modifier.padding(paddingValues), mvvm, uiState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameBodyScreen(modifier: Modifier, mvvm: ViewModelJuego, uiState: UiState) {
    var mostrarAlertDialog by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current


    Spacer(modifier = Modifier.height(16.dp))
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .background(Color.White)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        //edittext para que el usuario introduzca el numero
        OutlinedTextField(
            value = uiState.numeroJugador,
            onValueChange = { mvvm.changedNumero(it.take(4)) },
            label = { Text("Introduce un número de 4 cifras") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    mvvm.comprobarNumero(uiState.numeroJugador, context)
                },
                colors = ButtonDefaults.buttonColors(Color.Blue, contentColor = Color.White)
            ) {
                Text("Comprobar número")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    mvvm.reiniciarJuego()
                },
                colors = ButtonDefaults.buttonColors(Color.Red, contentColor = Color.White)
            ) {
                Text("Reiniciar")
            }
        }

        Text("Número secreto: ${uiState.numeroSecreto}")
        Spacer(modifier = Modifier.height(16.dp))

        //creo un lazycolumn con un card donde ira apareciendo la informacion de los intentos
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(uiState.intentosAnteriores.reversed()) { intento ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 10.dp
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Intento ${intento.numeroIntento}: ${intento.numeroJugador}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )

                        val matrizCirculos = mvvm.generarMatrizCirculos(intento)

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End
                        ) {
                            //recorro dos veces para crear las dos filas
                            for (i in 0 until 2) {
                                Row {
                                    //recorro dos veces para crear dos circulos
                                    for (j in 0 until 2) {
                                        val index = 2 * i + j

                                        //pongo su color dependiendo de su valor
                                        val color = when (matrizCirculos[index]) {
                                            0 -> Color.Black // bien colocados
                                            1 -> Color.Gray // mal colocados
                                            else -> Color.White // no existe
                                        }

                                        //dibujo un circulo con el color asignado
                                        Canvas(modifier = Modifier
                                            .size(32.dp)
                                            .padding(4.dp)) {
                                            drawCircle(color)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Mover la lógica de mostrar el AlertDialog aquí
    if (uiState.intentosAnteriores.isNotEmpty() && uiState.intentosAnteriores.last().bienColocados == 4) {
        mostrarAlertDialog = true
    } else if (uiState.intentosAnteriores.size >= 10) {
        mostrarAlertDialog = true
    }

    if (mostrarAlertDialog) {
        AlertDialog(
            text = { Text(text = uiState.resultado) },
            onDismissRequest = { mostrarAlertDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    mvvm.reiniciarJuego()
                    mostrarAlertDialog = false
                }) { Text(text = "Reiniciar") }
            })
    }
}
