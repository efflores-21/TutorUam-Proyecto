package com.example.tutoruam_proyecto.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tutoruam_proyecto.ui.model.Chat
import com.example.tutoruam_proyecto.ui.model.UserProfile
import com.example.tutoruam_proyecto.ui.model.UserRole
import com.example.tutoruam_proyecto.ui.model.TutorClass
import com.example.tutoruam_proyecto.ui.service.ApiResult
import com.example.tutoruam_proyecto.ui.service.ServiceLocator
import com.example.tutoruam_proyecto.ui.service.TokenManager
import com.example.tutoruam_proyecto.ui.viewmodel.TutoriasViewModel
import com.example.tutoruam_proyecto.ui.viewmodel.TutoriasViewModelFactory
import com.example.tutoruam_proyecto.ui.utils.DateUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassDetailScreen(
    classId: Long,
    role: UserRole,
    onBack: () -> Unit,
    onNavigateToChat: (Long, String) -> Unit,
    onNavigateToEdit: (Long) -> Unit = {} // para editar desde detalles
) {
    val viewModel: TutoriasViewModel = viewModel(
        factory = TutoriasViewModelFactory(
            ServiceLocator.tutoriasRepository,
            ServiceLocator.chatRepository,
            ServiceLocator.authRepository,
            role
        )
    )

    val classState by viewModel.classDetailState.collectAsState()
    val studentsState by viewModel.classStudentsState.collectAsState()
    val actionState by viewModel.actionState.collectAsState()
    val currentUserId = TokenManager.getUserId()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var confirmAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    LaunchedEffect(classId) {
        viewModel.loadClassDetail(classId)
        viewModel.loadClassStudents(classId)
    }

    LaunchedEffect(actionState) {
        when (val state = actionState) {
            is ApiResult.Success -> {
                snackbarHostState.showSnackbar("Operación exitosa")
                viewModel.clearActionState()
                // recargar detalles y estudiantes
                viewModel.loadClassDetail(classId)
                viewModel.loadClassStudents(classId)
            }
            is ApiResult.Error -> {
                snackbarHostState.showSnackbar(state.message ?: "Error al procesar")
                viewModel.clearActionState()
            }
            else -> Unit
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Detalle de clase", fontWeight = FontWeight.Bold) },
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
                .padding(innerPadding)
                .background(Color(0xFFF8F9FB))
        ) {
            when (val state = classState) {
                is ApiResult.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ApiResult.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error al cargar la clase: ${state.message}")
                    }
                }
                is ApiResult.Success -> {
                    val classItem = state.data
                    val isTutorOfClass = role == UserRole.TUTOR && classItem.creatorId == currentUserId

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Información principal
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = classItem.subject,
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Creado por: ${classItem.creatorName}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Descripción: ${classItem.description}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    if (classItem.scheduledTime != null) {
                                        Text(
                                            text = "Fecha: ${DateUtils.formatDate(classItem.scheduledTime)}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Estado: ${classItem.status ?: "OPEN"}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (classItem.status == "OPEN") Color(0xFF2E7D32) else Color(0xFFD32F2F)
                                        )
                                        Text(
                                            text = "Cupos: ${classItem.currentStudents}/${classItem.maxStudents}",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }

                        // Botones de acción para el tutor
                        if (isTutorOfClass) {
                            item {
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = { onNavigateToEdit(classId) },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Editar")
                                            }
                                            Button(
                                                onClick = {
                                                    confirmAction = { viewModel.toggleClassStatus(classId, classItem.status ?: "OPEN") }
                                                    showConfirmDialog = true
                                                },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(12.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (classItem.status == "OPEN") Color(0xFFD32F2F) else Color(0xFF2E7D32)
                                                )
                                            ) {
                                                Icon(if (classItem.status == "OPEN") Icons.Default.Lock else Icons.Default.LockOpen, contentDescription = null)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(if (classItem.status == "OPEN") "Cerrar" else "Abrir")
                                            }
                                        }
                                        Button(
                                            onClick = {
                                                viewModel.createGroupChatForClass(classId) { chat ->
                                                    onNavigateToChat(chat.id, chat.groupName ?: "Chat grupal")
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                        ) {
                                            Icon(Icons.Default.Group, contentDescription = null)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Crear / Ir al chat grupal")
                                        }
                                    }
                                }
                            }
                        }

                        // Lista de estudiantes
                        item {
                            Text(
                                text = "Estudiantes inscritos (${classItem.currentStudents ?: 0}/${classItem.maxStudents ?: 0})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        when (val stState = studentsState) {
                            is ApiResult.Loading -> {
                                item { Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
                            }
                            is ApiResult.Error -> {
                                item { Text("Error al cargar estudiantes") }
                            }
                            is ApiResult.Success -> {
                                if (stState.data.isEmpty()) {
                                    item { Text("Aún no hay estudiantes inscritos", style = MaterialTheme.typography.bodyMedium, color = Color.Gray) }
                                } else {
                                    items(stState.data) { student ->
                                        StudentItem(
                                            student = student,
                                            showRemove = isTutorOfClass,
                                            onRemove = {
                                                confirmAction = { viewModel.expelStudent(classId, student.id) }
                                                showConfirmDialog = true
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Botón para el estudiante: unirse a la clase (si no está inscrito)
                        if (role == UserRole.ESTUDIANTE && classItem.creatorId != currentUserId) {
                            item {
                                val isEnrolled = studentsState is ApiResult.Success &&
                                        (studentsState as ApiResult.Success<List<UserProfile>>).data.any { it.id == currentUserId }
                                if (!isEnrolled && classItem.status != "CLOSED") {
                                    Button(
                                        onClick = {
                                            viewModel.joinClass(classId, classItem.creatorName, classItem.creatorId)
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Inscribirse a la clase")
                                    }
                                } else if (isEnrolled) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                viewModel.getGroupChatForClass(classId) { chat ->
                                                    onNavigateToChat(chat.id, chat.groupName ?: "Chat Grupal")
                                                }
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                        ) {
                                            Icon(Icons.Default.Groups, contentDescription = null)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Chat Grupal")
                                        }
                                        Button(
                                            onClick = {
                                                viewModel.getChatWithCreator(classId, classItem.creatorId) { chat ->
                                                    onNavigateToChat(chat.id, chat.tutorName ?: chat.studentName ?: "Chat")
                                                }
                                            },
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Tutor")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirmar acción") },
            text = { Text("¿Estás seguro de realizar esta acción?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmAction?.invoke()
                        showConfirmDialog = false
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun StudentItem(
    student: UserProfile,
    showRemove: Boolean,
    onRemove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        student.name.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = student.name, style = MaterialTheme.typography.bodyMedium)
            }
            if (showRemove) {
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                }
            }
        }
    }
}
