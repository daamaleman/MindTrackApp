package ni.edu.uam.mindtrack.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ni.edu.uam.mindtrack.model.UserProfile
import ni.edu.uam.mindtrack.ui.components.MindTrackButton
import ni.edu.uam.mindtrack.ui.theme.*
import ni.edu.uam.mindtrack.viewmodel.MindTrackViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: MindTrackViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val context = LocalContext.current
    val originalProfile by viewModel.userProfile.collectAsState()
    val avatarUri by viewModel.avatarUri.collectAsState()

    // Local State
    var name by remember { mutableStateOf(originalProfile.name) }
    var email by remember { mutableStateOf(originalProfile.email) }
    var age by remember { mutableStateOf(originalProfile.age) }
    var gender by remember { mutableStateOf(originalProfile.gender) }
    var bio by remember { mutableStateOf(originalProfile.bio) }
    var dailyReminder by remember { mutableStateOf(originalProfile.dailyReminder) }

    var showDiscardDialog by remember { mutableStateOf(false) }

    // Validation States
    val isNameValid = name.trim().length in 2..50
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    val isEmailValid = emailRegex.matches(email)
    val isAgeValid = age.isEmpty() || (age.toIntOrNull() != null && age.toInt() in 13..99)
    
    val canSave = isNameValid && isEmailValid && isAgeValid && name.trim().isNotEmpty()

    val hasChanges = name != originalProfile.name || 
                     email != originalProfile.email || 
                     age != originalProfile.age || 
                     gender != originalProfile.gender || 
                     bio != originalProfile.bio || 
                     dailyReminder != originalProfile.dailyReminder ||
                     avatarUri?.toString() != originalProfile.profileImageUri

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            viewModel.setAvatarUri(uri)
        }
    )

    fun handleBack() {
        if (hasChanges) {
            showDiscardDialog = true
        } else {
            onBack()
        }
    }

    BackHandler {
        handleBack()
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("¿Descartar cambios?", color = PrimaryAccent, fontWeight = FontWeight.Bold) },
            text = { Text("Tienes cambios sin guardar. ¿Deseas salir sin guardar?") },
            containerColor = SurfaceVariant,
            shape = RoundedCornerShape(16.dp),
            confirmButton = {
                TextButton(onClick = { 
                    showDiscardDialog = false
                    onBack() 
                }) {
                    Text("Descartar", color = ImpulsiveColor, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text("Seguir editando", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Editar perfil", 
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { handleBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            val newProfile = originalProfile.copy(
                                name = name,
                                email = email,
                                age = age,
                                gender = gender,
                                bio = bio,
                                dailyReminder = dailyReminder,
                                profileImageUri = avatarUri?.toString()
                            )
                            if (viewModel.updateProfile(newProfile)) {
                                Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                                onSaved()
                            } else {
                                Toast.makeText(context, "No se pudo guardar el perfil", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = canSave
                    ) {
                        Text(
                            "Guardar",
                            color = if (canSave) PrimaryAccent else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background,
                    titleContentColor = TextWhite,
                    navigationIconContentColor = TextWhite
                )
            )
        },
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar Section
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    modifier = Modifier
                        .size(120.dp)
                        .clickable { 
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            ) 
                        },
                    shape = CircleShape,
                    color = PrimaryAccent.copy(alpha = 0.6f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (avatarUri != null) {
                            AsyncImage(
                                model = avatarUri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else if (originalProfile.profileImageUri != null) {
                             AsyncImage(
                                model = originalProfile.profileImageUri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = name.firstOrNull()?.toString() ?: "U",
                                style = MaterialTheme.typography.displayMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Black
                                )
                            )
                        }
                    }
                }
                Surface(
                    modifier = Modifier
                        .size(36.dp)
                        .offset(x = (-4).dp, y = (-4).dp)
                        .clickable { 
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            ) 
                        },
                    shape = CircleShape,
                    color = SurfaceVariant,
                    border = BorderStroke(2.dp, Background)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.PhotoCamera,
                            contentDescription = "Cambiar foto",
                            tint = PrimaryAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Cambiar foto de perfil",
                modifier = Modifier.clickable { 
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    ) 
                },
                style = MaterialTheme.typography.labelMedium.copy(
                    color = PrimaryAccent,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Form Section
            SectionLabel("INFORMACIÓN PERSONAL")
            
            Spacer(modifier = Modifier.height(16.dp))

            // Name Field
            EditProfileField(
                label = "Nombre completo",
                value = name,
                onValueChange = { name = it },
                icon = Icons.Default.Person,
                error = if (!isNameValid && name.isNotEmpty()) "Nombre debe tener entre 2 y 50 caracteres" else null
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Email Field
            EditProfileField(
                label = "Correo electrónico",
                value = email,
                onValueChange = { email = it },
                icon = Icons.Default.Email,
                error = if (!isEmailValid && email.isNotEmpty()) "Correo electrónico inválido" else null
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                // Age Field
                Box(modifier = Modifier.weight(1f)) {
                    EditProfileField(
                        label = "Edad",
                        value = age,
                        onValueChange = { if (it.all { char -> char.isDigit() }) age = it },
                        error = if (!isAgeValid) "Edad inválida (13-99)" else null
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))

                // Gender Field
                Box(modifier = Modifier.weight(1.2f)) {
                    GenderDropdown(
                        selectedGender = gender,
                        onGenderSelected = { gender = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bio Field
            EditProfileField(
                label = "Biografía",
                value = bio,
                onValueChange = { if (it.length <= 240) bio = it },
                singleLine = false,
                minLines = 3,
                maxLines = 5,
                counter = "${bio.length}/240"
            )

            Spacer(modifier = Modifier.height(40.dp))

            SectionLabel("PREFERENCIAS DE SIMULACIÓN")
            
            Spacer(modifier = Modifier.height(16.dp))

            // Preferences Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = SurfaceVariant,
                border = BorderStroke(1.dp, BorderColor.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Recordatorio diario",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            "Recibe una notificación a las 7 PM",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = dailyReminder,
                        onCheckedChange = { dailyReminder = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = PrimaryAccent,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color.Gray.copy(alpha = 0.5f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            MindTrackButton(
                text = "Guardar cambios",
                onClick = {
                    val newProfile = originalProfile.copy(
                        name = name,
                        email = email,
                        age = age,
                        gender = gender,
                        bio = bio,
                        dailyReminder = dailyReminder,
                        profileImageUri = avatarUri?.toString()
                    )
                    if (viewModel.updateProfile(newProfile)) {
                        Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                        onSaved()
                    } else {
                        Toast.makeText(context, "No se pudo guardar el perfil", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = canSave
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            MindTrackButton(
                text = "Cancelar",
                onClick = { handleBack() },
                isSecondary = true
            )
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp
        )
    )
}

@Composable
fun EditProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    error: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = 1,
    counter: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = icon?.let { { Icon(it, contentDescription = null, tint = PrimaryAccent) } },
            singleLine = singleLine,
            minLines = minLines,
            maxLines = maxLines,
            shape = RoundedCornerShape(14.dp),
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
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (error != null) {
                Text(
                    text = error,
                    color = ImpulsiveColor,
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }
            
            if (counter != null) {
                Text(
                    text = counter,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderDropdown(
    selectedGender: String,
    onGenderSelected: (String) -> Unit
) {
    val genders = listOf("Femenino", "Masculino", "No binario", "Prefiero no decir")
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Género",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedGender,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryAccent,
                    unfocusedBorderColor = BorderColor,
                    focusedContainerColor = SurfaceVariant.copy(alpha = 0.5f),
                    unfocusedContainerColor = SurfaceVariant.copy(alpha = 0.5f),
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(SurfaceVariant)
            ) {
                genders.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, color = TextWhite) },
                        onClick = {
                            onGenderSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
