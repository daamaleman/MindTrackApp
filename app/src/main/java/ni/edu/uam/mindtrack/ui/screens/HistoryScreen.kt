package ni.edu.uam.mindtrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ni.edu.uam.mindtrack.ui.components.HistoryItemCard
import ni.edu.uam.mindtrack.ui.theme.*
import ni.edu.uam.mindtrack.viewmodel.MindTrackViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: MindTrackViewModel,
    onBack: () -> Unit
) {
    val history by viewModel.sessionHistory.collectAsState()
    
    val rationalCount = history.count { it.profile == "Racional" }
    val balancedCount = history.count { it.profile == "Equilibrado" }
    val impulsiveCount = history.count { it.profile == "Impulsivo" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            "Historial",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                            modifier = Modifier.size(18.dp),
                            tint = TextMuted
                        )
                    }
                },
                actions = {
                    Box(modifier = Modifier.size(48.dp)) // Spacer to balance center title
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            // Summary Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryMiniCard("Racional", rationalCount, RationalColor, Modifier.weight(1f))
                SummaryMiniCard("Equilibrado", balancedCount, BalancedColor, Modifier.weight(1f))
                SummaryMiniCard("Impulsivo", impulsiveCount, ImpulsiveColor, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sesiones recientes",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "${history.size} entradas",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = PrimaryAccent,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (history.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "📭", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Aún no hay sesiones",
                            style = MaterialTheme.typography.bodyLarge.copy(color = TextFaint)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(history) { session ->
                        HistoryItemCard(session = session)
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryMiniCard(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(color.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
            .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleLarge.copy(
                color = color,
                fontWeight = FontWeight.ExtraBold
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(color = TextFaint)
        )
    }
}
