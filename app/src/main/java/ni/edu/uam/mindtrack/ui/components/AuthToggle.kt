package ni.edu.uam.mindtrack.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ni.edu.uam.mindtrack.ui.theme.BorderColor
import ni.edu.uam.mindtrack.ui.theme.PrimaryAccent
import ni.edu.uam.mindtrack.ui.theme.Surface
import ni.edu.uam.mindtrack.ui.theme.TextMuted

@Composable
fun AuthToggle(
    isLogin: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val outerShape = RoundedCornerShape(14.dp)
    val pillShape = RoundedCornerShape(10.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(outerShape)
            .background(Surface)
            .border(1.dp, BorderColor, outerShape)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ToggleTab(
            text = "Iniciar sesión",
            selected = isLogin,
            onClick = { onToggle(true) },
            modifier = Modifier.weight(1f),
            shape = pillShape
        )
        ToggleTab(
            text = "Registrarse",
            selected = !isLogin,
            onClick = { onToggle(false) },
            modifier = Modifier.weight(1f),
            shape = pillShape
        )
    }
}

@Composable
private fun ToggleTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
    shape: RoundedCornerShape
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .clip(shape)
            .background(if (selected) PrimaryAccent else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (selected) Color.White else TextMuted
            )
        )
    }
}