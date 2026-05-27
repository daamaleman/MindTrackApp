package ni.edu.uam.mindtrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import ni.edu.uam.mindtrack.engine.AuthManager
import ni.edu.uam.mindtrack.model.User
import ni.edu.uam.mindtrack.ui.components.MindTrackButton
import ni.edu.uam.mindtrack.ui.theme.*
import ni.edu.uam.mindtrack.viewmodel.MindTrackViewModel

@Composable
fun RegisterScreen(
    viewModel: MindTrackViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    AuthScreen(
        viewModel = viewModel,
        initialIsLogin = false, 
        onAuthSuccess = onRegisterSuccess
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterContent(
    viewModel: MindTrackViewModel,
    onRegisterSuccess: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icono de persona con cámara
        Box(
            modifier = Modifier.size(110.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .shadow(elevation = 10.dp, shape = CircleShape, spotColor = PrimaryAccent)
                    .background(SurfaceVariant, CircleShape)
                    .border(2.dp, PrimaryAccent.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(60.dp)
                )
            }
            
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(32.dp)
                    .shadow(4.dp, CircleShape),
                shape = CircleShape,
                color = PrimaryAccent
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Change photo",
                    tint = Color.White,
                    modifier = Modifier.padding(6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Burbuja (Card) con el formulario
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 30.dp, shape = RoundedCornerShape(32.dp), spotColor = PrimaryAccent.copy(alpha = 0.2f)),
            colors = CardDefaults.cardColors(containerColor = Surface),
            shape = RoundedCornerShape(32.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { 
                        fullName = it
                        error = null
                    },
                    label = { Text("Nombre completo") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryAccent) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryAccent,
                        unfocusedBorderColor = BorderColor,
                        focusedContainerColor = SurfaceVariant.copy(alpha = 0.5f),
                        unfocusedContainerColor = SurfaceVariant.copy(alpha = 0.5f),
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        cursorColor = PrimaryAccent
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { 
                        email = it
                        error = null
                    },
                    label = { Text("Correo electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = PrimaryAccent) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryAccent,
                        unfocusedBorderColor = BorderColor,
                        focusedContainerColor = SurfaceVariant.copy(alpha = 0.5f),
                        unfocusedContainerColor = SurfaceVariant.copy(alpha = 0.5f),
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        cursorColor = PrimaryAccent
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { 
                        password = it
                        error = null
                    },
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryAccent) },
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = null, tint = TextMuted)
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryAccent,
                        unfocusedBorderColor = BorderColor,
                        focusedContainerColor = SurfaceVariant.copy(alpha = 0.5f),
                        unfocusedContainerColor = SurfaceVariant.copy(alpha = 0.5f),
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        cursorColor = PrimaryAccent
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                if (error != null) {
                    Text(
                        text = error!!,
                        color = ImpulsiveColor,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                MindTrackButton(
                    text = "Registrarse",
                    onClick = {
                        val validationError = AuthManager.validateUserRegistration(fullName, email, password)
                        if (validationError == null) {
                            val user = User(fullName, email, password)
                            if (AuthManager.register(user)) {
                                viewModel.setUser(user)
                                onRegisterSuccess()
                            } else {
                                error = "El correo ya está registrado"
                            }
                        } else {
                            error = validationError
                        }
                    }
                )
            }
        }
    }
}
