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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.tutoruam_proyecto.ui.theme.UamBackground
import com.example.tutoruam_proyecto.ui.theme.UamOnSurfaceVariant
import com.example.tutoruam_proyecto.ui.theme.UamPrimary
import com.example.tutoruam_proyecto.ui.theme.UamSurfaceVariant
import com.example.tutoruam_proyecto.ui.theme.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    var email by remember { mutableStateOf("") }
    var cif by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(UamBackground)
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo circular
                Surface(
                    modifier = Modifier.size(70.dp),
                    shape = RoundedCornerShape(35.dp),
                    color = UamPrimary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "UAM",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Universidad Americana",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = UamPrimary
                )

                Text(
                    text = "Ingresa tus credenciales",
                    style = MaterialTheme.typography.bodyMedium,
                    color = UamOnSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Email Field
                UamTextField(
                    value = email,
                    onValueChange = { email = it; errorMessage = "" },
                    label = "Correo",
                    placeholder = "Correo",
                    leadingIcon = Icons.Default.Email,
                    isError = errorMessage == "Correo no válido" || (errorMessage.isNotEmpty() && email.isBlank()),
                    errorText = if (errorMessage == "Correo no válido") "Correo no válido" else if (errorMessage.isNotEmpty() && email.isBlank()) "Campo requerido" else null
                )

                Spacer(modifier = Modifier.height(8.dp))

                // CIF Field
                UamTextField(
                    value = cif,
                    onValueChange = { cif = it; errorMessage = "" },
                    label = "CIF",
                    placeholder = "CIF",
                    leadingIcon = Icons.Default.Badge,
                    isError = errorMessage.isNotEmpty() && cif.isBlank(),
                    errorText = if (errorMessage.isNotEmpty() && cif.isBlank()) "Campo requerido" else null
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Password Field
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
                                tint = UamPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    isError = errorMessage.isNotEmpty() && password.isBlank(),
                    errorText = if (errorMessage.isNotEmpty() && password.isBlank()) "Campo requerido" else null
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        when {
                            email.isBlank() || cif.isBlank() || password.isBlank() -> {
                                errorMessage = "Por favor, complete todos los campos"
                            }
                            !email.contains("@") || !email.contains(".com") -> {
                                errorMessage = "Correo no válido"
                            }
                            else -> {
                                errorMessage = ""
                                scope.launch {
                                    loading = true
                                    delay(1000)
                                    loading = false
                                    onLoginSuccess()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = UamPrimary),
                    enabled = !loading
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Iniciar sesión",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UamTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorText: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = UamOnSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.bodyMedium,
            placeholder = { Text(placeholder, style = MaterialTheme.typography.bodyMedium) },
            leadingIcon = { Icon(leadingIcon, contentDescription = null, tint = UamPrimary, modifier = Modifier.size(20.dp)) },
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = UamPrimary,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = UamSurfaceVariant,
                unfocusedContainerColor = UamSurfaceVariant,
                errorContainerColor = UamSurfaceVariant,
                errorBorderColor = MaterialTheme.colorScheme.error
            ),
            isError = isError,
            singleLine = true
        )
        if (isError && !errorText.isNullOrEmpty()) {
            Text(
                text = errorText,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )
        }
    }
}
