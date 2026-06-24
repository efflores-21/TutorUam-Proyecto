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

@Composable
fun ChatScreen(
    onChatClick: (Chat) -> Unit,
    viewModel: ChatViewModel = viewModel(factory = ChatViewModelFactory(ServiceLocator.chatRepository))
) {
    val chatsState by viewModel.chatsState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
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
                placeholder = { Text("Buscar conversaciones...") },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = MaterialTheme.colorScheme.primaryContainer,
                    focusedContainerColor = Color(0xFFECEFF3),
                    unfocusedContainerColor = Color(0xFFECEFF3)
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
                    items(chats.filter { it.tutorName.contains(searchQuery, ignoreCase = true) || it.studentName.contains(searchQuery, ignoreCase = true) }) { chat ->
                        ChatItemCard(chat = chat, onClick = { onChatClick(chat) })
                    }
                }
            }
            is ApiResult.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error al cargar chats: ${state.message}", color = MaterialTheme.colorScheme.error)
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
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val displayName = if (chat.tutorId == TokenManager.getUserId()) chat.studentName else chat.tutorName
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color(0xFFE2E7ED), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(displayName.first().toString(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 20.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(displayName, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(chat.lastMessageAt ?: "", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (chat.lastMessage != null) {
                        Text(chat.lastMessage, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, modifier = Modifier.weight(1f))
                    }
                    if (chat.unreadCount > 0) {
                        Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = CircleShape, modifier = Modifier.size(20.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(chat.unreadCount.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
