package com.example.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.models.ViewModel
import com.example.weatherapp.navigation.appNavigation
import com.example.weatherapp.ui.theme.WeatherAppTheme


class MainActivity : ComponentActivity() {
    private val MY_PERMISSIONS_REQUEST_LOCATION = 99
    private lateinit var mvvm: ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mvvm = ViewModelProvider(this).get(ViewModel::class.java)
        mvvm.initLocationClient(this)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        } else {
            mvvm.ubicacionInicial()
        }

        setContent {
            WeatherAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    appNavigation()
                }
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    mvvm.ubicacionInicial()
                } else {
                    // Permiso denegado
                    // Deshabilita la funcionalidad que depende de este permiso.
                }
                return
            }
            else -> {
                // Ignora todos los otros casos de solicitud
            }
        }
    }
}
