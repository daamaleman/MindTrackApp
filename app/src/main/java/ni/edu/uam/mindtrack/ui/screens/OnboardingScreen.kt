package ni.edu.uam.mindtrack.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Canvas
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.launch
import ni.edu.uam.mindtrack.ui.theme.PrimaryAccent
import ni.edu.uam.mindtrack.ui.theme.BalancedColor
import ni.edu.uam.mindtrack.ui.theme.Background
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import ni.edu.uam.mindtrack.viewmodel.MindTrackViewModel

data class OnboardingSlide(val title: String, val subtitle: String, val emoji: String)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingScreen(
    viewModel: MindTrackViewModel,
    onFinish: () -> Unit
) {
    val slides = listOf(
        OnboardingSlide("Bienvenido", "Conoce cómo piensas", "🧠"),
        OnboardingSlide("Decisiones", "Practica decisiones cotidianas", "⚖️"),
        OnboardingSlide("Progreso", "Ve tu evolución en el tiempo", "📈")
    )

    val pagerState = rememberPagerState(pageCount = { slides.size }, initialPage = 0)
    var step by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val infinite = rememberInfiniteTransition()
    val ringRotation by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = tween(8000, easing = LinearEasing), repeatMode = RepeatMode.Restart)
    )

    // Observe page changes to update step, vibrate and analytics hook
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collectLatest { page ->
            step = page
            // Haptic feedback on slide change
            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
        }
    }

    // Analytics placeholder when step changes
    LaunchedEffect(step) {
        // TODO: Track event "onboarding_slide_viewed" with step
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top right skip
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
            Text(
                text = "Saltar",
                color = Color.White.copy(alpha = 0.65f),
                modifier = Modifier
                    .padding(4.dp)
                    .clickable {
                        coroutineScope.launch {
                            viewModel.completeOnboarding()
                            onFinish()
                        }
                    }
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        HorizontalPager(pagerState, modifier = Modifier.weight(1f)) { page ->
            val slide = slides[page]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .semantics { contentDescription = "${slide.title}. ${slide.subtitle}" },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Decorative central area
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(240.dp)) {
                    // rotating dotted rings and orbiting dots
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val cx = size.width / 2f
                        val cy = size.height / 2f
                        val center = Offset(cx, cy)
                        val maxRadius = size.minDimension / 2f
                        val radii = listOf(maxRadius * 0.9f, maxRadius * 0.64f, maxRadius * 0.42f)

                        // draw subtle stroked rings
                        radii.forEachIndexed { idx, r ->
                            drawCircle(
                                color = PrimaryAccent.copy(alpha = 0.08f + (idx * 0.02f)),
                                radius = r,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.6f),
                                center = center
                            )
                        }

                        // dotted appearance: draw small dots along circumference and rotate them
                        val dotsCount = 36
                        val anglesBase = (0 until dotsCount).map { it * (360f / dotsCount) }
                        anglesBase.forEachIndexed { idx, base ->
                            val angle = base + ringRotation * (1f + (idx % 3) * 0.07f)
                            val rad = Math.toRadians(angle.toDouble())
                            val ringIndex = idx % radii.size
                            val or = radii[ringIndex]
                            val x = cx + (or * 0.95f) * kotlin.math.cos(rad).toFloat()
                            val y = cy + (or * 0.95f) * kotlin.math.sin(rad).toFloat()
                            drawCircle(color = PrimaryAccent.copy(alpha = 0.18f), radius = if (idx % 6 == 0) 3f else 1.6f, center = Offset(x, y))
                        }

                        // orbiting larger dots
                        val angles = listOf(ringRotation * 1.15f, ringRotation * -0.85f, ringRotation * 1.6f)
                        angles.forEachIndexed { i, a ->
                            val rad = Math.toRadians(a.toDouble())
                            val or = radii[i % radii.size]
                            val x = cx + (or * 0.75f) * kotlin.math.cos(rad).toFloat()
                            val y = cy + (or * 0.75f) * kotlin.math.sin(rad).toFloat()
                            drawCircle(color = if (i == 0) BalancedColor else PrimaryAccent.copy(alpha = 0.9f), radius = 4f, center = Offset(x, y))
                        }
                    }

                    // emoji center with radial gradient glow
                    AnimatedContent(targetState = slide.emoji, transitionSpec = {
                        (fadeIn(animationSpec = tween(300, delayMillis = 100)) + scaleIn(initialScale = 0.6f, animationSpec = tween(300, delayMillis = 100))) togetherWith
                                (fadeOut(animationSpec = tween(200)) + scaleOut(targetScale = 0.6f, animationSpec = tween(200)))
                    }) { emoji ->
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(PrimaryAccent, PrimaryAccent.copy(alpha = 0.36f), Color.Transparent),
                                        center = Offset.Unspecified,
                                        radius = 200f
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 48.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // step capsule
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(PrimaryAccent.copy(alpha = 0.14f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    val stepNumber = (step + 1).coerceAtMost(slides.size)
                    val stepText = "PASO %02d".format(stepNumber)
                    Text(text = stepText, color = PrimaryAccent.copy(alpha = 0.98f), style = MaterialTheme.typography.labelSmall)
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(text = slide.title, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, color = Color.White))
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = slide.subtitle, style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.72f)))
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // progress dots
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.wrapContentWidth()) {
            slides.forEachIndexed { index, _ ->
                Box(modifier = Modifier
                    .padding(6.dp)
                    .size(if (index == step) 36.dp else 8.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (index == step) PrimaryAccent else Color.White.copy(alpha = 0.12f))
                    .semantics { contentDescription = "Punto ${index + 1} de ${slides.size}" })
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // primary button (styled with PrimaryAccent)
        if (pagerState.currentPage < slides.lastIndex) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        val next = (pagerState.currentPage + 1).coerceAtMost(slides.lastIndex)
                        pagerState.animateScrollToPage(next)
                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent)
            ) {
                Text("Siguiente", color = Color.White)
            }
        } else {
            Button(
                onClick = {
                    coroutineScope.launch {
                        viewModel.completeOnboarding()
                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        onFinish()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent)
            ) {
                Text("Comenzar", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // small back link
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            if (pagerState.currentPage > 0) {
                Text(modifier = Modifier.clickable {
                    coroutineScope.launch { pagerState.animateScrollToPage((pagerState.currentPage - 1).coerceAtLeast(0)) }
                }, text = "Atrás", color = Color.White.copy(alpha = 0.72f))
            }
        }
    }
}









