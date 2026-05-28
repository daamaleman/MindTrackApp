package ni.edu.uam.mindtrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Psychology
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ni.edu.uam.mindtrack.engine.AuthManager
import ni.edu.uam.mindtrack.ui.components.AuthToggle
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
<<<<<<< HEAD
=======
    AuthScreen(
        viewModel = viewModel,
        initialIsLogin = true,
        onAuthSuccess = onLoginSuccess
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginContent(
    viewModel: MindTrackViewModel,
    onLoginSuccess: () -> Unit
) {
>>>>>>> main
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
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
            .background(Background.copy(alpha = 0.6f))
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Background.copy(alpha = 0f)))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 56.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF8567FF), Color(0xFF6B4FF0))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
<<<<<<< HEAD
                androidx.compose.material3.Icon(
                    imageVector = Icons.Filled.Psychology,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
=======
                OutlinedTextField(
                    value = email,
                    onValueChange = { 
                        email = it
                        error = null
                    },
                    label = { Text("Correo electrónico") },
                    placeholder = { Text("ejemplo@correo.com") },
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
                    text = "Iniciar sesión",
                    onClick = {
                        val validationError = AuthManager.validateUserLogin(email, password)
                        if (validationError == null) {
                            val user = AuthManager.login(email, password)
                            if (user != null) {
                                viewModel.setUser(user)
                                onLoginSuccess()
                            } else {
                                error = "Credenciales incorrectas"
                            }
                        } else {
                            error = validationError
                        }
                    }
>>>>>>> main
                )
            }
            Spacer(Modifier.height(16.dp))
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

            Spacer(Modifier.height(32.dp))

            AuthToggle(
                isLogin = true,
                onToggle = { isLogin ->
                    if (!isLogin) onNavigateToRegister()
                }
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
                        if (AuthManager.login(email, password)) onLoginSuccess()
                        else error = "Credenciales incorrectas"
                    } else error = validation
                }
            )

            Spacer(Modifier.height(40.dp))
            Spacer(Modifier.weight(1f))

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = TextMuted)) { append("¿No tienes cuenta? ") }
                    withStyle(
                        SpanStyle(
                            color = SecondaryAccent,
                            fontWeight = FontWeight.Bold
                        )
                    ) { append("Crea una") }
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onNavigateToRegister)
                    .padding(8.dp),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.5.sp,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}
