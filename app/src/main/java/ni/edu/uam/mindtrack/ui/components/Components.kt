package ni.edu.uam.mindtrack.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ni.edu.uam.mindtrack.model.Option
import ni.edu.uam.mindtrack.model.SessionResult
import ni.edu.uam.mindtrack.navigation.Routes
import ni.edu.uam.mindtrack.ui.theme.*

private val PrimaryGradient = Brush.verticalGradient(
    listOf(Color(0xFF8567FF), Color(0xFF6B4FF0))
)

@Composable
fun MindTrackPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    height: Dp = 52.dp
) {
    val shape = RoundedCornerShape(14.dp)
    val bgModifier = if (enabled && !loading) {
        Modifier.background(PrimaryGradient, shape)
    } else {
        Modifier.background(SurfaceElevated, shape)
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .then(bgModifier)
            .clickable(enabled = enabled && !loading, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = if (enabled) Color.White else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (enabled) Color.White else TextMuted
                    )
                )
                if (trailingIcon != null) {
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = trailingIcon,
                        contentDescription = null,
                        tint = if (enabled) Color.White else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MindTrackSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    height: Dp = 52.dp
) {
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .border(1.dp, BorderStrong, shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = TextWhite,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = TextWhite
                )
            )
        }
    }
}

@Composable
fun MindTrackButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    isSecondary: Boolean = false
) {
    if (isSecondary) {
        MindTrackSecondaryButton(text = text, onClick = onClick, modifier = modifier)
    } else {
        MindTrackPrimaryButton(text = text, onClick = onClick, modifier = modifier, enabled = enabled, loading = loading)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MindTrackInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingClick: (() -> Unit)? = null,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(14.dp)
    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            singleLine = true,
            shape = shape,
            placeholder = {
                Text(
                    placeholder,
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted)
                )
            },
            leadingIcon = if (leadingIcon != null) {
                {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else null,
            trailingIcon = if (trailingIcon != null) {
                {
                    if (onTrailingClick != null) {
                        IconButton(onClick = onTrailingClick) {
                            Icon(
                                imageVector = trailingIcon,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = trailingIcon,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            } else null,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = TextWhite,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryAccent,
                unfocusedBorderColor = BorderColor,
                focusedContainerColor = SurfaceVariant,
                unfocusedContainerColor = SurfaceVariant,
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                cursorColor = PrimaryAccent
            )
        )
        Box(
            modifier = Modifier
                .offset(x = 12.dp, y = (-7).dp)
                .background(Background)
                .padding(horizontal = 6.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = TextMuted,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    letterSpacing = 0.3.sp
                )
            )
        }
    }
}

@Composable
fun Pill(
    text: String,
    modifier: Modifier = Modifier,
    accent: Color = PrimaryAccent,
    contentColor: Color = SecondaryAccent
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(accent.copy(alpha = 0.12f))
            .border(1.dp, accent.copy(alpha = 0.32f), RoundedCornerShape(100.dp))
            .padding(horizontal = 11.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(accent)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = text.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                color = contentColor,
                fontWeight = FontWeight.Bold,
                fontSize = 10.5.sp,
                letterSpacing = 0.8.sp
            )
        )
    }
}

@Composable
fun StatTile(
    number: String,
    label: String,
    modifier: Modifier = Modifier,
    numberColor: Color = SecondaryAccent
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
            .padding(vertical = 14.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = number,
            style = MaterialTheme.typography.headlineLarge.copy(
                color = numberColor,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
                letterSpacing = (-0.5).sp
            )
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelMedium.copy(
                color = TextMuted,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                letterSpacing = 0.6.sp
            )
        )
    }
}

@Composable
fun StatMini(
    icon: ImageVector,
    label: String,
    value: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    val pct = value.coerceIn(0, 100) / 100f
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
        }
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                letterSpacing = (-0.3).sp,
                color = TextWhite
            )
        )
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                color = TextMuted,
                fontWeight = FontWeight.SemiBold,
                fontSize = 9.5.sp,
                letterSpacing = 0.5.sp
            )
        )
        Box(
            modifier = Modifier
                .width(36.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White.copy(alpha = 0.06f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(pct)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
        }
    }
}

@Composable
fun OptionCard(
    option: Option,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(14.dp)
    val borderColor = if (isSelected) PrimaryAccent else BorderColor
    val bg: Brush = if (isSelected) {
        Brush.verticalGradient(
            listOf(PrimaryAccent.copy(alpha = 0.10f), PrimaryAccent.copy(alpha = 0.04f))
        )
    } else SolidColor(Surface)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(bg, shape)
            .border(if (isSelected) 1.5.dp else 1.dp, borderColor, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 1.dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(if (isSelected) PrimaryAccent else Color.Transparent)
                .border(
                    width = 1.5.dp,
                    color = if (isSelected) PrimaryAccent else BorderStrong,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = option.text,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.5.sp,
                    lineHeight = 19.sp,
                    color = TextWhite
                )
            )
        }
    }
}

@Composable
fun StepDots(
    total: Int,
    currentIndex: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        for (i in 0 until total) {
            val color = when {
                i < currentIndex -> PrimaryAccent
                i == currentIndex -> PrimaryAccent
                else -> SurfaceElevated
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
        }
    }
}

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        modifier = modifier.padding(horizontal = 4.dp),
        style = MaterialTheme.typography.labelSmall.copy(
            color = TextMuted,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 1.2.sp
        )
    )
}

@Composable
fun IconCircleButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    iconSize: Dp = 20.dp,
    contentColor: Color = TextWhite
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Surface)
            .border(1.dp, BorderColor, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Composable
fun HistoryItemCard(session: SessionResult) {
    val (icon, color) = when {
        session.finalResult.contains("racional", ignoreCase = true) ||
            session.finalResult.contains("exitoso", ignoreCase = true) -> Icons.Filled.Psychology to RationalColor
        session.finalResult.contains("equilibr", ignoreCase = true) ||
            session.finalResult.contains("estratégico", ignoreCase = true) -> Icons.Filled.Psychology to BalancedColor
        session.finalResult.contains("impulsiv", ignoreCase = true) ||
            session.finalResult.contains("crítico", ignoreCase = true) -> Icons.Filled.Bolt to ImpulsiveColor
        else -> Icons.Filled.Psychology to SecondaryAccent
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.12f))
                .border(1.dp, color.copy(alpha = 0.30f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = session.finalResult,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextWhite
                )
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = session.date,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextMuted,
                    fontSize = 11.5.sp
                )
            )
        }
    }
}

@Composable
fun StatCard(number: String, label: String) {
    StatTile(number = number, label = label, modifier = Modifier.width(100.dp))
}

@Composable
fun MindTrackBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        Triple(Routes.Home.route, Triple(Icons.Filled.Home, Icons.Outlined.Home, "Inicio"), null),
        Triple(Routes.Statistics.route, Triple(Icons.Filled.BarChart, Icons.Outlined.BarChart, "Stats"), null),
        Triple(Routes.Profile.route, Triple(Icons.Filled.Person, Icons.Outlined.Person, "Perfil"), null),
        Triple(Routes.Settings.route, Triple(Icons.Filled.Tune, Icons.Outlined.Tune, "Ajustes"), null)
    )
    val shape = RoundedCornerShape(24.dp)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .shadow(12.dp, shape)
                .clip(shape)
                .background(Surface.copy(alpha = 0.95f))
                .border(1.dp, BorderColor, shape)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            items.forEach { (route, iconLabel, _) ->
                val (filledIcon, outlinedIcon, label) = iconLabel
                val isSelected = currentRoute == route
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onNavigate(route) },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isSelected) filledIcon else outlinedIcon,
                            contentDescription = label,
                            tint = if (isSelected) SecondaryAccent else TextMuted,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isSelected) SecondaryAccent else TextMuted,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 10.sp,
                                letterSpacing = 0.sp
                            )
                        )
                    }
                }
            }
        }
    }
}
