package ni.edu.uam.mindtrack.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
<<<<<<< HEAD
import androidx.compose.foundation.rememberScrollState
=======
import androidx.compose.foundation.shape.CircleShape
>>>>>>> main
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
<<<<<<< HEAD
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.filled.Translate
=======
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
>>>>>>> main
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
<<<<<<< HEAD
import ni.edu.uam.mindtrack.ui.components.MindTrackGhostButton
import ni.edu.uam.mindtrack.ui.components.SectionLabel
=======
import coil.compose.rememberAsyncImagePainter
import ni.edu.uam.mindtrack.engine.AuthManager
>>>>>>> main
import ni.edu.uam.mindtrack.ui.theme.*
import ni.edu.uam.mindtrack.viewmodel.MindTrackViewModel

@Composable
fun SettingsScreen(viewModel: MindTrackViewModel) {
<<<<<<< HEAD
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    var notificationsEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 100.dp)
    ) {
        Text(
            text = "Ajustes",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                letterSpacing = (-0.6).sp,
                color = TextWhite
            )
        )
=======
    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogMessage by remember { mutableStateOf("") }
    
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val currentUser by AuthManager.currentUser.collectAsState()

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            AuthManager.updateProfile(
                fullName = currentUser?.fullName ?: "",
                email = currentUser?.email ?: "",
                profileImageUri = it
            )
        }
    }

    // Diálogo informativo genérico
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = dialogTitle, fontWeight = FontWeight.Bold, color = PrimaryAccent) },
            text = { Text(text = dialogMessage, color = MaterialTheme.colorScheme.onSurface) },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Entendido", color = PrimaryAccent)
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(16.dp)
        )
    }
    
    // Diálogo para editar perfil (Nombre y Correo)
    if (showEditProfileDialog) {
        var newName by remember { mutableStateOf(currentUser?.fullName ?: "") }
        var newEmail by remember { mutableStateOf(currentUser?.email ?: "") }
        var editError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Editar Perfil", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Nombre Completo") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newEmail,
                        onValueChange = { newEmail = it },
                        label = { Text("Correo Electrónico") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    if (editError != null) {
                        Text(editError!!, color = ImpulsiveColor, style = MaterialTheme.typography.labelSmall)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank() && AuthManager.validateEmail(newEmail)) {
                            if (AuthManager.updateProfile(newName, newEmail)) {
                                showEditProfileDialog = false
                            } else {
                                editError = "Error al actualizar (el correo puede estar en uso)"
                            }
                        } else {
                            editError = "Datos inválidos"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent)
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo flotante para cambiar contraseña
    if (showPasswordDialog) {
        var currentPass by remember { mutableStateOf("") }
        var newPass by remember { mutableStateOf("") }
        var confirmPass by remember { mutableStateOf("") }
        var passError by remember { mutableStateOf<String?>(null) }
        
        var currentPassVisible by remember { mutableStateOf(false) }
        var newPassVisible by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showPasswordDialog = false },
            title = { Text("Cambiar Contraseña", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = currentPass,
                        onValueChange = { currentPass = it },
                        label = { Text("Contraseña Actual") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (currentPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { currentPassVisible = !currentPassVisible }) {
                                Icon(if (currentPassVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                            }
                        }
                    )
                    OutlinedTextField(
                        value = newPass,
                        onValueChange = { newPass = it },
                        label = { Text("Nueva Contraseña") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (newPassVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { newPassVisible = !newPassVisible }) {
                                Icon(if (newPassVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                            }
                        }
                    )
                    OutlinedTextField(
                        value = confirmPass,
                        onValueChange = { confirmPass = it },
                        label = { Text("Confirmar Nueva Contraseña") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation()
                    )
                    if (passError != null) {
                        Text(passError!!, color = ImpulsiveColor, style = MaterialTheme.typography.labelSmall)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPass != confirmPass) {
                            passError = "Las contraseñas nuevas no coinciden"
                        } else {
                            val error = AuthManager.updatePassword(currentPass, newPass)
                            if (error == null) {
                                showPasswordDialog = false
                                dialogTitle = "Éxito"
                                dialogMessage = "Tu contraseña ha sido actualizada correctamente."
                                showDialog = true
                            } else {
                                passError = error
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent)
                ) {
                    Text("Actualizar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
>>>>>>> main

        Spacer(Modifier.height(18.dp))

        // Profile card
        Row(
            modifier = Modifier
<<<<<<< HEAD
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Surface)
                .background(
                    Brush.linearGradient(
                        listOf(PrimaryAccent.copy(alpha = 0.14f), PrimaryAccent.copy(alpha = 0.02f))
                    )
                )
                .border(1.dp, PrimaryEdge, RoundedCornerShape(20.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with initials
            Box(
=======
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Seguridad",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            SettingsItem(
                icon = Icons.Default.VpnKey,
                label = "Cambiar Contraseña",
                onClick = { showPasswordDialog = true }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Preferencias",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Item de Tema
            Row(
>>>>>>> main
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(listOf(PrimaryAccent, SecondaryAccent))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "DA",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = Color.White,
                        letterSpacing = (-0.5).sp
                    )
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Daniela A.",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextWhite
                    )
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "daniela@uam.edu.ni",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                )
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceVariant)
                    .border(1.dp, BorderColor, RoundedCornerShape(10.dp))
                    .clickable {},
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(Modifier.height(22.dp))
        SectionLabel(text = "Preferencias")
        Spacer(Modifier.height(10.dp))

        SettingsRow(
            icon = Icons.Filled.DarkMode,
            label = "Modo oscuro",
            sub = "Activado siempre",
            onClick = { viewModel.toggleTheme() }
        ) {
            Switch(
                checked = isDarkMode,
                onCheckedChange = { viewModel.toggleTheme() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = PrimaryAccent,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = SurfaceElevated,
                    uncheckedBorderColor = SurfaceElevated
                )
            )
        }
        Spacer(Modifier.height(8.dp))
        SettingsRow(
            icon = Icons.Outlined.Notifications,
            label = "Notificaciones",
            sub = "Recordatorios diarios",
            onClick = { notificationsEnabled = !notificationsEnabled }
        ) {
            Switch(
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = PrimaryAccent,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = SurfaceElevated,
                    uncheckedBorderColor = SurfaceElevated
                )
            )
        }
        Spacer(Modifier.height(8.dp))
        SettingsRow(
            icon = Icons.Filled.Translate,
            label = "Idioma",
            sub = "Español",
            onClick = {}
        )

        Spacer(Modifier.height(22.dp))
        SectionLabel(text = "Cuenta y soporte")
        Spacer(Modifier.height(10.dp))

        SettingsRow(
            icon = Icons.Outlined.Lock,
            label = "Privacidad y seguridad",
            onClick = {}
        )
        Spacer(Modifier.height(8.dp))
        SettingsRow(
            icon = Icons.AutoMirrored.Filled.HelpOutline,
            label = "Ayuda y soporte",
            onClick = {}
        )
        Spacer(Modifier.height(8.dp))
        SettingsRow(
            icon = Icons.Outlined.Info,
            label = "Acerca de MindTrack",
            sub = "Versión 1.0.0",
            onClick = {}
        )

        Spacer(Modifier.height(18.dp))

        MindTrackGhostButton(
            text = "Cerrar sesión",
            leadingIcon = Icons.AutoMirrored.Filled.Logout,
            contentColor = ImpulsiveColor,
            onClick = {}
        )
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    label: String,
    sub: String? = null,
    onClick: () -> Unit,
    trail: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Surface)
            .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = TextWhite
                )
            )
            if (sub != null) {
                Spacer(Modifier.height(1.dp))
                Text(
                    text = sub,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextMuted,
                        fontSize = 11.5.sp
                    )
                )
            }
<<<<<<< HEAD
        }
        if (trail != null) {
            trail()
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(18.dp)
=======

            Spacer(modifier = Modifier.height(32.dp))

            SettingsItem(
                icon = Icons.AutoMirrored.Filled.Logout,
                label = "Cerrar Sesión",
                onClick = { AuthManager.logout() }
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Versión 1.3.0",
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
>>>>>>> main
            )
        }
    }
}
