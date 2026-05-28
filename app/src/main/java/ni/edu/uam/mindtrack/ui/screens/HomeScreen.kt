package ni.edu.uam.mindtrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import ni.edu.uam.mindtrack.ui.components.IconCircleButton
import ni.edu.uam.mindtrack.ui.components.MindTrackPrimaryButton
import ni.edu.uam.mindtrack.ui.components.Pill
import ni.edu.uam.mindtrack.ui.components.SectionLabel
import ni.edu.uam.mindtrack.ui.components.StatTile
import ni.edu.uam.mindtrack.ui.theme.*
import ni.edu.uam.mindtrack.viewmodel.MindTrackViewModel

@Composable
fun HomeScreen(
    viewModel: MindTrackViewModel,
    onStartSimulation: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .background(
                Brush.radialGradient(
                    colors = listOf(PrimaryAccent.copy(alpha = 0.18f), Color.Transparent),
                    radius = 700f
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = 100.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hola,",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextMuted,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "${userProfile.name} 👋",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextWhite
                        )
                    )
                }
                IconCircleButton(
                    icon = Icons.Outlined.Notifications,
                    onClick = {}
                )
            }

            Spacer(Modifier.height(22.dp))

            // Hero card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Surface)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                PrimaryAccent.copy(alpha = 0.18f),
                                PrimaryAccent.copy(alpha = 0.04f),
                                Color.Transparent
                            )
                        )
                    )
                    .border(1.dp, PrimaryEdge, RoundedCornerShape(24.dp))
                    .padding(22.dp)
            ) {
                Column {
                    Pill(text = "NUEVA SESIÓN")
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "Descubre tu perfil de\ntoma de decisiones",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            lineHeight = 28.sp,
                            letterSpacing = (-0.6).sp,
                            color = TextWhite
                        )
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "3 escenarios · ~3 minutos",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    )
                    Spacer(Modifier.height(18.dp))
                    MindTrackPrimaryButton(
                        text = "Iniciar simulación",
                        leadingIcon = Icons.Filled.PlayArrow,
                        onClick = onStartSimulation,
                        height = 48.dp
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Stat tiles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatTile(number = "3", label = "Escenarios", modifier = Modifier.weight(1f))
                StatTile(number = "~3'", label = "Duración", modifier = Modifier.weight(1f))
                StatTile(number = "3", label = "Perfiles", modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionLabel(text = "Última sesión")
                Text(
                    text = "VER TODO",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SecondaryAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.6.sp
                    )
                )
            }

            Spacer(Modifier.height(10.dp))

            // Last history card (mocked)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Surface)
                    .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(RationalColor.copy(alpha = 0.12f))
                        .border(1.dp, RationalColor.copy(alpha = 0.30f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Psychology,
                        contentDescription = null,
                        tint = RationalColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pensador Racional",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextWhite
                        )
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "Hace 2 días · 3 decisiones",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextMuted,
                            fontSize = 11.5.sp
                        )
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}