package com.example.tutoruam_proyecto.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tutoruam_proyecto.ui.model.UserRole
import com.example.tutoruam_proyecto.ui.service.ApiResult
import com.example.tutoruam_proyecto.ui.service.ServiceLocator
import com.example.tutoruam_proyecto.ui.service.TokenManager
import com.example.tutoruam_proyecto.ui.viewmodel.TutoriasViewModel
import com.example.tutoruam_proyecto.ui.viewmodel.TutoriasViewModelFactory

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.tutoruam_proyecto.ui.model.TutorClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPostsScreen(
    role: UserRole,
    onBack: () -> Unit,
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
    
    var editingItem by remember { mutableStateOf<TutorClass?>(null) }
    var itemToDelete by remember { mutableStateOf<TutorClass?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadMyPosts()
    }

    LaunchedEffect(actionState) {
        if (actionState is ApiResult.Success) {
            viewModel.clearActionState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Publicaciones", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FB))
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            when (val state = itemsState) {
                is ApiResult.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ApiResult.Success -> {
                    val myItems = state.data
                    
                    if (myItems.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No tienes publicaciones aún")
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 16.dp)
                        ) {
                            items(myItems) { item ->
                                MyPostCard(
                                    item = item,
                                    role = role,
                                    onEdit = { editingItem = item },
                                    onDelete = { itemToDelete = item },
                                    onClick = {
                                        if (item.type == "CLASS") {
                                            onNavigateToClassDetail(item.id)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                is ApiResult.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message ?: "Error al cargar datos")
                    }
                }
            }
        }
    }

    editingItem?.let { item ->
        val initialTime = item.scheduledTime ?: item.timeLimit
        val formattedTime = if (initialTime != null) {
            com.example.tutoruam_proyecto.ui.utils.DateUtils.formatTime(initialTime)
        } else {
            ""
        }

        PostFormDialog(
            role = role, // Usamos el rol actual para editar
            initialSubject = item.subject,
            initialDescription = item.description,
            initialTime = formattedTime,
            initialMaxStudents = item.maxStudents?.toString() ?: "1",
            onDismiss = { editingItem = null },
            onPost = { subject, time, desc, max ->
                viewModel.updatePost(item, subject, time, desc, max)
                editingItem = null
            }
        )
    }

    itemToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Eliminar publicación") },
            text = { Text("¿Estás seguro de que deseas eliminar esta publicación?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deletePost(item)
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun MyPostCard(
    item: TutorClass,
    role: UserRole,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit = {}
) {
    val isOwner = item.creatorId == TokenManager.getUserId()
    
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.subject,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (isOwner) {
                    Row {
                        IconButton(onClick = onEdit) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = item.description, style = MaterialTheme.typography.bodyMedium)
            
            val time = item.scheduledTime ?: item.timeLimit
            if (time != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Hora: ${com.example.tutoruam_proyecto.ui.utils.DateUtils.formatDate(time)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
