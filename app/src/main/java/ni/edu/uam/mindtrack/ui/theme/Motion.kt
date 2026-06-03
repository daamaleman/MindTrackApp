package ni.edu.uam.mindtrack.ui.theme

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith

object MindTrackMotion {
    private const val NAV_DURATION = 360
    private const val CONTENT_DURATION = 260
    private const val SECTION_DURATION = 240

    private val easing = FastOutSlowInEasing

    fun navEnter(isForward: Boolean): EnterTransition {
        val direction = if (isForward) 1 else -1
        return slideInHorizontally(
            animationSpec = tween(NAV_DURATION, easing = easing)
        ) { fullWidth ->
            fullWidth / 6 * direction
        } + fadeIn(animationSpec = tween(NAV_DURATION / 2, easing = easing))
    }

    fun navExit(isForward: Boolean): ExitTransition {
        val direction = if (isForward) -1 else 1
        return slideOutHorizontally(
            animationSpec = tween(NAV_DURATION, easing = easing)
        ) { fullWidth ->
            fullWidth / 6 * direction
        } + fadeOut(animationSpec = tween(NAV_DURATION / 2, easing = easing))
    }

    fun authContentTransition(targetIsLogin: Boolean): ContentTransform {
        val enterDirection = if (targetIsLogin) -1 else 1
        val exitDirection = -enterDirection

        return (slideInHorizontally(
            animationSpec = tween(CONTENT_DURATION, easing = easing)
        ) { fullWidth -> fullWidth / 5 * enterDirection } + fadeIn(
            animationSpec = tween(CONTENT_DURATION, easing = easing)
        )) togetherWith (slideOutHorizontally(
            animationSpec = tween(CONTENT_DURATION, easing = easing)
        ) { fullWidth -> fullWidth / 5 * exitDirection } + fadeOut(
            animationSpec = tween(CONTENT_DURATION, easing = easing)
        ))
    }

    fun onboardingEmojiTransition(): ContentTransform {
        return (fadeIn(animationSpec = tween(220, easing = easing)) + scaleIn(
            initialScale = 0.9f,
            animationSpec = tween(220, easing = easing)
        )) togetherWith (fadeOut(animationSpec = tween(180, easing = easing)) + scaleOut(
            targetScale = 0.9f,
            animationSpec = tween(180, easing = easing)
        ))
    }

    fun sectionEnterTransition(fromTop: Boolean = false): EnterTransition {
        return fadeIn(animationSpec = tween(SECTION_DURATION, easing = easing)) +
            slideInVertically(
                animationSpec = tween(SECTION_DURATION, easing = easing)
            ) { fullHeight ->
                if (fromTop) -fullHeight / 8 else fullHeight / 8
            } +
            scaleIn(
                initialScale = 0.98f,
                animationSpec = tween(SECTION_DURATION, easing = easing)
            )
    }
}
