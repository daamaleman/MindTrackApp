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
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import ni.edu.uam.mindtrack.R
import ni.edu.uam.mindtrack.ui.components.SectionLabel
import ni.edu.uam.mindtrack.ui.theme.*
import ni.edu.uam.mindtrack.viewmodel.MindTrackViewModel
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build

@Composable
fun SettingsScreen(viewModel: MindTrackViewModel) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val context = LocalContext.current
    
    // Verificar si el sistema ha otorgado el permiso
    val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }

    // Obtener idioma actual
    val currentLocale = AppCompatDelegate.getApplicationLocales().get(0)?.language ?: "es"
    val languageLabel = if (currentLocale == "en") "English" else "Español"

    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .windowInsetsPadding(WindowInsets.statusBars)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 16.dp, bottom = innerPadding.calculateBottomPadding() + 24.dp)
        ) {
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    letterSpacing = (-0.6).sp,
                    color = TextWhite
                )
            )

            Spacer(Modifier.height(18.dp))
            SectionLabel(text = stringResource(R.string.preferences_section))
            Spacer(Modifier.height(10.dp))

            SettingsRow(
                icon = Icons.Filled.DarkMode,
                label = stringResource(R.string.dark_mode_label),
                sub = stringResource(R.string.dark_mode_sub),
                onClick = null
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
                label = stringResource(R.string.notifications_label),
                sub = stringResource(R.string.notifications_sub),
                onClick = null
            ) {
                Switch(
                    checked = notificationsEnabled && hasPermission,
                    onCheckedChange = { viewModel.setNotificationsEnabled(it) },
                    enabled = hasPermission,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = PrimaryAccent,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = SurfaceElevated,
                        uncheckedBorderColor = SurfaceElevated,
                        disabledCheckedThumbColor = Color.White.copy(alpha = 0.5f),
                        disabledCheckedTrackColor = PrimaryAccent.copy(alpha = 0.5f)
                    )
                )
            }
            Spacer(Modifier.height(8.dp))
            SettingsRow(
                icon = Icons.Filled.Translate,
                label = stringResource(R.string.language_label),
                sub = languageLabel,
                onClick = {
                    val nextLocale = if (currentLocale == "es") "en" else "es"
                    val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(nextLocale)
                    AppCompatDelegate.setApplicationLocales(appLocale)
                }
            )

            Spacer(Modifier.height(22.dp))
            SectionLabel(text = stringResource(R.string.account_support_section))
            Spacer(Modifier.height(10.dp))

            SettingsRow(
                icon = Icons.Outlined.Lock,
                label = stringResource(R.string.privacy_security_label),
                onClick = null
            )
            Spacer(Modifier.height(8.dp))
            SettingsRow(
                icon = Icons.AutoMirrored.Filled.HelpOutline,
                label = stringResource(R.string.help_support_label),
                onClick = null
            )
            Spacer(Modifier.height(8.dp))
            SettingsRow(
                icon = Icons.Outlined.Info,
                label = stringResource(R.string.about_app_label),
                sub = stringResource(R.string.version_label),
                onClick = null
            )
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    label: String,
    sub: String? = null,
    onClick: (() -> Unit)? = null,
    trail: (@Composable () -> Unit)? = null
) {
    val rowModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Surface)
            .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
            .then(rowModifier)
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
        } else if (onClick != null) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
