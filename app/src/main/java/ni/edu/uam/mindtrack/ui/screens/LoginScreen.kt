package ni.edu.uam.mindtrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ni.edu.uam.mindtrack.R
import ni.edu.uam.mindtrack.engine.AuthManager
import ni.edu.uam.mindtrack.ui.components.MindTrackInput
import ni.edu.uam.mindtrack.ui.components.MindTrackPrimaryButton
import ni.edu.uam.mindtrack.ui.theme.*
import ni.edu.uam.mindtrack.viewmodel.MindTrackViewModel

@Composable
fun LoginScreen(
    viewModel: MindTrackViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToOnboarding: () -> Unit = {}
) {
    AuthScreen(
        viewModel = viewModel,
        initialIsLogin = true,
        onAuthSuccess = onLoginSuccess,
        onNavigateToOnboarding = onNavigateToOnboarding
    )
}

@Composable
fun LoginContent(
    viewModel: MindTrackViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToOnboarding: () -> Unit = {}
) {
    val apiError by viewModel.apiConnectionError.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // API Error Alert (Minimalist)
        if (apiError) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ImpulsiveColor.copy(alpha = 0.1f))
                    .border(1.dp, ImpulsiveColor.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(ImpulsiveColor)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.api_unavailable_error),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ImpulsiveColor,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        Text(
            text = stringResource(R.string.login_welcome),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp,
                letterSpacing = (-0.8).sp,
                color = TextWhite
            )
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.login_subtitle),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = TextMuted,
                fontSize = 14.sp
            )
        )

        Spacer(Modifier.height(24.dp))

        MindTrackInput(
            value = email,
            onValueChange = { email = it; error = null },
            label = stringResource(R.string.email_label),
            placeholder = stringResource(R.string.email_placeholder),
            leadingIcon = Icons.Outlined.MailOutline,
            keyboardType = KeyboardType.Email
        )
        Spacer(Modifier.height(18.dp))
        MindTrackInput(
            value = password,
            onValueChange = { password = it; error = null },
            label = stringResource(R.string.password_label),
            placeholder = stringResource(R.string.password_placeholder),
            leadingIcon = Icons.Outlined.Lock,
            trailingIcon = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
            onTrailingClick = { passwordVisible = !passwordVisible },
            isPassword = !passwordVisible,
            keyboardType = KeyboardType.Password
        )

        Spacer(Modifier.height(10.dp))
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            Text(
                text = stringResource(R.string.forgot_password),
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
            text = stringResource(R.string.login_button),
            trailingIcon = Icons.AutoMirrored.Filled.ArrowForward,
            loading = isLoading,
            enabled = !isLoading,
            onClick = {
                val validation = AuthManager.validateUserLogin(email, password)
                if (validation == null) {
                    isLoading = true
                    viewModel.loginWithApi(email) { success, message ->
                        isLoading = false
                        if (success) {
                            onLoginSuccess()
                        } else {
                            error = message ?: "Error al conectar con la API"
                        }
                    }
                } else {
                    error = validation
                }
            }
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.view_tutorial_again),
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onNavigateToOnboarding)
                .padding(8.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}
