package ni.edu.uam.mindtrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.*
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
import ni.edu.uam.mindtrack.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var showDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogMessage by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = dialogTitle, fontWeight = FontWeight.Bold, color = PrimaryAccent) },
            text = { Text(text = dialogMessage, color = TextWhite) },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Entendido", color = PrimaryAccent)
                }
            },
            containerColor = SurfaceVariant,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            "Ajustes",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
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
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Perfil de Usuario (Placeholder)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceVariant, RoundedCornerShape(16.dp))
                    .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(PrimaryAccent.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryAccent, modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Usuario MindTrack",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = Color.White)
                    )
                    Text(
                        text = "Premium",
                        style = MaterialTheme.typography.labelSmall.copy(color = PrimaryAccent, fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Preferencias",
                style = MaterialTheme.typography.labelSmall.copy(color = TextFaint, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            )
            Spacer(modifier = Modifier.height(12.dp))

            SettingsItem(
                icon = Icons.Default.Notifications,
                label = "Notificaciones",
                onClick = {
                    dialogTitle = "Notificaciones"
                    dialogMessage = "Próximamente podrás configurar recordatorios diarios para realizar tus simulaciones."
                    showDialog = true
                }
            )
            SettingsItem(
                icon = Icons.Default.Lock,
                label = "Privacidad y Seguridad",
                onClick = {
                    dialogTitle = "Privacidad"
                    dialogMessage = "Tus datos de simulación se guardan localmente en este dispositivo y no se comparten con terceros."
                    showDialog = true
                }
            )
            SettingsItem(
                icon = Icons.Default.Palette,
                label = "Tema y Apariencia",
                onClick = {
                    dialogTitle = "Apariencia"
                    dialogMessage = "MindTrack está optimizado para el modo oscuro para reducir la fatiga visual. Más temas vendrán pronto."
                    showDialog = true
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Soporte",
                style = MaterialTheme.typography.labelSmall.copy(color = TextFaint, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            )
            Spacer(modifier = Modifier.height(12.dp))

            SettingsItem(
                icon = Icons.AutoMirrored.Filled.Help,
                label = "Ayuda y Soporte",
                onClick = {
                    dialogTitle = "Ayuda"
                    dialogMessage = "Para soporte técnico, contacta a soporte@mindtrack.app o visita nuestra base de conocimientos."
                    showDialog = true
                }
            )
            SettingsItem(
                icon = Icons.Default.Info,
                label = "Acerca de MindTrack",
                onClick = {
                    dialogTitle = "Acerca de"
                    dialogMessage = "MindTrack v1.0.0\n\nDesarrollado para ayudarte a entender mejor tus patrones de toma de decisiones a través de escenarios realistas."
                    showDialog = true
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Versión 1.0.0",
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                style = MaterialTheme.typography.labelSmall.copy(color = TextFaint),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun SettingsItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(SurfaceVariant, RoundedCornerShape(12.dp))
            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = TextMuted, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, style = MaterialTheme.typography.bodyMedium.copy(color = Color.White), modifier = Modifier.weight(1f))
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = TextFaint, modifier = Modifier.size(20.dp))
    }
}
