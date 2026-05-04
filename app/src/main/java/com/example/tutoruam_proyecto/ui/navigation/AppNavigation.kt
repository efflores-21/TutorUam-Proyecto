package com.example.tutoruam_proyecto.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tutoruam_proyecto.ui.screen.HomeScreen
import com.example.tutoruam_proyecto.ui.screen.LoginScreen

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = LoginDestination,
        modifier = modifier.fillMaxSize()
    ) {
        composable<LoginDestination> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(HomeDestination) {
                        popUpTo(LoginDestination) { inclusive = true }
                    }
                }
            )
        }

        composable<HomeDestination> {
            HomeScreen(
                modifier = Modifier.fillMaxSize(),
                onLogout = {
                    navController.navigate(LoginDestination) {
                        popUpTo(HomeDestination) { inclusive = true }
                    }
                }
            )
        }
    }
}
