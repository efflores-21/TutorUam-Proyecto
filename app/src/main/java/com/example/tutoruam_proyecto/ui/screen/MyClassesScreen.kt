package com.example.tutoruam_proyecto.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.unit.sp
import com.example.tutoruam_proyecto.ui.utils.DateUtils
import com.example.tutoruam_proyecto.ui.model.RatingRequest
import com.example.tutoruam_proyecto.ui.viewmodel.RatingViewModel
import com.example.tutoruam_proyecto.ui.viewmodel.RatingViewModelFactory
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyClassesScreen(
    onBack: () -> Unit,
    onNavigateToDetail: (Long) -> Unit
) {
    val viewModel: TutoriasViewModel = viewModel(
        factory = TutoriasViewModelFactory(
            ServiceLocator.tutoriasRepository,
            ServiceLocator.chatRepository,
            ServiceLocator.authRepository,
            UserRole.ESTUDIANTE
        )
    )
    val state by viewModel.myClassesState.collectAsState()

    var showRatingDialog by remember { mutableStateOf(false) }
    var selectedClassId by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadMyClasses()
    }

    if (showRatingDialog && selectedClassId != null) {
        RatingDialog(
            classSessionId = selectedClassId!!,
            onDismiss = { showRatingDialog = false },
            onRatingSubmitted = {
                // Podrías recargar o mostrar un mensaje
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Clases", fontWeight = FontWeight.Bold) },
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
                        Text("No estás inscrito en ninguna clase", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(s.data) { item ->
                                MyClassCard(
                                    item = item,
                                    onClick = { onNavigateToDetail(item.id) },
                                    onRateClick = {
                                        selectedClassId = item.id
                                        showRatingDialog = true
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
fun MyClassCard(
    item: TutorClass,
    onClick: () -> Unit,
    onRateClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.subject,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Tutor: ${item.creatorName}", style = MaterialTheme.typography.bodyMedium)
                }
                
                // Botón de calificar
                Button(
                    onClick = onRateClick,
                    modifier = Modifier.padding(start = 8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Calificar", fontSize = 12.sp)
                }
            }
            
            val time = item.scheduledTime ?: item.timeLimit
            if (time != null) {
                Text(
                    text = "Fecha: ${DateUtils.formatDate(time)}",
                    style = MaterialTheme.typography.labelSmall
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Toca para ver detalles y chats", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}

@Composable
fun RatingDialog(
    classSessionId: Long,
    onDismiss: () -> Unit,
    onRatingSubmitted: () -> Unit,
    viewModel: RatingViewModel = viewModel(factory = RatingViewModelFactory(ServiceLocator.ratingRepository))
) {
    var score by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }
    val actionState by viewModel.actionState.collectAsState()

    LaunchedEffect(actionState) {
        if (actionState is ApiResult.Success) {
            onRatingSubmitted()
            onDismiss()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Calificar tutor") },
        text = {
            Column {
                Text("Puntuación: $score / 5")
                Slider(
                    value = score.toFloat(),
                    onValueChange = { score = it.roundToInt() },
                    valueRange = 1f..5f,
                    steps = 3
                )
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Comentario (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.createRating(RatingRequest(classSessionId, score, comment))
                },
                enabled = actionState !is ApiResult.Loading
            ) {
                if (actionState is ApiResult.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                } else {
                    Text("Enviar")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
