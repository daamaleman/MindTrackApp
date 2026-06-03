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
import ni.edu.uam.mindtrack.viewmodel.EditProfileViewModel
import ni.edu.uam.mindtrack.viewmodel.ProfileViewModel
import ni.edu.uam.mindtrack.data.repository.Result
import ni.edu.uam.mindtrack.engine.AuthManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    editProfileViewModel: EditProfileViewModel,
    profileViewModel: ProfileViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val context = LocalContext.current
    val profileState by profileViewModel.uiState.collectAsState()
    val updateState by editProfileViewModel.updateState.collectAsState()
    val currentUserId = AuthManager.currentUser.value?.id ?: 1L

    // Local State
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var photoUrl by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(profileState) {
        if (profileState is Result.Success) {
            val user = (profileState as Result.Success).data
            name = user.nombre
            email = user.correo
            bio = user.biografia ?: ""
            photoUrl = user.fotoPerfil ?: ""
        }
    }

    LaunchedEffect(updateState) {
        when (updateState) {
            is Result.Success -> {
                Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                editProfileViewModel.clearUpdateState()
                profileViewModel.loadProfile(currentUserId)
                onSaved()
            }
            is Result.Error -> {
                snackbarHostState.showSnackbar((updateState as Result.Error).message)
            }
            else -> {}
        }
    }

    // Validation States
    val isNameValid = name.trim().length in 2..50
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    val isEmailValid = emailRegex.matches(email)
    
    val canSave = isNameValid && isEmailValid && name.trim().isNotEmpty() && updateState !is Result.Loading

    fun handleBack() {
        onBack()
    }

    BackHandler {
        handleBack()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                    if (updateState is Result.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp).padding(end = 16.dp),
                            color = PrimaryAccent,
                            strokeWidth = 2.dp
                        )
                    } else {
                        TextButton(
                            onClick = {
                                editProfileViewModel.updateProfile(currentUserId, name, email, photoUrl, bio)
                            },
                            enabled = canSave
                        ) {
                            Text(
                                "Guardar",
                                color = if (canSave) PrimaryAccent else TextMuted,
                                fontWeight = FontWeight.Bold
                            )
                        }
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
                    modifier = Modifier.size(120.dp),
                    shape = CircleShape,
                    color = PrimaryAccent.copy(alpha = 0.6f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (photoUrl.isNotEmpty()) {
                            AsyncImage(
                                model = photoUrl,
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
            }

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
                error = if (!isNameValid && name.isNotEmpty()) "Nombre inválido" else null
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

            // Photo URL Field
            EditProfileField(
                label = "URL de Foto de Perfil",
                value = photoUrl,
                onValueChange = { photoUrl = it },
                icon = Icons.Default.Link
            )

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

            MindTrackButton(
                text = "Guardar cambios",
                onClick = {
                    editProfileViewModel.updateProfile(currentUserId, name, email, photoUrl, bio)
                },
                enabled = canSave,
                loading = updateState is Result.Loading
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
