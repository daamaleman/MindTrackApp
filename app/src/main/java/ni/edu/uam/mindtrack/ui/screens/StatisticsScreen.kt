package ni.edu.uam.mindtrack.ui.screens

import android.graphics.Bitmap
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import ni.edu.uam.mindtrack.model.PlayerState
import ni.edu.uam.mindtrack.model.SessionResult
import ni.edu.uam.mindtrack.ui.components.MindTrackButton
import ni.edu.uam.mindtrack.ui.theme.BalancedColor
import ni.edu.uam.mindtrack.ui.theme.ImpulsiveColor
import ni.edu.uam.mindtrack.ui.theme.PrimaryAccent
import ni.edu.uam.mindtrack.ui.theme.RationalColor
import ni.edu.uam.mindtrack.ui.theme.SecondaryAccent
import ni.edu.uam.mindtrack.viewmodel.MindTrackViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

private val StatisticsPeriods = listOf("Semana", "Mes", "Año", "Todo")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: MindTrackViewModel,
    onStartSimulation: () -> Unit,
    onOpenHistory: () -> Unit
) {
    val history by viewModel.sessionHistory.collectAsState()
    val currentStreak by viewModel.currentStreak.collectAsState()
    val bestStreak by viewModel.bestStreak.collectAsState()
    var selectedPeriod by rememberSaveable { mutableStateOf("Mes") }

    if (history.isEmpty()) {
        Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Aún no hay datos para mostrar",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    MindTrackButton(
                        text = "Iniciar simulación",
                        onClick = {
                            viewModel.resetSession()
                            onStartSimulation()
                        }
                    )
                }
            }
        }
        return
    }

    val currentSessions = remember(selectedPeriod, history) {
        viewModel.filterSessionsByPeriod(selectedPeriod).sortedBy { parseSessionMillis(it.date) }
    }

    val metrics = remember(selectedPeriod, history) {
        buildStatisticsMetrics(
            history = history,
            selectedPeriod = selectedPeriod,
            currentSessions = currentSessions
        )
    }

    val view = LocalView.current
    val ctx = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Estadísticas",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onOpenHistory) {
                        Icon(
                            imageVector = Icons.Filled.History,
                            contentDescription = "Ver historial",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = {
                            runCatching {
                                val bitmap = view.drawToBitmap()
                                val file = File(ctx.cacheDir, "stats_${System.currentTimeMillis()}.png")
                                FileOutputStream(file).use { output ->
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
                                }
                                val uri: Uri = FileProvider.getUriForFile(
                                    ctx,
                                    "${ctx.packageName}.fileprovider",
                                    file
                                )
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "image/png"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                ctx.startActivity(Intent.createChooser(intent, "Compartir estadísticas"))
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Compartir",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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
            PeriodSelector(
                selectedPeriod = selectedPeriod,
                onPeriodSelected = { selectedPeriod = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            HeroMetricCard(
                totalDecisions = metrics.totalDecisions,
                deltaLabel = metrics.deltaLabel,
                deltaColor = metrics.deltaColor,
                sessionsCount = metrics.sessionsCount,
                sparklineValues = metrics.sparklineValues
            )

            Spacer(modifier = Modifier.height(16.dp))

            DistributionCard(metrics.profileShares)

            Spacer(modifier = Modifier.height(16.dp))

            AverageStateCard(metrics.averageState)

            Spacer(modifier = Modifier.height(16.dp))

            StreakCard(
                currentStreak = currentStreak,
                bestStreak = bestStreak
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PeriodSelector(
    selectedPeriod: String,
    onPeriodSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
            .padding(4.dp)
    ) {
        StatisticsPeriods.forEach { period ->
            val isSelected = period == selectedPeriod
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 2.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) PrimaryAccent else Color.Transparent)
                    .clickable { onPeriodSelected(period) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = period,
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
private fun HeroMetricCard(
    totalDecisions: Int,
    deltaLabel: String,
    deltaColor: Color,
    sessionsCount: Int,
    sparklineValues: List<SparklineValue>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(18.dp)
    ) {
        Text(
            text = "DECISIONES TOMADAS",
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = totalDecisions.toString(),
                style = MaterialTheme.typography.displaySmall.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.ExtraBold
                )
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = deltaLabel,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = deltaColor,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "vs. período anterior · $sessionsCount sesiones completadas",
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        Spacer(modifier = Modifier.height(18.dp))
        SparklineChart(values = sparklineValues)
    }
}

@Composable
private fun SparklineChart(values: List<SparklineValue>) {
    val inactiveColor = SecondaryAccent.copy(alpha = 0.55f)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(84.dp)
    ) {
        val barSpacing = size.width / values.size.coerceAtLeast(1)
        val maxValue = maxOf(values.maxOfOrNull { it.value } ?: 0, 1)
        val barWidth = barSpacing * 0.72f
        values.forEachIndexed { index, item ->
            val barHeight = (item.value.toFloat() / maxValue.toFloat()) * size.height * 0.9f
            val left = (index * barSpacing) + (barSpacing - barWidth) / 2f
            val top = size.height - barHeight
            val brush = if (item.isLatest) {
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFD9D1FF),
                        PrimaryAccent,
                        Color(0xFF6B5CE7)
                    )
                )
            } else {
                Brush.verticalGradient(
                    colors = listOf(
                        inactiveColor.copy(alpha = 0.55f),
                        inactiveColor.copy(alpha = 0.95f)
                    )
                )
            }

            if (item.isLatest) {
                drawRoundRect(
                    color = PrimaryAccent.copy(alpha = 0.22f),
                    topLeft = androidx.compose.ui.geometry.Offset(left - 2f, top - 2f),
                    size = androidx.compose.ui.geometry.Size(barWidth + 4f, barHeight + 8f),
                    cornerRadius = CornerRadius(12f, 12f)
                )
            }

            drawRoundRect(
                brush = brush,
                topLeft = androidx.compose.ui.geometry.Offset(left, top),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                cornerRadius = CornerRadius(10f, 10f)
            )
        }
    }
}

@Composable
private fun DistributionCard(shares: List<ProfileShare>) {
    val dominant = shares.maxByOrNull { it.count }
    val total = shares.sumOf { it.count }.coerceAtLeast(1)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f), RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Text(
            text = "DISTRIBUCIÓN DE PERFIL",
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        )
        Spacer(modifier = Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(124.dp)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.45f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                ProfileDonutChart(shares)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (dominant != null && dominant.count > 0) {
                            "${((dominant.count.toFloat() / total.toFloat()) * 100f).roundToInt()}%"
                        } else {
                            "0%"
                        },
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                    Text(
                        text = "dominante",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                shares.forEach { share ->
                    ProfileLegendRow(share = share, total = total)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun ProfileDonutChart(shares: List<ProfileShare>) {
    val outlineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    Canvas(modifier = Modifier.size(124.dp)) {
        val stroke = 16.dp.toPx()
        var startAngle = -90f
        val total = shares.sumOf { it.count }.coerceAtLeast(1)
        if (shares.all { it.count == 0 }) {
            drawCircle(
                color = outlineColor,
                style = Stroke(width = stroke)
            )
            return@Canvas
        }

        shares.forEach { share ->
            val sweep = (share.count.toFloat() / total.toFloat()) * 360f
            drawArc(
                color = share.color,
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(width = stroke)
            )
            startAngle += sweep
        }
    }
}

@Composable
private fun ProfileLegendRow(share: ProfileShare, total: Int) {
    val percentage = if (total == 0) 0 else (share.count.toFloat() / total.toFloat() * 100f).roundToInt()
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(share.color, CircleShape)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = share.label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                text = "${share.count} sesiones",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
        Text(
            text = "$percentage%",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = share.color,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
private fun AverageStateCard(averageState: PlayerState?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f), RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Text(
            text = "PROMEDIO DE ESTADO FINAL",
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        StateAverageBar(
            label = "Energía",
            value = averageState?.energy ?: 0,
            color = BalancedColor
        )
        Spacer(modifier = Modifier.height(8.dp))
        StateAverageBar(
            label = "Estrés",
            value = averageState?.stress ?: 0,
            color = ImpulsiveColor
        )
        Spacer(modifier = Modifier.height(8.dp))
        StateAverageBar(
            label = "Progreso",
            value = averageState?.progress ?: 0,
            color = RationalColor
        )
        Spacer(modifier = Modifier.height(8.dp))
        StateAverageBar(
            label = "Dinero",
            value = averageState?.money ?: 0,
            color = PrimaryAccent
        )
    }
}

@Composable
private fun StateAverageBar(label: String, value: Int, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Text(
                text = "$value/100",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = color,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f), RoundedCornerShape(50))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(value.coerceIn(0, 100) / 100f)
                    .height(6.dp)
                    .background(color, RoundedCornerShape(50))
            )
        }
    }
}

@Composable
private fun StreakCard(currentStreak: Int, bestStreak: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f), RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Text(
            text = "RACHA",
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            StreakChip(label = "Actual", value = currentStreak, color = PrimaryAccent)
            StreakChip(label = "Mejor", value = bestStreak, color = BalancedColor)
        }
    }
}

@Composable
private fun RowScope.StreakChip(label: String, value: Int, color: Color) {
    Column(
        modifier = Modifier
            .weight(1f)
            .padding(end = 8.dp)
            .background(color.copy(alpha = 0.10f), RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$value días",
            style = MaterialTheme.typography.headlineSmall.copy(
                color = color,
                fontWeight = FontWeight.ExtraBold
            )
        )
    }
}

private data class ProfileShare(
    val label: String,
    val count: Int,
    val color: Color
)

private data class SparklineValue(
    val value: Int,
    val isLatest: Boolean
)

private data class StatisticsMetrics(
    val sessionsCount: Int,
    val totalDecisions: Int,
    val deltaLabel: String,
    val deltaColor: Color,
    val sparklineValues: List<SparklineValue>,
    val profileShares: List<ProfileShare>,
    val averageState: PlayerState?
)

private fun buildStatisticsMetrics(
    history: List<SessionResult>,
    selectedPeriod: String,
    currentSessions: List<SessionResult>
): StatisticsMetrics {
    val previousSessions = history.filterByPreviousPeriod(selectedPeriod).sortedBy { parseSessionMillis(it.date) }

    val sessionsCount = currentSessions.size
    val totalDecisions = currentSessions.sumOf { it.choicesMade }
    val previousTotalDecisions = previousSessions.sumOf { it.choicesMade }

    val deltaLabel = when {
        previousTotalDecisions == 0 -> "Nuevo"
        else -> {
            val delta = ((totalDecisions - previousTotalDecisions).toFloat() / previousTotalDecisions.toFloat()) * 100f
            val rounded = delta.roundToInt()
            if (rounded >= 0) "+$rounded%" else "$rounded%"
        }
    }
    val deltaColor = if (previousTotalDecisions == 0 || totalDecisions >= previousTotalDecisions) RationalColor else ImpulsiveColor

    val values = currentSessions.map { it.choicesMade }
    val paddedValues = if (values.size < 14) List(14 - values.size) { 0 } + values else values
    val latestIndex = if (values.isNotEmpty()) paddedValues.lastIndex else -1
    val sparklineValues = paddedValues.mapIndexed { index, value ->
        SparklineValue(
            value = value,
            isLatest = index == latestIndex
        )
    }

    val rationalCount = currentSessions.count { deriveProfile(it) == "Racional" }
    val balancedCount = currentSessions.count { deriveProfile(it) == "Equilibrado" }
    val impulsiveCount = currentSessions.count { deriveProfile(it) == "Impulsivo" }

    val profileShares = listOf(
        ProfileShare("Racional", rationalCount, RationalColor),
        ProfileShare("Equilibrado", balancedCount, BalancedColor),
        ProfileShare("Impulsivo", impulsiveCount, ImpulsiveColor)
    )

    val averageState = if (currentSessions.isNotEmpty()) {
        val total = currentSessions.size
        PlayerState(
            energy = currentSessions.sumOf { it.finalState.energy } / total,
            stress = currentSessions.sumOf { it.finalState.stress } / total,
            progress = currentSessions.sumOf { it.finalState.progress } / total,
            money = currentSessions.sumOf { it.finalState.money } / total
        )
    } else {
        null
    }

    return StatisticsMetrics(
        sessionsCount = sessionsCount,
        totalDecisions = totalDecisions,
        deltaLabel = deltaLabel,
        deltaColor = deltaColor,
        sparklineValues = sparklineValues,
        profileShares = profileShares,
        averageState = averageState
    )
}

private fun deriveProfile(session: SessionResult): String {
    return when {
        session.finalResult.contains("Burnout", ignoreCase = true) -> "Impulsivo"
        session.finalResult.contains("Éxito", ignoreCase = true) -> "Racional"
        session.finalState.stress >= 70 || session.finalState.energy <= 30 -> "Impulsivo"
        session.finalState.progress >= 70 && session.finalState.stress <= 45 -> "Racional"
        else -> "Equilibrado"
    }
}

private fun List<SessionResult>.filterByPreviousPeriod(period: String): List<SessionResult> {
    if (period == "Todo") return emptyList()

    val now = System.currentTimeMillis()
    val size = when (period) {
        "Semana" -> 7L * 24 * 60 * 60 * 1000
        "Mes" -> 30L * 24 * 60 * 60 * 1000
        "Año" -> 365L * 24 * 60 * 60 * 1000
        else -> 0L
    }
    val start = now - (size * 2)
    val end = now - size
    return filter {
        val time = parseSessionMillis(it.date)
        time in start until end
    }
}

private fun parseSessionMillis(date: String): Long {
    val format = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return runCatching { format.parse(date)?.time ?: 0L }.getOrDefault(0L)
}






