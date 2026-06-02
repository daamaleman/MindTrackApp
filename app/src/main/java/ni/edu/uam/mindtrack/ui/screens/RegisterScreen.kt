package ni.edu.uam.mindtrack.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ni.edu.uam.mindtrack.engine.AuthManager
import ni.edu.uam.mindtrack.model.User
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
    AuthScreen(
        viewModel = viewModel,
        initialIsLogin = false,
        onAuthSuccess = onRegisterSuccess
    )
}

@Composable
fun RegisterContent(
    viewModel: MindTrackViewModel,
    onRegisterSuccess: () -> Unit
) {
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar with camera badge
        Box(
            modifier = Modifier
                .size(96.dp)
                .clickable { launcher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Surface)
                    .border(1.5.dp, BorderColor, RoundedCornerShape(24.dp)),
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
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(40.dp)
                    )
                }
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

        Spacer(Modifier.height(22.dp))

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
            )
        }

        Spacer(Modifier.height(18.dp))

        MindTrackPrimaryButton(
            text = "Crear cuenta",
            onClick = {
                val validation = AuthManager.validateUserRegistration(fullName, email, password)
                if (validation == null) {
                    val user = User(fullName, email, password, profileImageUri)
                    if (AuthManager.register(user)) {
                        viewModel.setUser(user, profileImageUri?.toString())
                        onRegisterSuccess()
                    } else {
                        error = "El correo ya está registrado"
                    }
                } else {
                    error = validation
                }
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
    }
}
