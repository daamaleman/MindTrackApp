package ni.edu.uam.mindtrack.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ni.edu.uam.mindtrack.ui.components.AuthToggle
import ni.edu.uam.mindtrack.ui.theme.Background
import ni.edu.uam.mindtrack.ui.theme.PrimaryAccent
import ni.edu.uam.mindtrack.ui.theme.TextMuted
import ni.edu.uam.mindtrack.ui.theme.TextWhite

@Composable
fun AuthScreen(
    initialIsLogin: Boolean = true,
    onAuthSuccess: () -> Unit
) {
    var isLogin by remember { mutableStateOf(initialIsLogin) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // SECCIÓN DE BIENVENIDA (Fija arriba)
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .shadow(elevation = 20.dp, shape = CircleShape, spotColor = PrimaryAccent)
                    .background(PrimaryAccent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "MindTrack",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = TextWhite,
                    letterSpacing = 1.sp
                )
            )

            Text(
                text = "Explora tu forma de pensar",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = TextMuted
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // LA BURBUJA FLOTANTE (SELECTOR) - Debajo de la bienvenida
            AuthToggle(
                isLogin = isLogin,
                onToggle = { isLogin = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // CONTENIDO ANIMADO (LOS FORMULARIOS)
            AnimatedContent(
                targetState = isLogin,
                transitionSpec = {
                    if (targetState) {
                        slideInHorizontally(animationSpec = tween(400)) { -it } + fadeIn() togetherWith
                                slideOutHorizontally(animationSpec = tween(400)) { it } + fadeOut()
                    } else {
                        slideInHorizontally(animationSpec = tween(400)) { it } + fadeIn() togetherWith
                                slideOutHorizontally(animationSpec = tween(400)) { -it } + fadeOut()
                    }
                },
                label = "authTransition"
            ) { targetIsLogin ->
                if (targetIsLogin) {
                    LoginScreen(
                        onLoginSuccess = onAuthSuccess,
                        onNavigateToRegister = { isLogin = false }
                    )
                } else {
                    RegisterScreen(
                        onRegisterSuccess = onAuthSuccess,
                        onNavigateToLogin = { isLogin = true }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
