package com.example.tutoruam_proyecto.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tutoruam_proyecto.ui.model.Chat
import com.example.tutoruam_proyecto.ui.service.ApiResult
import com.example.tutoruam_proyecto.ui.service.ServiceLocator
import com.example.tutoruam_proyecto.ui.service.TokenManager
import com.example.tutoruam_proyecto.ui.viewmodel.ChatViewModel
import com.example.tutoruam_proyecto.ui.viewmodel.ChatViewModelFactory
import com.example.tutoruam_proyecto.ui.utils.DateUtils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner

@Composable
fun ChatScreen(
    onChatClick: (Chat) -> Unit,
    viewModel: ChatViewModel = viewModel(factory = ChatViewModelFactory(ServiceLocator.chatRepository))
) {
    val chatsState by viewModel.chatsState.collectAsState()
    val refreshTrigger by viewModel.refreshTrigger.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadChats()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(refreshTrigger) {
        viewModel.loadChats()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.padding(top = 16.dp, start = 20.dp, end = 20.dp)) {
            Text(
                "Chats",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar conversaciones...", style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = MaterialTheme.colorScheme.outline) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        when (val state = chatsState) {
            is ApiResult.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            is ApiResult.Success -> {
                val chats = state.data
                LazyColumn(
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val filteredChats = chats.filter {
                        val name = if (it.isGroup) it.groupName else (if (it.tutorId == TokenManager.getUserId()) it.studentName else it.tutorName)
                        name?.contains(searchQuery, ignoreCase = true) == true
                    }
                    items(filteredChats) { chat ->
                        ChatItemCard(chat = chat, onClick = { onChatClick(chat) })
                    }
                }
            }
            is ApiResult.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    val errorMsg = if (state.code == 500) {
                        "Error en el servidor al cargar chats. Inténtalo más tarde."
                    } else {
                        "Error al cargar chats: ${state.message}"
                    }
                    Text(errorMsg, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(20.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
            null -> Unit
        }
    }
}

@Composable
fun ChatItemCard(chat: Chat, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val displayName = if (chat.isGroup) {
                chat.groupName ?: "Grupo"
            } else {
                if (chat.tutorId == TokenManager.getUserId()) chat.studentName ?: "Estudiante" else chat.tutorName ?: "Tutor"
            }
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(if (chat.isGroup) MaterialTheme.colorScheme.secondaryContainer else Color(0xFFE2E7ED), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    displayName.firstOrNull()?.toString() ?: "?",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 20.sp
                )
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1 // 👈 Para que no se desborde
                )
                
                // 👇 Ahora el mensaje y el contador están en una fila
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chat.lastMessage ?: "Nuevo chat",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        modifier = Modifier.weight(1f) // Ocupa el espacio sobrante
                    )
                    
                    // 👇 Contador de mensajes no leídos (en la esquina superior derecha)
                    if (chat.unreadCount > 0) {
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    chat.unreadCount.toString(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
            
            // 👇 Fecha y hora en la columna derecha
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (chat.lastMessageAt != null) DateUtils.formatDate(chat.lastMessageAt) else "",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
