package ni.edu.uam.mindtrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import ni.edu.uam.mindtrack.viewmodel.ProfileReport
import android.content.Intent

@Composable
fun QuestionnaireResultScreen(report: ProfileReport?, onBack: () -> Unit) {
    val ctx = LocalContext.current
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text(text = "Informe de personalidad", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

        if (report == null) {
            Text("No hay informe disponible.")
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onBack) { Text("Volver") }
            return@Column
        }

        Text(text = report.mainLabel, style = MaterialTheme.typography.titleMedium)
        Text(text = "Confianza global: ${(report.globalConfidence * 100).toInt()}%", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(12.dp))

        // Simple bar chart por rasgo
        Column(modifier = Modifier.fillMaxWidth()) {
                report.traitScores.forEach { (trait, score) ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(trait, style = MaterialTheme.typography.bodySmall)
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(6.dp))) {
                                    val pct = (score.toFloat() / 100f).coerceIn(0f, 1f)
                                    Box(modifier = Modifier
                                        .fillMaxWidth(pct)
                                        .height(12.dp)
                                        .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(6.dp)))
                        }
                        Text(text = "${score.toInt()} • Conf: ${(report.confidences[trait]?.times(100))?.toInt() ?: 50}%", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        if (report.strengths.isNotEmpty()) {
            Text(text = "Fortalezas:", style = MaterialTheme.typography.labelMedium)
            report.strengths.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (report.growthAreas.isNotEmpty()) {
            Text(text = "Áreas de crecimiento:", style = MaterialTheme.typography.labelMedium)
            report.growthAreas.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
        }
        Spacer(modifier = Modifier.height(8.dp))
        if (report.patterns.isNotEmpty()) {
            Text(text = "Patrones detectados:", style = MaterialTheme.typography.labelMedium)
            report.patterns.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = onBack) { Text("Volver al perfil") }
            Button(onClick = {
                // Compartir como texto simple
                val shareText = buildString {
                    append("Informe MindTrack: ${report.mainLabel}\n")
                    append("Confianza: ${(report.globalConfidence * 100).toInt()}%\n\n")
                    append("Puntajes:\n")
                    report.traitScores.forEach { (t, s) -> append("- $t: ${s.toInt()}\n") }
                    append("\nFortalezas:\n")
                    report.strengths.forEach { append("- $it\n") }
                }
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    type = "text/plain"
                }
                val chooser = Intent.createChooser(intent, "Compartir informe")
                ctx.startActivity(chooser)
            }) { Text("Compartir") }
        }
    }
}




