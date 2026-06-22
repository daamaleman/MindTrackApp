package ni.edu.uam.mindtrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ni.edu.uam.mindtrack.ui.components.HistoryItemCard
import ni.edu.uam.mindtrack.ui.components.IconCircleButton
import ni.edu.uam.mindtrack.ui.components.SectionLabel
import ni.edu.uam.mindtrack.ui.components.StatTile
import ni.edu.uam.mindtrack.ui.theme.*
import ni.edu.uam.mindtrack.viewmodel.MindTrackViewModel

@Composable
fun HistoryScreen(
    viewModel: MindTrackViewModel,
    onBack: () -> Unit
) {
    val history by viewModel.sessionHistory.collectAsState()

    val totalCount = history.size
    val rationalCount = history.count {
        it.finalResult.contains("racional", true) || it.finalResult.contains("exitoso", true)
    }
    val impulsiveCount = history.count {
        it.finalResult.contains("impulsiv", true) || it.finalResult.contains("crítico", true)
    }

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = innerPadding.calculateBottomPadding() + 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(SurfaceVariant.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = TextWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Historial",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp,
                            letterSpacing = (-0.6).sp,
                            color = TextWhite
                        )
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "Tus sesiones anteriores",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextMuted,
                            fontSize = 12.5.sp
                        )
                    )
                }

                IconCircleButton(icon = Icons.Filled.FilterList, onClick = {})
            }

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatTile(number = totalCount.toString(), label = "Total", modifier = Modifier.weight(1f))
                StatTile(
                    number = rationalCount.toString(),
                    label = "Racional",
                    numberColor = RationalColor,
                    modifier = Modifier.weight(1f)
                )
                StatTile(
                    number = impulsiveCount.toString(),
                    label = "Impulsivo",
                    numberColor = ImpulsiveColor,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(22.dp))

            if (history.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay sesiones registradas",
                        style = MaterialTheme.typography.bodyLarge.copy(color = TextMuted)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        SectionLabel(text = "Esta semana")
                        Spacer(Modifier.height(8.dp))
                    }
                    items(history) { session ->
                        HistoryItemCard(session = session)
                    }
                }
            }
        }
    }
}
