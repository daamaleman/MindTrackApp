package ni.edu.uam.mindtrack.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ni.edu.uam.mindtrack.ui.components.MindTrackButton
import ni.edu.uam.mindtrack.ui.components.ScenarioOptionCard
import ni.edu.uam.mindtrack.ui.theme.PrimaryAccent
import ni.edu.uam.mindtrack.viewmodel.MindTrackViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScenarioScreen(
    viewModel: MindTrackViewModel,
    onBack: () -> Unit,
    onFinish: () -> Unit
) {
    val currentStep by viewModel.currentScenarioIndex.collectAsState()
    val scenario = viewModel.scenarios[currentStep]
    var selectedOptionType by remember { mutableStateOf<String?>(null) }

    // Reset selection when scenario changes
    LaunchedEffect(currentStep) {
        selectedOptionType = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            "Escenario",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    Text(
                        text = "${currentStep + 1} / ${viewModel.scenarios.size}",
                        modifier = Modifier.padding(end = 16.dp),
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = PrimaryAccent,
                            fontWeight = FontWeight.Bold
                        )
                    )
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
                .padding(24.dp)
        ) {
            // Progress Indicator
            LinearProgressIndicator(
                progress = { (currentStep + 1).toFloat() / viewModel.scenarios.size },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = PrimaryAccent,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Category Chip
            Surface(
                color = PrimaryAccent.copy(alpha = 0.14f),
                shape = RoundedCornerShape(100.dp),
                border = BorderStroke(1.dp, PrimaryAccent.copy(alpha = 0.28f))
            ) {
                Text(
                    text = "${scenario.emoji} ${scenario.category}",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = PrimaryAccent,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Question
            Text(
                text = scenario.question,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 32.sp
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Options with AnimatedContent for transitions
            AnimatedContent(
                targetState = scenario,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "scenarioTransition"
            ) { targetScenario ->
                Column {
                    targetScenario.options.forEach { option ->
                        ScenarioOptionCard(
                            option = option,
                            isSelected = selectedOptionType == option.type,
                            onClick = { selectedOptionType = option.type }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Confirm Button
            MindTrackButton(
                text = "Confirmar elección  →",
                enabled = selectedOptionType != null,
                onClick = {
                    selectedOptionType?.let { type ->
                        viewModel.selectOption(type)
                        if (!viewModel.nextScenario()) {
                            viewModel.finishSession()
                            onFinish()
                        }
                    }
                }
            )
        }
    }
}
