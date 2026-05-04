package ni.edu.uam.mindtrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ni.edu.uam.mindtrack.engine.AuthManager
import ni.edu.uam.mindtrack.model.User
import ni.edu.uam.mindtrack.ui.components.AuthToggle
import ni.edu.uam.mindtrack.ui.components.MindTrackInput
import ni.edu.uam.mindtrack.ui.components.MindTrackPrimaryButton
import ni.edu.uam.mindtrack.ui.theme.*

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 28.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar with camera badge
            Box(
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