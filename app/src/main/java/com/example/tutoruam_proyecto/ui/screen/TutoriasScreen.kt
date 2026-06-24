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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tutoruam_proyecto.ui.model.TutorClass
import com.example.tutoruam_proyecto.ui.model.UserRole
import com.example.tutoruam_proyecto.ui.service.ApiResult
import com.example.tutoruam_proyecto.ui.service.ServiceLocator
import com.example.tutoruam_proyecto.ui.viewmodel.TutoriasViewModel
import com.example.tutoruam_proyecto.ui.viewmodel.TutoriasViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutoriasScreen(
    role: UserRole,
    onNavigateToChat: (Long, String) -> Unit = { _, _ -> }
) {
    val viewModel: TutoriasViewModel = viewModel(
        factory = TutoriasViewModelFactory(ServiceLocator.tutoriasRepository, role)
    )
    val itemsState by viewModel.itemsState.collectAsState()
    val actionState by viewModel.actionState.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var showForm by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(actionState) {
        when (val state = actionState) {
            is ApiResult.Success -> {
                snackbarHostState.showSnackbar("Operación exitosa")
                viewModel.clearActionState()
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
                title = {
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
                            "Universidad Americana",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FB))
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
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
                        contentPadding = PaddingValues(bottom = 80.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.data) { item ->
                            TutorClassCard(
                                item = item,
                                role = role,
                                onActionClick = {
                                    if (role == UserRole.ESTUDIANTE) {
                                        viewModel.joinClass(item.id)
                                    } else {
                                        // Tutor clicks on a request to offer help -> open chat or similar
                                        // For now, let's assume it joins/accepts
                                        viewModel.joinClass(item.id)
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
                                text = item.time,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                if (item.maxStudents > 1) {
                    Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                        Text("Cupos: ${item.currentStudents}/${item.maxStudents}")
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
                    text = if (role == UserRole.TUTOR) "Ofrecer ayuda" else "Unirse a clase",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostFormDialog(
    role: UserRole,
    onDismiss: () -> Unit,
    onPost: (String, String, String, Int) -> Unit
) {
    var subject by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var maxStudents by remember { mutableStateOf("1") }

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
                    onValueChange = { subject = it },
                    label = { Text("Materia") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Hora (ej. 15:30)") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (role == UserRole.TUTOR) {
                    OutlinedTextField(
                        value = maxStudents,
                        onValueChange = { maxStudents = it },
                        label = { Text("Cantidad de estudiantes") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Button(
                    onClick = {
                        onPost(
                            subject,
                            time,
                            description,
                            maxStudents.toIntOrNull() ?: 1
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
