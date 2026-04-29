package ni.edu.uam.mindtrack.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ni.edu.uam.mindtrack.ui.components.MindTrackButton
import ni.edu.uam.mindtrack.ui.components.StatCard
import ni.edu.uam.mindtrack.ui.theme.PrimaryAccent
import ni.edu.uam.mindtrack.ui.theme.SecondaryAccent
import ni.edu.uam.mindtrack.ui.theme.TextMuted
import ni.edu.uam.mindtrack.ui.theme.TextWhite

@Composable
fun HomeScreen(
    onStartSimulation: () -> Unit,
    onViewHistory: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Radial glow background
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp)
                .size(300.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            PrimaryAccent.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + expandVertically()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Logo Circle
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .shadow(20.dp, CircleShape, spotColor = PrimaryAccent)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(PrimaryAccent, SecondaryAccent)
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(56.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // App Title
                    Text(
                        text = "MindTrack",
                        style = MaterialTheme.typography.displayMedium.copy(
                            brush = Brush.linearGradient(
                                colors = listOf(Color.White, SecondaryAccent)
                            )
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Tagline
                    Text(
                        text = "Descubre tu perfil de toma\nde decisiones en 3 minutos",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = TextMuted,
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    MindTrackButton(
                        text = "▶  Iniciar Simulación",
                        onClick = onStartSimulation
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    MindTrackButton(
                        text = "📋  Ver Historial",
                        onClick = onViewHistory,
                        isSecondary = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it })
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatCard(number = "3", label = "Escenarios")
                    StatCard(number = "~3'", label = "Duración")
                    StatCard(number = "3", label = "Perfiles")
                }
            }
        }
    }
}
