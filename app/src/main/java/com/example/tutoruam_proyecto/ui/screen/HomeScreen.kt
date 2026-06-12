package com.example.tutoruam_proyecto.ui.screen

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.tutoruam_proyecto.ui.model.UserRole
import com.example.tutoruam_proyecto.ui.navigation.ChatDestination
import com.example.tutoruam_proyecto.ui.navigation.PerfilDestination
import com.example.tutoruam_proyecto.ui.navigation.TutoriasDestination

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    role: UserRole,
    onChangeRole: () -> Unit,
    onLogout: () -> Unit
) {
    val homeNavController = rememberNavController()
    val currentDestination = homeNavController.currentBackStackEntryAsState().value?.destination

    val items = listOf(
        BottomItem(
            label = "Tutoría",
            icon = Icons.Default.School,
            destination = TutoriasDestination,
            isSelected = { destination ->
                destination?.hierarchy?.any { it.hasRoute<TutoriasDestination>() } == true
            }
        ),
        BottomItem(
            label = "Chat",
            icon = Icons.Default.Chat,
            destination = ChatDestination,
            isSelected = { destination ->
                destination?.hierarchy?.any { it.hasRoute<ChatDestination>() } == true
            }
        ),
        BottomItem(
            label = "Perfil",
            icon = Icons.Default.Person,
            destination = PerfilDestination,
            isSelected = { destination ->
                destination?.hierarchy?.any { it.hasRoute<PerfilDestination>() } == true
            }
        )
    )

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                items.forEach { item ->
                    val selected = item.isSelected(currentDestination)

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            homeNavController.navigate(item.destination) {
                                popUpTo(homeNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { androidx.compose.material3.Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        HomeNavHost(
            modifier = Modifier.padding(innerPadding),
            navController = homeNavController,
            role = role,
            onChangeRole = onChangeRole,
            onLogout = onLogout
        )
    }
}

private data class BottomItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val destination: Any,
    val isSelected: (NavDestination?) -> Boolean
)

@Composable
private fun HomeNavHost(
    modifier: Modifier,
    navController: NavHostController,
    role: UserRole,
    onChangeRole: () -> Unit,
    onLogout: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = TutoriasDestination,
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable<TutoriasDestination> {
            TutoriasScreen(role = role)
        }
        composable<ChatDestination> {
            ChatScreen()
        }
        composable<PerfilDestination> {
            PerfilScreen(
                onChangeRole = onChangeRole,
                onLogout = onLogout
            )
        }
    }
}
