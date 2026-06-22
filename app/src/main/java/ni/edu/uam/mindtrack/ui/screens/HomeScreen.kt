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
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ni.edu.uam.mindtrack.R
import ni.edu.uam.mindtrack.model.SessionResult
import ni.edu.uam.mindtrack.ui.components.*
import ni.edu.uam.mindtrack.ui.theme.*
import ni.edu.uam.mindtrack.viewmodel.MindTrackViewModel

@Composable
fun HomeScreen(
    viewModel: MindTrackViewModel,
    onStartSimulation: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val apiError by viewModel.apiConnectionError.collectAsState()
    val sessionHistory by viewModel.sessionHistory.collectAsState()

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
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
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp, bottom = innerPadding.calculateBottomPadding() + 24.dp)
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
                                text = stringResource(R.string.api_error_message),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = ImpulsiveColor,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.home_greeting),
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
                        Pill(text = stringResource(R.string.new_session_pill))
                        Spacer(Modifier.height(14.dp))
                        Text(
                            text = stringResource(R.string.hero_title),
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
                            text = stringResource(R.string.hero_subtitle),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                        )
                        Spacer(Modifier.height(18.dp))
                        MindTrackPrimaryButton(
                            text = stringResource(R.string.start_simulation_button),
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
                    StatTile(number = "3", label = stringResource(R.string.stat_scenarios_label), modifier = Modifier.weight(1f))
                    StatTile(number = "~3'", label = stringResource(R.string.stat_duration_label), modifier = Modifier.weight(1f))
                    StatTile(number = "3", label = stringResource(R.string.stat_profiles_label), modifier = Modifier.weight(1f))
                }

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionLabel(text = stringResource(R.string.last_session_label))
                    Text(
                        text = stringResource(R.string.view_all_button),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SecondaryAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 0.6.sp
                        )
                    )
                }

                Spacer(Modifier.height(10.dp))

                // Last history card (Room Data)
                val lastSession = sessionHistory.firstOrNull()
                if (lastSession != null) {
                    SessionHistoryCard(
                        session = lastSession,
                        onDelete = { viewModel.deleteSession(it) }
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay sesiones registradas",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SessionHistoryCard(
    session: SessionResult,
    onDelete: (SessionResult) -> Unit
) {
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
                text = session.finalResult,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextWhite
                )
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "${session.date} · ${session.choicesMade} decisiones",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextMuted,
                    fontSize = 11.5.sp
                )
            )
        }
        IconButton(onClick = { onDelete(session) }) {
            Icon(
                imageVector = Icons.Filled.DeleteOutline,
                contentDescription = "Borrar",
                tint = ImpulsiveColor.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
