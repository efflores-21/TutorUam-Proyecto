package com.example.tutoruam_proyecto.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tutoruam_proyecto.ui.model.UserRole
import com.example.tutoruam_proyecto.ui.screen.HomeScreen
import com.example.tutoruam_proyecto.ui.screen.LoginScreen
import com.example.tutoruam_proyecto.ui.screen.RegisterScreen
import com.example.tutoruam_proyecto.ui.service.TokenManager

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
                },
                onNavigateToRegister = {
                    navController.navigate(RegisterDestination)
                }
            )
        }

        composable<RegisterDestination> {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(HomeDestination) {
                        popUpTo(LoginDestination) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable<HomeDestination> {
            var currentRole by remember {
                mutableStateOf(if (TokenManager.getRole() == "TUTOR") UserRole.TUTOR else UserRole.ESTUDIANTE)
            }

            key(currentRole) {
                HomeScreen(
                    modifier = Modifier.fillMaxSize(),
                    role = currentRole,
                    onChangeRole = {
                        val newRoleStr = TokenManager.getRole()
                        currentRole = if (newRoleStr == "TUTOR") UserRole.TUTOR else UserRole.ESTUDIANTE
                    },
                    onLogout = {
                        TokenManager.clear()
                        navController.navigate(LoginDestination) {
                            popUpTo(HomeDestination) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
