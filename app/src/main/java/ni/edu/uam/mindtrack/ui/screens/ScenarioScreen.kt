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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ni.edu.uam.mindtrack.model.Option
import ni.edu.uam.mindtrack.ui.components.*
import ni.edu.uam.mindtrack.ui.theme.*
import ni.edu.uam.mindtrack.viewmodel.MindTrackViewModel

@Composable
fun ScenarioScreen(
    viewModel: MindTrackViewModel,
    onBack: () -> Unit,
    onFinish: () -> Unit
) {
    val scenario = viewModel.getCurrentScenario()
    val playerState by viewModel.playerState.collectAsState()
    val gameFinished by viewModel.gameFinished.collectAsState()
    val decisionPath by viewModel.decisionPath.collectAsState()

    val totalSteps = 3
    val currentStep = (decisionPath.size).coerceIn(0, totalSteps - 1)

    // Estado mutable para la selección actual
    var selectedOption by remember { mutableStateOf<Option?>(null) }

    // Reset selectedOption cuando cambia el escenario ID
    LaunchedEffect(scenario?.id) {
        selectedOption = null
    }

    // Navega a ResultScreen cuando el juego termina
    LaunchedEffect(gameFinished) {
        if (gameFinished) {
            onFinish()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Header
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconCircleButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    onClick = onBack
                )
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = TextMuted)) { append("ESCENARIO ") }
                            withStyle(
                                SpanStyle(
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold
                                )
                            ) { append("${currentStep + 1}") }
                            withStyle(SpanStyle(color = TextMuted)) { append(" DE $totalSteps") }
                        },
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 0.8.sp
                        )
                    )
                }
                IconCircleButton(
                    icon = Icons.Filled.Close,
                    onClick = onBack
                )
            }
            Spacer(Modifier.height(12.dp))
            StepDots(
                total = totalSteps,
                currentIndex = currentStep,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        // Content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 18.dp)
        ) {
            if (scenario != null) {
                Pill(text = scenario.title)
                Spacer(Modifier.height(12.dp))
                Text(
                    text = scenario.question,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        lineHeight = 24.7.sp,
                        letterSpacing = (-0.3).sp,
                        color = TextWhite
                    )
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "SELECCIONA UNA OPCIÓN",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextMuted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.5.sp,
                        letterSpacing = 1.sp
                    )
                )
                Spacer(Modifier.height(10.dp))
                scenario.options.forEach { option ->
                    OptionCard(
                        option = option,
                        isSelected = selectedOption == option,
                        onClick = { selectedOption = option }
                    )
                    Spacer(Modifier.height(8.dp))
                }
                Spacer(Modifier.height(8.dp))
            }
        }

        // Bottom card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
                .padding(top = 12.dp, bottom = 18.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Surface)
                .border(1.dp, BorderColor, RoundedCornerShape(20.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    StatMini(
                        icon = Icons.Filled.Bolt,
                        label = "Energía",
                        value = playerState.energy,
                        color = EnergyColor,
                        modifier = Modifier.weight(1f)
                    )
                    StatMini(
                        icon = Icons.Filled.SelfImprovement,
                        label = "Estrés",
                        value = playerState.stress,
                        color = StressColor,
                        modifier = Modifier.weight(1f)
                    )
                    StatMini(
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        label = "Progreso",
                        value = playerState.progress,
                        color = ProgressColor,
                        modifier = Modifier.weight(1f)
                    )
                    StatMini(
                        icon = Icons.Filled.Payments,
                        label = "Dinero",
                        value = playerState.money,
                        color = MoneyColor,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(12.dp))
                MindTrackPrimaryButton(
                    text = "Confirmar elección",
                    enabled = selectedOption != null,
                    height = 46.dp,
                    onClick = {
                        selectedOption?.let {
                            viewModel.selectOption(it)
                            selectedOption = null
                        }
                    }
                )
            }
        }
    }
}
