package ni.edu.uam.mindtrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ni.edu.uam.mindtrack.ui.components.IconCircleButton
import ni.edu.uam.mindtrack.ui.components.MindTrackPrimaryButton
import ni.edu.uam.mindtrack.ui.components.MindTrackSecondaryButton
import ni.edu.uam.mindtrack.ui.theme.*
import ni.edu.uam.mindtrack.viewmodel.MindTrackViewModel

@Composable
fun ResultScreen(
    viewModel: MindTrackViewModel,
    onBackHome: () -> Unit
) {
    val finalResult by viewModel.finalResult.collectAsState()
    val playerState by viewModel.playerState.collectAsState()

    val profileColor = when {
        finalResult.contains("racional", true) || finalResult.contains("exitoso", true) -> RationalColor
        finalResult.contains("equilibr", true) || finalResult.contains("estratégico", true) -> BalancedColor
        finalResult.contains("impulsiv", true) || finalResult.contains("crítico", true) -> ImpulsiveColor
        else -> SecondaryAccent
    }

    val title = if (finalResult.isNotBlank()) finalResult else "Pensador Racional"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .background(
                Brush.radialGradient(
                    colors = listOf(profileColor.copy(alpha = 0.10f), Color.Transparent),
                    radius = 700f
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 32.dp)
        ) {
            // Top row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconCircleButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    onClick = onBackHome,
                    size = 38.dp,
                    iconSize = 18.dp
                )
                IconCircleButton(
                    icon = Icons.Filled.IosShare,
                    onClick = {},
                    size = 38.dp,
                    iconSize = 18.dp
                )
            }

            Spacer(Modifier.height(20.dp))

            // Centered profile icon
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(112.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(112.dp)
                            .clip(RoundedCornerShape(36.dp))
                            .background(profileColor.copy(alpha = 0.18f))
                    )
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(profileColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Psychology,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "TU PERFIL",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = profileColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 1.2.sp
                    )
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 30.sp,
                        lineHeight = 33.sp,
                        letterSpacing = (-1).sp,
                        color = TextWhite,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Tomas decisiones analizando consecuencias a largo plazo y priorizas el equilibrio entre estabilidad y progreso.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary,
                        fontSize = 13.5.sp,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            // Stats card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Surface)
                    .border(1.dp, BorderColor, RoundedCornerShape(20.dp))
                    .padding(18.dp)
            ) {
                Text(
                    text = "ESTADO FINAL",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextMuted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.8.sp
                    )
                )
                Spacer(Modifier.height(12.dp))
                ResultStatRow(Icons.Filled.Bolt, "Energía", playerState.energy, EnergyColor)
                ResultStatRow(Icons.Filled.SelfImprovement, "Estrés", playerState.stress, StressColor)
                ResultStatRow(Icons.AutoMirrored.Filled.TrendingUp, "Progreso", playerState.progress, ProgressColor)
                ResultStatRow(Icons.Filled.Payments, "Dinero", playerState.money, MoneyColor)
            }

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MindTrackSecondaryButton(
                    text = "Inicio",
                    leadingIcon = Icons.Filled.Home,
                    onClick = onBackHome,
                    modifier = Modifier.weight(1f)
                )
                MindTrackPrimaryButton(
                    text = "Reiniciar simulación",
                    leadingIcon = Icons.Filled.Refresh,
                    onClick = {
                        viewModel.resetSession()
                        onBackHome()
                    },
                    modifier = Modifier.weight(1.4f)
                )
            }
        }
    }
}

@Composable
private fun ResultStatRow(
    icon: ImageVector,
    label: String,
    value: Int,
    color: Color
) {
    val pct = value.coerceIn(0, 100) / 100f
    Column(modifier = Modifier.padding(bottom = 14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = TextSecondary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextWhite
                    )
                )
                Text(
                    text = "%",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextMuted
                    )
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color.copy(alpha = 0.10f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(pct)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )
        }
    }
}
