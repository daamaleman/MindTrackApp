package ni.edu.uam.mindtrack.ui.screens

import android.content.Intent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import ni.edu.uam.mindtrack.model.Achievement
import ni.edu.uam.mindtrack.model.AchievementCategory
import ni.edu.uam.mindtrack.ui.components.MindTrackButton
import ni.edu.uam.mindtrack.ui.theme.BalancedColor
import ni.edu.uam.mindtrack.ui.theme.ImpulsiveColor
import ni.edu.uam.mindtrack.ui.theme.PrimaryAccent
import ni.edu.uam.mindtrack.ui.theme.RationalColor
import ni.edu.uam.mindtrack.ui.theme.SecondaryAccent
import ni.edu.uam.mindtrack.viewmodel.MindTrackViewModel
import kotlin.random.Random

fun computeLevel(unlocked: Int): Pair<Int, String> {
    val level = (unlocked / 4).coerceIn(1, 5)
    val name = when (level) {
        1 -> "Iniciado"
        2 -> "Aprendiz"
        3 -> "Estratega"
        4 -> "Maestro"
        else -> "Visionario"
    }
    return level to name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    viewModel: MindTrackViewModel,
    onBack: () -> Unit
) {
    val achievements by viewModel.achievements.collectAsState()
    val newlyUnlocked by viewModel.newlyUnlocked.collectAsState()
    val unlockedCount = achievements.count { it.unlocked }
    val (level, levelName) = computeLevel(unlockedCount)
    val totalAchievements = achievements.size.coerceAtLeast(1)
    var selectedAchievement by remember { mutableStateOf<Achievement?>(null) }
    var showConfetti by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(newlyUnlocked) {
        if (newlyUnlocked.isNotEmpty()) {
            showConfetti = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Logros",
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
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    actions = {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = PrimaryAccent.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                PrimaryAccent.copy(alpha = 0.45f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = PrimaryAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "$unlockedCount / $totalAchievements",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
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
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
            ) {
                HeroLevelCard(
                    unlockedCount = unlockedCount,
                    totalAchievements = totalAchievements,
                    level = level,
                    levelName = levelName
                )

                Spacer(modifier = Modifier.height(18.dp))

                SectionHeader(title = "DESBLOQUEADOS RECIENTES")
                Spacer(modifier = Modifier.height(10.dp))
                val recentAchievements = remember(achievements, newlyUnlocked) {
                    achievements.filter { it.id in newlyUnlocked }
                }
                if (recentAchievements.isEmpty()) {
                    EmptyRecentCard()
                } else {
                    AchievementRowGrid(
                        achievements = recentAchievements,
                        newlyUnlocked = newlyUnlocked,
                        onAchievementClick = { selectedAchievement = it },
                        onConsumeNewlyUnlocked = viewModel::consumeNewlyUnlocked
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                AchievementCategorySection(
                    title = AchievementCategory.CONSTANCIA.title,
                    achievements = achievements.filter { it.category == AchievementCategory.CONSTANCIA },
                    newlyUnlocked = newlyUnlocked,
                    onAchievementClick = { selectedAchievement = it },
                    onConsumeNewlyUnlocked = viewModel::consumeNewlyUnlocked
                )

                Spacer(modifier = Modifier.height(18.dp))

                AchievementCategorySection(
                    title = AchievementCategory.MAESTRIA.title,
                    achievements = achievements.filter { it.category == AchievementCategory.MAESTRIA },
                    newlyUnlocked = newlyUnlocked,
                    onAchievementClick = { selectedAchievement = it },
                    onConsumeNewlyUnlocked = viewModel::consumeNewlyUnlocked
                )

                Spacer(modifier = Modifier.height(18.dp))

                AchievementCategorySection(
                    title = AchievementCategory.EXPLORACION.title,
                    achievements = achievements.filter { it.category == AchievementCategory.EXPLORACION },
                    newlyUnlocked = newlyUnlocked,
                    onAchievementClick = { selectedAchievement = it },
                    onConsumeNewlyUnlocked = viewModel::consumeNewlyUnlocked
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        if (showConfetti) {
            ConfettiOverlay(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(10f),
                onFinished = { showConfetti = false }
            )
        }

        selectedAchievement?.let { achievement ->
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { selectedAchievement = null },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                AchievementSheetContent(
                    achievement = achievement,
                    onShare = { shareAchievement(context, achievement) },
                    onClose = { selectedAchievement = null }
                )
            }
        }
    }
}

@Composable
private fun HeroLevelCard(
    unlockedCount: Int,
    totalAchievements: Int,
    level: Int,
    levelName: String
) {
    val progress = unlockedCount.toFloat() / totalAchievements.toFloat()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.82f),
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                    )
                ),
                shape = RoundedCornerShape(22.dp)
            )
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(PrimaryAccent.copy(alpha = 0.18f), RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = PrimaryAccent,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "NIVEL $level · $levelName",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
                Text(
                    text = "$unlockedCount de $totalAchievements logros",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(50)),
            color = PrimaryAccent,
            trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    )
}

@Composable
private fun EmptyRecentCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f), RoundedCornerShape(18.dp))
            .padding(18.dp)
    ) {
        Text(
            text = "Aún no hay logros recientes.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Sigue completando simulaciones para desbloquear más.",
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

@Composable
private fun AchievementCategorySection(
    title: String,
    achievements: List<Achievement>,
    newlyUnlocked: Set<String>,
    onAchievementClick: (Achievement) -> Unit,
    onConsumeNewlyUnlocked: (String) -> Unit
) {
    SectionHeader(title = title.uppercase())
    Spacer(modifier = Modifier.height(10.dp))
    AchievementRowGrid(
        achievements = achievements,
        newlyUnlocked = newlyUnlocked,
        onAchievementClick = onAchievementClick,
        onConsumeNewlyUnlocked = onConsumeNewlyUnlocked
    )
}

@Composable
private fun AchievementRowGrid(
    achievements: List<Achievement>,
    newlyUnlocked: Set<String>,
    onAchievementClick: (Achievement) -> Unit,
    onConsumeNewlyUnlocked: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        achievements.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { achievement ->
                    AchievementCard(
                        achievement = achievement,
                        accentColor = accentColorFor(achievement.category),
                        modifier = Modifier.weight(1f),
                        isNewlyUnlocked = achievement.id in newlyUnlocked,
                        onClick = { onAchievementClick(achievement) },
                        onConsumeNewlyUnlocked = onConsumeNewlyUnlocked
                    )
                }

                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun AchievementCard(
    achievement: Achievement,
    accentColor: Color,
    modifier: Modifier = Modifier,
    isNewlyUnlocked: Boolean,
    onClick: () -> Unit,
    onConsumeNewlyUnlocked: (String) -> Unit
) {
    val scaleTarget = remember(achievement.id, isNewlyUnlocked) {
        mutableStateOf(if (isNewlyUnlocked) 0.8f else 1f)
    }
    val scale by animateFloatAsState(
        targetValue = scaleTarget.value,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "achievementScale"
    )
    val pulseAlpha by rememberInfiniteTransition(label = "achievementPulse").animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    LaunchedEffect(achievement.id, isNewlyUnlocked) {
        if (isNewlyUnlocked) {
            scaleTarget.value = 1f
            delay(4_000)
            onConsumeNewlyUnlocked(achievement.id)
        }
    }

    val lockedAlpha = if (achievement.unlocked) 1f else 0.55f
    val shape = RoundedCornerShape(18.dp)

    Box(
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .border(1.dp, accentColor.copy(alpha = 0.35f * lockedAlpha), shape)
            .then(
                if (isNewlyUnlocked) {
                    Modifier.border(2.dp, accentColor.copy(alpha = pulseAlpha), shape)
                } else {
                    Modifier
                }
            )
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f), shape)
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(accentColor.copy(alpha = 0.16f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = achievement.emoji,
                        style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = achievement.name,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = achievement.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                if (achievement.unlocked) {
                    val badgeText = if (isNewlyUnlocked) "NUEVO" else "OK"
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = accentColor.copy(alpha = 0.14f)
                    ) {
                        Text(
                            text = badgeText,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = accentColor,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
            Spacer(modifier = Modifier.height(10.dp))

            if (achievement.unlocked) {
                Text(
                    text = "Desbloqueado el ${achievement.unlockedDate ?: "--/--/---- --:--"}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = accentColor,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            } else {
                LinearProgressIndicator(
                    progress = { achievement.progress.coerceIn(0, 100) / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(50)),
                    color = accentColor,
                    trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Te falta ${100 - achievement.progress.coerceIn(0, 100)}% para desbloquearlo",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}

@Composable
private fun AchievementSheetContent(
    achievement: Achievement,
    onShare: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = achievement.emoji,
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 80.sp),
                    textAlign = TextAlign.Start
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = achievement.name,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            if (achievement.unlocked) {
                IconButton(onClick = onShare) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "Compartir logro",
                        tint = PrimaryAccent
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (achievement.unlocked) {
            Text(
                text = "Desbloqueado el ${achievement.unlockedDate ?: "--/--/---- --:--"}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = RationalColor,
                    fontWeight = FontWeight.SemiBold
                )
            )
        } else {
            LinearProgressIndicator(
                progress = { achievement.progress.coerceIn(0, 100) / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50)),
                color = SecondaryAccent,
                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Te falta ${100 - achievement.progress.coerceIn(0, 100)}% para desbloquearlo",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        MindTrackButton(
            text = "Cerrar",
            onClick = onClose,
            isSecondary = true
        )
    }
}

@Composable
private fun ConfettiOverlay(
    modifier: Modifier = Modifier,
    onFinished: () -> Unit
) {
    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }
        var particles by remember(widthPx, heightPx) {
            mutableStateOf(generateParticles(widthPx, heightPx))
        }
        var active by remember { mutableStateOf(true) }

        LaunchedEffect(widthPx, heightPx) {
            val gravity = 900f
            val start = System.currentTimeMillis()
            while (active && System.currentTimeMillis() - start < 2_500L) {
                particles = particles.map { particle ->
                    val dt = 0.016f
                    val newVy = particle.vy + gravity * dt
                    val newY = particle.y + newVy * dt
                    val newX = particle.x + particle.vx * dt
                    particle.copy(
                        x = newX,
                        y = newY,
                        vy = newVy,
                        rotation = particle.rotation + particle.spin * dt
                    )
                }
                delay(16)
            }
            active = false
            onFinished()
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            particles.forEach { particle ->
                drawRect(
                    color = particle.color,
                    topLeft = androidx.compose.ui.geometry.Offset(particle.x, particle.y),
                    size = androidx.compose.ui.geometry.Size(particle.size, particle.size * 0.6f)
                )
            }
        }
    }
}

private fun generateParticles(widthPx: Float, heightPx: Float): List<ConfettiParticle> {
    val palette = listOf(PrimaryAccent, RationalColor, BalancedColor, ImpulsiveColor)
    return List(30) {
        ConfettiParticle(
            x = Random.nextFloat() * widthPx.coerceAtLeast(1f),
            y = Random.nextFloat() * (heightPx * 0.15f),
            vx = randomFloat(-120f, 120f),
            vy = randomFloat(80f, 220f),
            rotation = randomFloat(0f, 360f),
            spin = randomFloat(-240f, 240f),
            size = randomFloat(8f, 14f),
            color = palette.random()
        )
    }
}

private fun randomFloat(min: Float, max: Float): Float {
    return min + (Random.nextFloat() * (max - min))
}

private data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val rotation: Float,
    val spin: Float,
    val size: Float,
    val color: Color
)

private fun accentColorFor(category: AchievementCategory): Color {
    return when (category) {
        AchievementCategory.CONSTANCIA -> PrimaryAccent
        AchievementCategory.MAESTRIA -> BalancedColor
        AchievementCategory.EXPLORACION -> RationalColor
    }
}

private fun shareAchievement(context: android.content.Context, achievement: Achievement) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(
            Intent.EXTRA_TEXT,
            "¡Desbloqueé el logro '${achievement.name}' en MindTrack! ${achievement.emoji}"
        )
    }
    context.startActivity(Intent.createChooser(intent, "Compartir logro"))
}



