package com.example.tutoruam_proyecto.ui.navigation



import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tutoruam_proyecto.ui.model.UserRole
import com.example.tutoruam_proyecto.ui.screen.HomeScreen
import com.example.tutoruam_proyecto.ui.screen.LoginScreen
import com.example.tutoruam_proyecto.ui.screen.RegisterScreen
import com.example.tutoruam_proyecto.ui.screen.RoleSelectionScreen

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    var selectedRole by rememberSaveable { mutableStateOf(UserRole.ESTUDIANTE) }

    NavHost(
        navController = navController,
        startDestination = LoginDestination,
        modifier = modifier.fillMaxSize()
    ) {
        composable<LoginDestination> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(RoleSelectionDestination) {
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
                    navController.navigate(RoleSelectionDestination) {
                        popUpTo(LoginDestination) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable<RoleSelectionDestination> {
            RoleSelectionScreen(
                onRoleSelected = { role ->
                    selectedRole = role
                    navController.navigate(HomeDestination) {
                        popUpTo(RoleSelectionDestination) { inclusive = true }
                    }
                }
            )
        }

        composable<HomeDestination> {
            HomeScreen(
                modifier = Modifier.fillMaxSize(),
                role = selectedRole,
                onChangeRole = {
                    navController.navigate(RoleSelectionDestination)
                },
                onLogout = {
                    navController.navigate(LoginDestination) {
                        popUpTo(HomeDestination) { inclusive = true }
                    }
                }
            )
        }
    }
}
