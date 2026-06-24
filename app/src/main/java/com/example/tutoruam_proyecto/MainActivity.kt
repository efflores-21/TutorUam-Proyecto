package com.example.tutoruam_proyecto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.tutoruam_proyecto.ui.navigation.AppNavigation
import com.example.tutoruam_proyecto.ui.service.TokenManager
import com.example.tutoruam_proyecto.ui.theme.TutorUamProyectoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TokenManager.init(this)
        enableEdgeToEdge()
        setContent {
            TutorUamProyectoTheme {
                AppNavigation(modifier = Modifier.fillMaxSize())
            }
        }
    }
}