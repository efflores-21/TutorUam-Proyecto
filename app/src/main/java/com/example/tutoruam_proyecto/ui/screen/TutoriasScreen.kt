package com.example.tutoruam_proyecto.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tutoruam_proyecto.ui.model.TutorClass
import com.example.tutoruam_proyecto.ui.model.UserRole
import com.example.tutoruam_proyecto.ui.navigation.ClassDetailDestination
import com.example.tutoruam_proyecto.ui.navigation.MyPostsDestination
import com.example.tutoruam_proyecto.ui.service.ApiResult
import com.example.tutoruam_proyecto.ui.service.ServiceLocator
import com.example.tutoruam_proyecto.ui.service.TokenManager
import com.example.tutoruam_proyecto.ui.viewmodel.TutoriasViewModel
import com.example.tutoruam_proyecto.ui.viewmodel.TutoriasViewModelFactory
import com.example.tutoruam_proyecto.ui.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutoriasScreen(
    role: UserRole,
    navController: NavController,
    onNavigateToChat: (Long, String) -> Unit = { _, _ -> },
    onNavigateToMyPosts: () -> Unit = {},
    onNavigateToClassDetail: (Long) -> Unit = {}
) {
    val viewModel: TutoriasViewModel = viewModel(
        factory = TutoriasViewModelFactory(
            ServiceLocator.tutoriasRepository,
            ServiceLocator.chatRepository,
            ServiceLocator.authRepository,
            role
        )
    )
    val itemsState by viewModel.itemsState.collectAsState()
    val actionState by viewModel.actionState.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var showForm by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<TutorClass?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    var showConfirmAssignDialog by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(actionState) {
        when (val state = actionState) {
            is ApiResult.Success -> {
                if (state.data is Pair<*, *>) {
                    val data = state.data as Pair<Long, String>
                    onNavigateToChat(data.first, data.second)
                } else if (selectedItem?.type == "REQUEST" && role == UserRole.TUTOR) {
                    // Después de asignar, obtener el chat entre el tutor y el estudiante
                    val creatorId = selectedItem?.creatorId
                    val currentUserId = TokenManager.getUserId()
                    if (creatorId != null && currentUserId != null) {
                        viewModel.findOrCreateChatWithUser(creatorId) { chat ->
                            onNavigateToChat(chat.id, chat.tutorName ?: chat.studentName ?: "Chat")
                        }
                    } else {
                        snackbarHostState.showSnackbar("Ayuda ofrecida correctamente")
                    }
                } else {
                    snackbarHostState.showSnackbar("Operación exitosa")
                }
                viewModel.clearActionState()
            }
            is ApiResult.Error -> {
                snackbarHostState.showSnackbar(state.message ?: "Error al procesar")
                viewModel.clearActionState()
            }
            else -> Unit
        }
    }

    if (showConfirmAssignDialog != null) {
        AlertDialog(
            onDismissRequest = { showConfirmAssignDialog = null },
            title = { Text("¡Ayuda ofrecida!") },
            text = { Text("Te has comprometido a ayudar a ${showConfirmAssignDialog}. Puedes contactar con el estudiante desde la sección de chats.") },
            confirmButton = {
                Button(onClick = { showConfirmAssignDialog = null }) {
                    Text("Entendido")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Rounded.School,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Text(
                                "TutorUAM",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showForm = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                // 🔥 IMPORTANTE: Este padding evita que el FAB se superponga con la barra inferior
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (role == UserRole.TUTOR) "Ofrecer ayuda" else "Pedir ayuda",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { innerPadding ->
        // 🔥 El padding innerPadding ya incluye el espacio de la barra inferior.
        // No debemos añadir más padding inferior a la lista, o se duplicará.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FB))
                .padding(innerPadding)   // Padding del Scaffold
                .padding(horizontal = 20.dp) // Padding lateral
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (role == UserRole.TUTOR) "Hola, Tutor" else "Hola, Estudiante",
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 24.sp),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (role == UserRole.TUTOR) "¿A quién quieres ayudar hoy?" else "¿Qué quieres aprender hoy?",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { 
                    searchQuery = it
                    viewModel.loadData(it.takeIf { it.isNotBlank() })
                },
                placeholder = { Text("Buscar por materia...", style = MaterialTheme.typography.bodyMedium) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.outline) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = itemsState) {
                is ApiResult.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ApiResult.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message ?: "Error al cargar datos")
                    }
                }
                is ApiResult.Success -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.data as List<TutorClass>) { item ->
                            TutorClassCard(
                                item = item,
                                role = role,
                                onActionClick = {
                                    if (item.type == "CLASS") {
                                        onNavigateToClassDetail(item.id)
                                    } else {
                                        // Es una solicitud: ofrecer ayuda
                                        selectedItem = item
                                        // No navegues a ClassDetailScreen, ejecuta la acción directamente
                                        viewModel.assignRequest(item.id)
                                        selectedItem = null
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showForm) {
        PostFormDialog(
            role = role,
            onDismiss = { showForm = false },
            onPost = { subject, time, desc, max ->
                viewModel.createPost(subject, time, desc, max)
                showForm = false
            }
        )
    }

    // Eliminado el bloque de navegación automática por selectedItem

}

@Composable
fun TutorClassCard(
    item: TutorClass,
    role: UserRole,
    onActionClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.creatorName.firstOrNull()?.toString() ?: "?",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Column {
                            Text(
                                text = item.creatorName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Book,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = item.subject,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            
                            val displayTime = if (item.type == "CLASS") item.scheduledTime else item.timeLimit
                            if (displayTime != null) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule, 
                                        contentDescription = null, 
                                        modifier = Modifier.size(12.dp), 
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = DateUtils.formatDate(displayTime),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "\"" + item.description + "\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onActionClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(99.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = if (item.type == "CLASS") {
                            if (role == UserRole.TUTOR) "Ver clase" else "Unirse a clase"
                        } else {
                            if (role == UserRole.TUTOR) "Ofrecer ayuda" else "Ver solicitud"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Cupos en la esquina superior derecha
            if (item.type == "CLASS" && item.maxStudents != null && item.maxStudents > 1) {
                Text(
                    text = "Cupos: ${item.currentStudents ?: 0}/${item.maxStudents}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostFormDialog(
    role: UserRole,
    initialSubject: String = "",
    initialTime: String = "",
    initialDescription: String = "",
    initialMaxStudents: String = "1",
    onDismiss: () -> Unit,
    onPost: (String, String, String, Int) -> Unit
) {
    var subject by remember { mutableStateOf(initialSubject) }
    var time by remember { mutableStateOf(initialTime) }
    var description by remember { mutableStateOf(initialDescription) }
    var maxStudents by remember { mutableStateOf(initialMaxStudents) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (role == UserRole.TUTOR) "Ofrecer Tutoría" else "Solicitar Ayuda",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it; errorMessage = null },
                    label = { Text("Materia") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage != null && errorMessage!!.contains("materia")
                )

                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it; errorMessage = null },
                    label = { Text("Hora (ej. 15:30)") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = errorMessage != null && errorMessage!!.contains("hora"),
                    placeholder = { Text("15:30") }
                )

                if (role == UserRole.TUTOR) {
                    OutlinedTextField(
                        value = maxStudents,
                        onValueChange = { maxStudents = it; errorMessage = null },
                        label = { Text("Cantidad de estudiantes") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = errorMessage != null && errorMessage!!.contains("estudiantes")
                    )
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it; errorMessage = null },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    isError = errorMessage != null && errorMessage!!.contains("descripción")
                )

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Button(
                    onClick = {
                        errorMessage = null

                        if (subject.isBlank()) {
                            errorMessage = "La materia es obligatoria"
                            return@Button
                        }
                        if (time.isBlank()) {
                            errorMessage = "La hora es obligatoria"
                            return@Button
                        }
                        if (description.isBlank()) {
                            errorMessage = "La descripción es obligatoria"
                            return@Button
                        }

                        val formattedTime = try {
                            val sdfInput = SimpleDateFormat("HH:mm", Locale.getDefault())
                            val date = sdfInput.parse(time)
                            val calendar = Calendar.getInstance()
                            val now = Calendar.getInstance()
                            calendar.time = date ?: throw Exception()
                            
                            now.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
                            now.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
                            now.set(Calendar.SECOND, 0)
                            now.set(Calendar.MILLISECOND, 0)

                            val sdfOutput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                            sdfOutput.format(now.time)
                        } catch (e: Exception) {
                            errorMessage = "Formato de hora inválido. Usa HH:mm (ej. 15:30)"
                            return@Button
                        }

                        val max = if (role == UserRole.TUTOR) {
                            maxStudents.toIntOrNull()
                        } else {
                            1
                        }

                        if (role == UserRole.TUTOR && (max == null || max <= 0)) {
                            errorMessage = "La cantidad de estudiantes debe ser un número positivo"
                            return@Button
                        }

                        onPost(
                            subject,
                            formattedTime,
                            description,
                            max ?: 1
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Publicar")
                }
            }
        }
    }
}
