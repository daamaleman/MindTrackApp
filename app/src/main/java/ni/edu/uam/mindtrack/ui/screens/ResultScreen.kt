package ni.edu.uam.mindtrack.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ni.edu.uam.mindtrack.ui.components.MindTrackButton
import ni.edu.uam.mindtrack.ui.theme.*
import ni.edu.uam.mindtrack.viewmodel.MindTrackViewModel

@Composable
fun ResultScreen(
    viewModel: MindTrackViewModel,
    onBackHome: () -> Unit
) {
    val resultProfile by viewModel.currentResult.collectAsState()
    val distribution = viewModel.getDistribution()
    
    var startAnimation by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.8f,
        animationSpec = tween(600),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        if (MaterialTheme.colorScheme.background == Background) Color(0xFF16122A) else Color(0xFFE9ECEF),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        // Radial glow
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp)
                .size(300.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(PrimaryAccent.copy(alpha = 0.22f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .scale(scale),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Badge
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .shadow(40.dp, CircleShape, spotColor = PrimaryAccent)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(PrimaryAccent, Color(0xFFC084FC))
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(56.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "TU PERFIL DECISIONAL",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = PrimaryAccent,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = resultProfile,
                style = MaterialTheme.typography.displayMedium.copy(
                    brush = Brush.linearGradient(
                        colors = listOf(MaterialTheme.colorScheme.onSurface, SecondaryAccent)
                    )
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            val description = when (resultProfile) {
                "Racional" -> "Analizas cada situación con lógica antes de actuar. Tus decisiones son sólidas y bien fundamentadas."
                "Impulsivo" -> "Confías en tu instinto y actúas rápido. Tu espontaneidad es una fortaleza, aunque a veces conviene pausar."
                else -> "Combinas lógica y emoción de forma balanceada. Reflexivo pero adaptable ante cada situación."
            }

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Trait Pills
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val traits = when (resultProfile) {
                    "Racional" -> listOf("🧠" to "Analítico", "📊" to "Lógico", "🎯" to "Preciso")
                    "Impulsivo" -> listOf("⚡" to "Rápido", "🔥" to "Audaz", "🌊" to "Fluido")
                    else -> listOf("⚖️" to "Equilibrado", "🧩" to "Versátil", "💡" to "Prudente")
                }
                traits.forEach { (emoji, label) ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = emoji, fontSize = 20.sp)
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Distribution Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "DISTRIBUCIÓN",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                DistributionBar("Racional", distribution["Racional"] ?: 0f, RationalColor)
                DistributionBar("Equilibrado", distribution["Equilibrado"] ?: 0f, BalancedColor)
                DistributionBar("Impulsivo", distribution["Impulsivo"] ?: 0f, ImpulsiveColor)
            }

            Spacer(modifier = Modifier.height(32.dp))

            MindTrackButton(
                text = "↩  Volver al inicio",
                onClick = onBackHome
            )
        }
    }
}

@Composable
fun DistributionBar(label: String, progress: Float, color: Color) {
    val animatedWidth by animateFloatAsState(targetValue = progress, label = "barWidth")
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.width(80.dp),
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(4.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedWidth)
                    .background(color, RoundedCornerShape(4.dp))
            )
        }
        Text(
            text = "${(progress * 100).toInt()}%",
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.bodySmall.copy(
                color = color,
                fontWeight = FontWeight.Bold
            )
        )
    }
}
