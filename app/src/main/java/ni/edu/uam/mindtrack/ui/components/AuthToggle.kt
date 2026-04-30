package ni.edu.uam.mindtrack.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ni.edu.uam.mindtrack.ui.theme.PrimaryAccent
import ni.edu.uam.mindtrack.ui.theme.SurfaceVariant
import ni.edu.uam.mindtrack.ui.theme.TextMuted
import ni.edu.uam.mindtrack.ui.theme.TextWhite

@Composable
fun AuthToggle(
    isLogin: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .width(280.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(SurfaceVariant.copy(alpha = 0.8f))
            .padding(4.dp)
    ) {
        // Sliding indicator
        val offset by animateDpAsState(
            targetValue = if (isLogin) 0.dp else 136.dp,
            animationSpec = tween(durationMillis = 300),
            label = "offset"
        )

        Box(
            modifier = Modifier
                .offset(x = offset)
                .fillMaxHeight()
                .width(136.dp)
                .clip(RoundedCornerShape(21.dp))
                .background(PrimaryAccent)
        )

        // Labels
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onToggle(true) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = if (isLogin) Color.White else TextMuted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onToggle(false) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Registro",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = if (!isLogin) Color.White else TextMuted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                )
            }
        }
    }
}
