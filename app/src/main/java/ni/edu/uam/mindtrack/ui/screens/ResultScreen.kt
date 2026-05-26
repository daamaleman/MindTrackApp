package ni.edu.uam.mindtrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ni.edu.uam.mindtrack.ui.components.MindTrackButton
import ni.edu.uam.mindtrack.ui.theme.PrimaryAccent
import ni.edu.uam.mindtrack.viewmodel.MindTrackViewModel

@Composable
fun ResultScreen(
    viewModel: MindTrackViewModel,
    onBackHome: () -> Unit
) {
    val finalResult by viewModel.finalResult.collectAsState()
    val playerState by viewModel.playerState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Box(
            modifier = Modifier
                .size(120.dp)
                .background(PrimaryAccent.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = PrimaryAccent,
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Simulación Completada",
            style = MaterialTheme.typography.labelLarge.copy(color = PrimaryAccent, fontWeight = FontWeight.Bold)
        )

        Text(
            text = finalResult,
            style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        ResultStatRow("Energía", playerState.energy, "⚡", PrimaryAccent)
        ResultStatRow("Estrés", playerState.stress, "🧘", Color(0xFFF87171))
        ResultStatRow("Progreso", playerState.progress, "📈", Color(0xFF34D399))
        ResultStatRow("Dinero", playerState.money, "💵", Color(0xFFFBBF24))

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(48.dp))

        MindTrackButton(
            text = "Jugar de nuevo",
            onClick = {
                viewModel.resetSession()
                onBackHome()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        MindTrackButton(
            text = "Volver al Inicio",
            onClick = onBackHome,
            isSecondary = true
        )
    }
}

@Composable
fun ResultStatRow(label: String, value: Int, icon: String, color: Color) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = icon, modifier = Modifier.padding(end = 8.dp))
                Text(text = label, style = MaterialTheme.typography.bodyMedium)
            }
            Text(
                text = "$value%",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { value / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = color,
            trackColor = color.copy(alpha = 0.1f)
        )
    }
}
