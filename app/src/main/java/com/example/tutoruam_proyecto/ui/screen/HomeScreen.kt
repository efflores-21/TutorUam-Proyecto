package com.example.tutoruam_proyecto.ui.screen

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChatBubble
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
            icon = Icons.Rounded.School,
            destination = TutoriasDestination,
            isSelected = { dest -> dest?.hierarchy?.any { it.hasRoute<TutoriasDestination>() } == true }
        ),
        BottomItem(
            label = "Chat",
            icon = Icons.Rounded.ChatBubble,
            destination = ChatDestination,
            isSelected = { dest -> dest?.hierarchy?.any { it.hasRoute<ChatDestination>() } == true }
        ),
        BottomItem(
            label = "Perfil",
            icon = Icons.Rounded.Person,
            destination = PerfilDestination,
            isSelected = { dest -> dest?.hierarchy?.any { it.hasRoute<PerfilDestination>() } == true }
        )
    )

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.shadow(8.dp),
                tonalElevation = 0.dp
            ) {
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
                        icon = {
                            Icon(
                                item.icon,
                                contentDescription = item.label,
                                tint = if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        label = {
                            Text(
                                item.label,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                color = if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                        )
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
