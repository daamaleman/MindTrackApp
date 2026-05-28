package ni.edu.uam.mindtrack.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ni.edu.uam.mindtrack.engine.AuthManager
import ni.edu.uam.mindtrack.ui.components.MindTrackInput
import ni.edu.uam.mindtrack.ui.components.MindTrackPrimaryButton
import ni.edu.uam.mindtrack.ui.theme.*
import ni.edu.uam.mindtrack.viewmodel.MindTrackViewModel

@Composable
fun LoginScreen(
    viewModel: MindTrackViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    AuthScreen(
        viewModel = viewModel,
        initialIsLogin = true,
        onAuthSuccess = onLoginSuccess
    )
}

@Composable
fun LoginContent(
    viewModel: MindTrackViewModel,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bienvenido",
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp,
                letterSpacing = (-0.8).sp,
                color = TextWhite
            )
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Inicia sesión para continuar",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextMuted,
                fontSize = 14.sp
            )
        )

        Spacer(Modifier.height(24.dp))

        MindTrackInput(
            value = email,
            onValueChange = { email = it; error = null },
            label = "Correo electrónico",
            placeholder = "tu@correo.com",
            leadingIcon = Icons.Outlined.MailOutline,
            keyboardType = KeyboardType.Email
        )
        Spacer(Modifier.height(18.dp))
        MindTrackInput(
            value = password,
            onValueChange = { password = it; error = null },
            label = "Contraseña",
            placeholder = "••••••••",
            leadingIcon = Icons.Outlined.Lock,
            trailingIcon = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
            onTrailingClick = { passwordVisible = !passwordVisible },
            isPassword = !passwordVisible,
            keyboardType = KeyboardType.Password
        )

        Spacer(Modifier.height(10.dp))
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            Text(
                text = "¿Olvidaste tu contraseña?",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = SecondaryAccent,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.5.sp
                )
            )
        }

        if (error != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = error!!,
                color = ImpulsiveColor,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(28.dp))

        MindTrackPrimaryButton(
            text = "Iniciar sesión",
            trailingIcon = Icons.AutoMirrored.Filled.ArrowForward,
            onClick = {
                val validation = AuthManager.validateUserLogin(email, password)
                if (validation == null) {
                    val user = AuthManager.login(email, password)
                    if (user != null) {
                        viewModel.setUser(user)
                        onLoginSuccess()
                    } else {
                        error = "Credenciales incorrectas"
                    }
                } else {
                    error = validation
                }
            }
        )
    }
}
