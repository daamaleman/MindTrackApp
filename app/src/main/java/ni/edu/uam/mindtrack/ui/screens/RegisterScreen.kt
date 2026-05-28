package ni.edu.uam.mindtrack.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
<<<<<<< HEAD
import androidx.compose.ui.graphics.Brush
=======
import androidx.compose.ui.draw.shadow
>>>>>>> main
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
<<<<<<< HEAD
import androidx.compose.ui.unit.sp
=======
import coil.compose.AsyncImage
>>>>>>> main
import ni.edu.uam.mindtrack.engine.AuthManager
import ni.edu.uam.mindtrack.model.User
import ni.edu.uam.mindtrack.ui.components.AuthToggle
import ni.edu.uam.mindtrack.ui.components.MindTrackInput
import ni.edu.uam.mindtrack.ui.components.MindTrackPrimaryButton
import ni.edu.uam.mindtrack.ui.theme.*
import ni.edu.uam.mindtrack.viewmodel.MindTrackViewModel

@Composable
fun RegisterScreen(
    viewModel: MindTrackViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
<<<<<<< HEAD
=======
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
>>>>>>> main
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var profileImageUri by remember { mutableStateOf<android.net.Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        profileImageUri = uri
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(PrimaryAccent.copy(alpha = 0.18f), Color.Transparent),
                    radius = 900f
                )
            )
            .background(Background.copy(alpha = 0f))
    ) {
<<<<<<< HEAD
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 28.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
=======
        // Icono de persona con cámara
        Box(
            modifier = Modifier
                .size(110.dp)
                .clickable { launcher.launch("image/*") },
            contentAlignment = Alignment.Center
>>>>>>> main
        ) {
            // Avatar with camera badge
            Box(
<<<<<<< HEAD
                modifier = Modifier.size(96.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(84.dp)
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Surface)
                        .border(1.5.dp, PrimaryEdge, RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(40.dp)
=======
                modifier = Modifier
                    .size(100.dp)
                    .shadow(elevation = 10.dp, shape = CircleShape, spotColor = PrimaryAccent)
                    .background(SurfaceVariant, CircleShape)
                    .border(2.dp, PrimaryAccent.copy(alpha = 0.5f), CircleShape)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (profileImageUri != null) {
                    AsyncImage(
                        model = profileImageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(60.dp)
                    )
                }
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
>>>>>>> main
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(PrimaryAccent)
                        .border(2.5.dp, Background, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PhotoCamera,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))
            Text(
                text = "Crea tu cuenta",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    letterSpacing = (-0.6).sp,
                    color = TextWhite
                )
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Empieza tu primera simulación",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextMuted,
                    fontSize = 13.sp
                )
            )

<<<<<<< HEAD
            Spacer(Modifier.height(22.dp))

            AuthToggle(
                isLogin = false,
                onToggle = { isLogin ->
                    if (isLogin) onNavigateToLogin()
                }
            )

            Spacer(Modifier.height(20.dp))

            MindTrackInput(
                value = fullName,
                onValueChange = { fullName = it; error = null },
                label = "Nombre completo",
                placeholder = "Tu nombre",
                leadingIcon = Icons.Outlined.Person
            )
            Spacer(Modifier.height(16.dp))
            MindTrackInput(
                value = email,
                onValueChange = { email = it; error = null },
                label = "Correo electrónico",
                placeholder = "tu@correo.com",
                leadingIcon = Icons.Outlined.MailOutline,
                keyboardType = KeyboardType.Email
            )
            Spacer(Modifier.height(16.dp))
            MindTrackInput(
                value = password,
                onValueChange = { password = it; error = null },
                label = "Contraseña",
                placeholder = "••••••••",
                leadingIcon = Icons.Outlined.Lock,
                trailingIcon = Icons.Outlined.VisibilityOff,
                isPassword = true,
                keyboardType = KeyboardType.Password
            )

            if (error != null) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = error!!,
                    color = ImpulsiveColor,
                    style = MaterialTheme.typography.bodySmall
=======
                MindTrackButton(
                    text = "Registrarse",
                    onClick = {
                        val validationError = AuthManager.validateUserRegistration(fullName, email, password)
                        if (validationError == null) {
                            val user = User(fullName, email, password)
                            if (AuthManager.register(user)) {
                                viewModel.setUser(user, profileImageUri?.toString())
                                onRegisterSuccess()
                            } else {
                                error = "El correo ya está registrado"
                            }
                        } else {
                            error = validationError
                        }
                    }
>>>>>>> main
                )
            }

            Spacer(Modifier.height(18.dp))

            MindTrackPrimaryButton(
                text = "Crear cuenta",
                onClick = {
                    val validation = AuthManager.validateUserRegistration(fullName, email, password)
                    if (validation == null) {
                        val user = User(fullName, email, password)
                        if (AuthManager.register(user)) onRegisterSuccess()
                        else error = "El correo ya está registrado"
                    } else error = validation
                }
            )

            Spacer(Modifier.height(14.dp))
            Text(
                text = "Al registrarte aceptas nuestros Términos y Política de privacidad",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextFaint,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(Modifier.weight(1f))
        }
    }
}