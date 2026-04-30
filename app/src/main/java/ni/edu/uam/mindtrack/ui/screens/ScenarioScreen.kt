package ni.edu.uam.mindtrack.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ni.edu.uam.mindtrack.model.Option
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
    val scenario = viewModel.getCurrentScenario()
    val playerState by viewModel.playerState.collectAsState()
    val gameFinished by viewModel.gameFinished.collectAsState()
    val progress = viewModel.getScenarioProgress()
    
    var selectedOption by remember { mutableStateOf<Option?>(null) }

    LaunchedEffect(gameFinished) {
        if (gameFinished) {
            onFinish()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = scenario?.title ?: "Simulación",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .width(100.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = PrimaryAccent,
                            trackColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp,
                shadowElevation = 16.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .navigationBarsPadding()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StateIndicator(label = "Energía", value = playerState.energy, icon = "⚡")
                        StateIndicator(label = "Estrés", value = playerState.stress, icon = "🧘")
                        StateIndicator(label = "Progreso", value = playerState.progress, icon = "📈")
                        StateIndicator(label = "Dinero", value = playerState.money, icon = "💵")
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    MindTrackButton(
                        text = "Confirmar elección",
                        enabled = selectedOption != null,
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
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (scenario != null) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        SuggestionChip(
                            onClick = { },
                            label = { Text(scenario.title) },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = PrimaryAccent.copy(alpha = 0.1f),
                                labelColor = PrimaryAccent
                            ),
                            border = SuggestionChipDefaults.suggestionChipBorder(
                                borderColor = PrimaryAccent.copy(alpha = 0.3f),
                                enabled = true
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = scenario.question,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 32.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    items(scenario.options) { option ->
                        ScenarioOptionCard(
                            option = option,
                            isSelected = selectedOption == option,
                            onClick = { selectedOption = option }
                        )
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun StateIndicator(label: String, value: Int, icon: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = icon, fontSize = 20.sp)
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
        )
    }
}
