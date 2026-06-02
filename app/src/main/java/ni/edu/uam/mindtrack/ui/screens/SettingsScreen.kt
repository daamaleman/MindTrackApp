package ni.edu.uam.mindtrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ni.edu.uam.mindtrack.ui.components.SectionLabel
import ni.edu.uam.mindtrack.ui.theme.*
import ni.edu.uam.mindtrack.viewmodel.MindTrackViewModel

@Composable
fun SettingsScreen(viewModel: MindTrackViewModel) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    var notificationsEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 100.dp)
    ) {
        Text(
            text = "Ajustes",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                letterSpacing = (-0.6).sp,
                color = TextWhite
            )
        )

        Spacer(Modifier.height(24.dp))
        SectionLabel(text = "Preferencias")
        Spacer(Modifier.height(10.dp))

        SettingsRow(
            icon = Icons.Filled.DarkMode,
            label = "Modo oscuro",
            sub = "Activado siempre",
            onClick = { viewModel.toggleTheme() }
        ) {
            Switch(
                checked = isDarkMode,
                onCheckedChange = { viewModel.toggleTheme() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = PrimaryAccent,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = SurfaceElevated,
                    uncheckedBorderColor = SurfaceElevated
                )
            )
        }
        Spacer(Modifier.height(8.dp))
        SettingsRow(
            icon = Icons.Outlined.Notifications,
            label = "Notificaciones",
            sub = "Recordatorios diarios",
            onClick = { notificationsEnabled = !notificationsEnabled }
        ) {
            Switch(
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = PrimaryAccent,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = SurfaceElevated,
                    uncheckedBorderColor = SurfaceElevated
                )
            )
        }
        Spacer(Modifier.height(8.dp))
        SettingsRow(
            icon = Icons.Filled.Translate,
            label = "Idioma",
            sub = "Español",
            onClick = {}
        )

        Spacer(Modifier.height(22.dp))
        SectionLabel(text = "Cuenta y soporte")
        Spacer(Modifier.height(10.dp))

        SettingsRow(
            icon = Icons.Outlined.Lock,
            label = "Privacidad y seguridad",
            onClick = {}
        )
        Spacer(Modifier.height(8.dp))
        SettingsRow(
            icon = Icons.AutoMirrored.Filled.HelpOutline,
            label = "Ayuda y soporte",
            onClick = {}
        )
        Spacer(Modifier.height(8.dp))
        SettingsRow(
            icon = Icons.Outlined.Info,
            label = "Acerca de MindTrack",
            sub = "Versión 1.0.0",
            onClick = {}
        )
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    label: String,
    sub: String? = null,
    onClick: () -> Unit,
    trail: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Surface)
            .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = TextWhite
                )
            )
            if (sub != null) {
                Spacer(Modifier.height(1.dp))
                Text(
                    text = sub,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextMuted,
                        fontSize = 11.5.sp
                    )
                )
            }
        }
        if (trail != null) {
            trail()
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
