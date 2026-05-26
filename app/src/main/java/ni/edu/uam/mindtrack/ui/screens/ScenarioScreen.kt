package ni.edu.uam.mindtrack.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ni.edu.uam.mindtrack.model.Option
import ni.edu.uam.mindtrack.ui.components.MindTrackButton
import ni.edu.uam.mindtrack.ui.theme.PrimaryAccent
import ni.edu.uam.mindtrack.viewmodel.MindTrackViewModel

/* -------------------------------------------------------------------------- */
/*  PALETA SEMÁNTICA                                                          */
/* -------------------------------------------------------------------------- */

private val EnergyColor = Color(0xFFFFC107)   // amarillo
private val StressColor = Color(0xFFFF7043)   // naranja
private val ProgressColor = Color(0xFF42A5F5) // azul
private val MoneyColor = Color(0xFF66BB6A)    // verde

private val PositiveColor = Color(0xFF4ADE80)
private val NegativeColor = Color(0xFFF87171)
private val NeutralColor = Color(0xFF94A3B8)

/* -------------------------------------------------------------------------- */
/*  BREAKPOINTS RESPONSIVE                                                    */
/* -------------------------------------------------------------------------- */

private data class ResponsiveSpec(
    val horizontalPadding: Dp,
    val contentMaxWidth: Dp,
    val titleFontSize: Int,           // sp
    val cardPadding: Dp,
    val sectionSpacing: Dp,
    val isCompact: Boolean
)

private fun specFor(widthDp: Dp): ResponsiveSpec = when {
    widthDp < 360.dp -> ResponsiveSpec(16.dp, 600.dp, 20, 14.dp, 12.dp, true)
    widthDp < 600.dp -> ResponsiveSpec(20.dp, 600.dp, 24, 18.dp, 16.dp, true)
    widthDp < 840.dp -> ResponsiveSpec(32.dp, 640.dp, 28, 20.dp, 20.dp, false)
    else             -> ResponsiveSpec(48.dp, 720.dp, 30, 22.dp, 24.dp, false)
}

/* -------------------------------------------------------------------------- */
/*  PANTALLA PRINCIPAL                                                        */
/* -------------------------------------------------------------------------- */

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
        if (gameFinished) onFinish()
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        val spec = specFor(maxWidth)

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                ScenarioTopBar(
                    title = scenario?.title ?: "Simulación",
                    progress = progress,
                    onBack = onBack
                )
            },
            bottomBar = {
                ScenarioBottomBar(
                    playerState = playerState,
                    enabled = selectedOption != null,
                    spec = spec,
                    onConfirm = {
                        selectedOption?.let {
                            viewModel.selectOption(it)
                            selectedOption = null
                        }
                    }
                )
            }
        ) { paddingValues ->
            ScenarioContent(
                scenario = scenario,
                selectedOption = selectedOption,
                onOptionSelected = { selectedOption = it },
                spec = spec,
                paddingValues = paddingValues
            )
        }
    }
}

/* -------------------------------------------------------------------------- */
/*  TOP BAR                                                                   */
/* -------------------------------------------------------------------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScenarioTopBar(
    title: String,
    progress: Float,
    onBack: () -> Unit
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 600),
        label = "progress"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent
            ),
            windowInsets = WindowInsets(0, 0, 0, 0)
        )

        // Progress bar visual estilo "stepper"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = PrimaryAccent,
                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                gapSize = 0.dp,
                drawStopIndicator = {}
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = PrimaryAccent
            )
        }
        Spacer(Modifier.height(4.dp))
    }
}

/* -------------------------------------------------------------------------- */
/*  CONTENIDO CENTRAL                                                         */
/* -------------------------------------------------------------------------- */

@Composable
private fun ScenarioContent(
    scenario: ni.edu.uam.mindtrack.model.Scenario?,
    selectedOption: Option?,
    onOptionSelected: (Option) -> Unit,
    spec: ResponsiveSpec,
    paddingValues: PaddingValues
) {
    Box(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        if (scenario == null) return@Box

        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = spec.contentMaxWidth)
                .padding(horizontal = spec.horizontalPadding),
            verticalArrangement = Arrangement.spacedBy(spec.sectionSpacing)
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                CategoryBadge(label = scenario.title)
                Spacer(Modifier.height(spec.sectionSpacing))
                Text(
                    text = scenario.question,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = spec.titleFontSize.sp,
                        lineHeight = (spec.titleFontSize + 8).sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(spec.sectionSpacing / 2))
                Text(
                    text = "Selecciona una opción",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(scenario.options, key = { it.hashCode() }) { option ->
                OptionCard(
                    option = option,
                    isSelected = selectedOption == option,
                    cardPadding = spec.cardPadding,
                    onClick = { onOptionSelected(option) }
                )
            }

            item {
                // Espaciado para que la última card no quede tapada por la bottom bar
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
/*  CATEGORY BADGE                                                            */
/* -------------------------------------------------------------------------- */

@Composable
private fun CategoryBadge(label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(PrimaryAccent.copy(alpha = 0.12f))
            .border(
                width = 1.dp,
                color = PrimaryAccent.copy(alpha = 0.35f),
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Box(
            Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(PrimaryAccent)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            ),
            color = PrimaryAccent
        )
    }
}

/* -------------------------------------------------------------------------- */
/*  TARJETA DE OPCIÓN                                                         */
/* -------------------------------------------------------------------------- */

@Composable
private fun OptionCard(
    option: Option,
    isSelected: Boolean,
    cardPadding: Dp,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) PrimaryAccent else Color.Transparent,
        animationSpec = tween(220),
        label = "border"
    )
    val containerColor by animateColorAsState(
        targetValue = if (isSelected)
            PrimaryAccent.copy(alpha = 0.10f)
        else
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        animationSpec = tween(220),
        label = "container"
    )
    val elevation by animateDpAsState(
        targetValue = if (isSelected) 2.dp else 0.dp,
        animationSpec = tween(220),
        label = "elev"
    )

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
        tonalElevation = elevation,
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected)
                borderColor
            else
                MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(cardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Radio personalizado
            SelectionIndicator(isSelected = isSelected)

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.text,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        lineHeight = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(12.dp))
                EffectsRow(option = option)
            }
        }
    }
}

@Composable
private fun SelectionIndicator(isSelected: Boolean) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) PrimaryAccent
        else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
        animationSpec = tween(200),
        label = "selBorder"
    )
    val fill by animateColorAsState(
        targetValue = if (isSelected) PrimaryAccent else Color.Transparent,
        animationSpec = tween(200),
        label = "selFill"
    )

    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .border(2.dp, borderColor, CircleShape)
            .background(fill),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isSelected,
            enter = fadeIn(tween(150)),
            exit = fadeOut(tween(150))
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

/* -------------------------------------------------------------------------- */
/*  CHIP DE EFECTOS                                                           */
/* -------------------------------------------------------------------------- */

@Composable
private fun EffectsRow(option: Option) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        EffectChip(icon = Icons.Default.Bolt, value = option.energyEffect, color = EnergyColor)
        EffectChip(icon = Icons.Default.SelfImprovement, value = option.stressEffect, color = StressColor, inverted = true)
        EffectChip(icon = Icons.AutoMirrored.Filled.TrendingUp, value = option.progressEffect, color = ProgressColor)
        EffectChip(icon = Icons.Default.AttachMoney, value = option.moneyEffect, color = MoneyColor)
    }
}

/**
 * @param inverted Para Estrés: subir es malo, bajar es bueno. Invertimos la semántica de color.
 */
@Composable
private fun EffectChip(
    icon: ImageVector,
    value: Int,
    color: Color,
    inverted: Boolean = false
) {
    val effectiveValue = if (inverted) -value else value
    val tone = when {
        effectiveValue > 0 -> PositiveColor
        effectiveValue < 0 -> NegativeColor
        else -> NeutralColor
    }
    val displayText = when {
        value > 0 -> "+$value"
        else -> "$value"
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.10f))
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.20f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = displayText,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            ),
            color = tone
        )
    }
}

/* -------------------------------------------------------------------------- */
/*  BOTTOM BAR                                                                */
/* -------------------------------------------------------------------------- */

@Composable
private fun ScenarioBottomBar(
    playerState: ni.edu.uam.mindtrack.model.PlayerState,
    enabled: Boolean,
    spec: ResponsiveSpec,
    onConfirm: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        shadowElevation = 24.dp,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = spec.contentMaxWidth)
                    .fillMaxWidth()
                    .padding(
                        horizontal = spec.horizontalPadding,
                        vertical = if (spec.isCompact) 16.dp else 20.dp
                    )
                    .navigationBarsPadding()
            ) {
                // "Pull handle" decorativo
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(36.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                )
                Spacer(Modifier.height(if (spec.isCompact) 14.dp else 18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    StatPill(
                        label = "Energía",
                        value = playerState.energy,
                        icon = Icons.Default.Bolt,
                        color = EnergyColor,
                        compact = spec.isCompact
                    )
                    StatPill(
                        label = "Estrés",
                        value = playerState.stress,
                        icon = Icons.Default.SelfImprovement,
                        color = StressColor,
                        compact = spec.isCompact
                    )
                    StatPill(
                        label = "Progreso",
                        value = playerState.progress,
                        icon = Icons.AutoMirrored.Filled.TrendingUp,
                        color = ProgressColor,
                        compact = spec.isCompact
                    )
                    StatPill(
                        label = "Dinero",
                        value = playerState.money,
                        icon = Icons.Default.AttachMoney,
                        color = MoneyColor,
                        compact = spec.isCompact,
                        showAsMoney = true
                    )
                }

                Spacer(Modifier.height(if (spec.isCompact) 18.dp else 22.dp))

                MindTrackButton(
                    text = if (enabled) "Confirmar elección" else "Selecciona una opción",
                    enabled = enabled,
                    onClick = onConfirm
                )
            }
        }
    }
}

/* -------------------------------------------------------------------------- */
/*  STAT PILL                                                                 */
/* -------------------------------------------------------------------------- */

@Composable
private fun StatPill(
    label: String,
    value: Int,
    icon: ImageVector,
    color: Color,
    compact: Boolean,
    showAsMoney: Boolean = false
) {
    val animatedValue by animateFloatAsState(
        targetValue = value.toFloat(),
        animationSpec = tween(500),
        label = "statValue"
    )

    val percentage by remember(value) {
        derivedStateOf { (value / 100f).coerceIn(0f, 1f) }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.widthIn(min = if (compact) 56.dp else 68.dp)
    ) {
        Box(
            modifier = Modifier
                .size(if (compact) 36.dp else 40.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(if (compact) 20.dp else 22.dp)
            )
        }

        Spacer(Modifier.height(6.dp))

        Text(
            text = if (showAsMoney) "$${animatedValue.toInt()}" else animatedValue.toInt().toString(),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = if (compact) 15.sp else 17.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = if (compact) 10.sp else 11.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(4.dp))

        // Mini progress bar visual
        Box(
            modifier = Modifier
                .width(if (compact) 36.dp else 44.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color.copy(alpha = 0.15f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(percentage)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
        }
    }
}