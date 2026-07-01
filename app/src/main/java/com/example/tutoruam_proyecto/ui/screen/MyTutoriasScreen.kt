package com.example.tutoruam_proyecto.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tutoruam_proyecto.ui.model.TutorClass
import com.example.tutoruam_proyecto.ui.model.UserRole
import com.example.tutoruam_proyecto.ui.service.ApiResult
import com.example.tutoruam_proyecto.ui.service.ServiceLocator
import com.example.tutoruam_proyecto.ui.viewmodel.TutoriasViewModel
import com.example.tutoruam_proyecto.ui.viewmodel.TutoriasViewModelFactory
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.tutoruam_proyecto.ui.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTutoriasScreen(
    onBack: () -> Unit,
    onNavigateToChat: (Long, String) -> Unit
) {
    val viewModel: TutoriasViewModel = viewModel(
        factory = TutoriasViewModelFactory(
            ServiceLocator.tutoriasRepository,
            ServiceLocator.chatRepository,
            ServiceLocator.authRepository,
            UserRole.TUTOR
        )
    )
    val state by viewModel.myTutoriasState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadMyTutorias()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Tutorías", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FB))
                .padding(innerPadding)
        ) {
            when (val s = state) {
                is ApiResult.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is ApiResult.Error -> Text("Error: ${s.message}", modifier = Modifier.align(Alignment.Center))
                is ApiResult.Success -> {
                    if (s.data.isEmpty()) {
                        Text("No tienes tutorías asignadas", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(s.data) { item ->
                                MyTutoriaCard(
                                    item = item,
                                    onChatClick = {
                                        viewModel.getChatWithCreator(item.id, item.creatorId) { chat ->
                                            onNavigateToChat(chat.id, chat.studentName ?: "Estudiante")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyTutoriaCard(
    item: TutorClass,
    onChatClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = item.subject,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Estudiante: ${item.creatorName}", style = MaterialTheme.typography.bodyMedium)
            Text(text = item.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            
            val time = item.scheduledTime ?: item.timeLimit
            if (time != null) {
                Text(
                    text = "Fecha: ${DateUtils.formatDate(time)}",
                    style = MaterialTheme.typography.labelSmall
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onChatClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ir al chat")
            }
        }
    }
}
