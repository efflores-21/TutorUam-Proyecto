package com.example.tutoruam_proyecto.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tutoruam_proyecto.ui.model.UserRole
import com.example.tutoruam_proyecto.ui.service.ApiResult
import com.example.tutoruam_proyecto.ui.service.ServiceLocator
import com.example.tutoruam_proyecto.ui.service.TokenManager
import com.example.tutoruam_proyecto.ui.viewmodel.PerfilViewModel
import com.example.tutoruam_proyecto.ui.viewmodel.PerfilViewModelFactory
import com.example.tutoruam_proyecto.ui.viewmodel.RatingViewModel
import com.example.tutoruam_proyecto.ui.viewmodel.RatingViewModelFactory

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun PerfilScreen(
    role: UserRole,
    userId: Long? = null, // ID del usuario a visualizar (null si es el propio)
    onChangeRole: () -> Unit = {},
    onLogout: () -> Unit = {},
    onNavigateToChat: (Long, String) -> Unit = { _, _ -> },
    onNavigateToMyPosts: () -> Unit = {},
    onNavigateToMyTutorias: () -> Unit = {},
    onNavigateToMyClasses: () -> Unit = {}
) {
    val viewModel: PerfilViewModel = viewModel(
        factory = PerfilViewModelFactory(ServiceLocator.authRepository)
    )
    val uiState by viewModel.uiState.collectAsState()

    val ratingViewModel: RatingViewModel = viewModel(
        factory = RatingViewModelFactory(ServiceLocator.ratingRepository)
    )
    val averageState by ratingViewModel.averageState.collectAsState()
    
    // Si userId es null o igual al actual, es mi perfil
    val isMyProfile = userId == null || userId == TokenManager.getUserId()
    
    // Aquí idealmente cargarías los datos del usuario si userId != null
    // Por ahora asumimos que los datos del TokenManager son para "mi perfil"
    val userName = if (isMyProfile) TokenManager.getName() else "Usuario $userId"
    val userEmail = if (isMyProfile) TokenManager.getEmail() else ""
    val userRole = if (isMyProfile) role else UserRole.TUTOR // Mock para otros perfiles
    
    var showConfirmDialog by remember { mutableStateOf(false) }

    // Cargar rating si es un tutor (y no es el propio perfil, o si es el propio perfil y es tutor)
    if (!isMyProfile || role == UserRole.TUTOR) {
        val targetId = userId ?: TokenManager.getUserId()
        LaunchedEffect(targetId) {
            targetId?.let { ratingViewModel.loadAverage(it) }
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is ApiResult.Success) {
            onChangeRole()
            viewModel.clearState()
        }
    }

    if (showConfirmDialog) {
        val nextRole = if (role == UserRole.ESTUDIANTE) "TUTOR" else "ESTUDIANTE"
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Cambiar Rol") },
            text = { Text("¿Estás seguro de que quieres cambiar tu rol a ${nextRole.lowercase()}?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.changeRole(nextRole)
                    showConfirmDialog = false
                }) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (uiState is ApiResult.Loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Tu Perfil",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Información personal",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            userName.take(2).uppercase(),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 32.sp
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            userName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            userEmail,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (averageState is ApiResult.Success) {
                            val avg = (averageState as ApiResult.Success<Double>).data
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "%.1f".format(avg),
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = if (isMyProfile) (if (role == UserRole.TUTOR) "Tutor" else "Estudiante") else "Tutor",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    if (!isMyProfile) {
                        Button(
                            onClick = {
                                userId?.let { id ->
                                    viewModel.startChatWithUser(id) { chat ->
                                        onNavigateToChat(chat.id, chat.tutorName ?: chat.studentName ?: "Chat")
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Rounded.SwapHoriz, contentDescription = null) // Cambiar por icono de chat
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Iniciar chat")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isMyProfile) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Configuración",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    ProfileListItem(
                        icon = Icons.Rounded.School,
                        title = "Mis publicaciones",
                        iconBgColor = MaterialTheme.colorScheme.primaryContainer,
                        iconColor = MaterialTheme.colorScheme.primary,
                        onClick = onNavigateToMyPosts
                    )

                    ProfileListItem(
                        icon = Icons.Rounded.School,
                        title = if (role == UserRole.TUTOR) "Mis tutorías" else "Mis clases",
                        iconBgColor = MaterialTheme.colorScheme.tertiaryContainer,
                        iconColor = MaterialTheme.colorScheme.tertiary,
                        onClick = {
                            if (role == UserRole.TUTOR) {
                                onNavigateToMyTutorias()
                            } else {
                                onNavigateToMyClasses()
                            }
                        }
                    )

                    ProfileListItem(
                        icon = Icons.Rounded.SwapHoriz,
                        title = "Cambiar rol",
                        iconBgColor = MaterialTheme.colorScheme.secondaryContainer,
                        iconColor = MaterialTheme.colorScheme.primary,
                        onClick = { showConfirmDialog = true }
                    )

                    ProfileListItem(
                        icon = Icons.Rounded.Logout,
                        title = "Cerrar sesión",
                        iconBgColor = MaterialTheme.colorScheme.errorContainer,
                        iconColor = MaterialTheme.colorScheme.error,
                        onClick = onLogout
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileListItem(
    icon: ImageVector,
    title: String,
    iconBgColor: Color,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(1.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(iconBgColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
            Icon(
                Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}
