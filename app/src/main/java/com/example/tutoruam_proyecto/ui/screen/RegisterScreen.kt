package com.example.tutoruam_proyecto.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.tutoruam_proyecto.ui.components.UamTextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tutoruam_proyecto.ui.service.ApiResult
import com.example.tutoruam_proyecto.ui.service.ServiceLocator
import com.example.tutoruam_proyecto.ui.viewmodel.LoginViewModel
import com.example.tutoruam_proyecto.ui.viewmodel.LoginViewModelFactory
import com.example.tutoruam_proyecto.ui.model.RegisterRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val viewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(ServiceLocator.authRepository)
    )
    val loginState by viewModel.loginState.collectAsState()
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    var name by remember { mutableStateOf("") }
    var cif by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("ESTUDIANTE") }
    var errorMessage by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    val loading = loginState is ApiResult.Loading

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is ApiResult.Success -> {
                viewModel.clearLoginState()
                onRegisterSuccess()
            }
            is ApiResult.Error -> {
                errorMessage = state.message ?: "Error inesperado"
                viewModel.clearLoginState()
            }
            else -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FB)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                shape = RoundedCornerShape(40.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 440.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AuthHeader(
                        subtitle = "Crea tu cuenta universitaria"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Registrarse como:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedRole == "ESTUDIANTE",
                            onClick = { selectedRole = "ESTUDIANTE" }
                        )
                        Text(
                            text = "Estudiante",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        RadioButton(
                            selected = selectedRole == "TUTOR",
                            onClick = { selectedRole = "TUTOR" }
                        )
                        Text(
                            text = "Tutor",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    UamTextField(
                        value = name,
                        onValueChange = { name = it; errorMessage = "" },
                        label = "Nombre",
                        placeholder = "Nombre completo",
                        leadingIcon = Icons.Default.Person,
                        isError = errorMessage.isNotEmpty() && name.isBlank(),
                        errorText = if (errorMessage.isNotEmpty() && name.isBlank()) "Campo requerido" else null
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    UamTextField(
                        value = cif,
                        onValueChange = { cif = it; errorMessage = "" },
                        label = "CIF",
                        placeholder = "ID Universitario",
                        leadingIcon = Icons.Default.Badge,
                        isError = errorMessage.isNotEmpty() && cif.isBlank(),
                        errorText = if (errorMessage.isNotEmpty() && cif.isBlank()) "Campo requerido" else null
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    UamTextField(
                        value = email,
                        onValueChange = { email = it; errorMessage = "" },
                        label = "Correo",
                        placeholder = "Correo electrónico",
                        leadingIcon = Icons.Default.Email,
                        isError = errorMessage == "Correo no válido" || (errorMessage.isNotEmpty() && email.isBlank()),
                        errorText = when {
                            errorMessage == "Correo no válido" -> "Correo no válido"
                            errorMessage.isNotEmpty() && email.isBlank() -> "Campo requerido"
                            else -> null
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    UamTextField(
                        value = password,
                        onValueChange = { password = it; errorMessage = "" },
                        label = "Contraseña",
                        placeholder = "Contraseña",
                        leadingIcon = Icons.Default.Lock,
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        },
                        isError = errorMessage.isNotEmpty() && (password.isBlank() || errorMessage.contains("contraseña")),
                        errorText = when {
                            errorMessage.contains("contraseña") -> errorMessage
                            errorMessage.isNotEmpty() && password.isBlank() -> "Campo requerido"
                            else -> null
                        }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            when {
                                name.isBlank() || cif.isBlank() || email.isBlank() || password.isBlank() -> {
                                    errorMessage = "Por favor, complete todos los campos"
                                }
                                !email.contains("@") -> {
                                    errorMessage = "Correo no válido"
                                }
                                password.length < 6 -> {
                                    errorMessage = "La contraseña debe tener al menos 6 caracteres"
                                }
                                else -> {
                                    errorMessage = ""
                                    viewModel.register(
                                        RegisterRequest(
                                            name = name,
                                            cif = cif,
                                            email = email,
                                            password = password,
                                            role = selectedRole
                                        )
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        enabled = !loading
                    ) {
                        if (loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Registrarse",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    TextButton(onClick = onNavigateToLogin) {
                        Text(
                            text = "¿Ya tienes cuenta? Inicia sesión",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
