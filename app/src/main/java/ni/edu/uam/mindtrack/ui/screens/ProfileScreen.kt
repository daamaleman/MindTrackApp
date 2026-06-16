package ni.edu.uam.mindtrack.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ni.edu.uam.mindtrack.model.Achievement
import ni.edu.uam.mindtrack.model.SessionResult
import ni.edu.uam.mindtrack.ui.components.MindTrackButton
import ni.edu.uam.mindtrack.ui.components.StatCard
import ni.edu.uam.mindtrack.ui.theme.MindTrackMotion
import ni.edu.uam.mindtrack.ui.theme.*
import ni.edu.uam.mindtrack.viewmodel.MindTrackViewModel
import ni.edu.uam.mindtrack.viewmodel.ProfileViewModel
import ni.edu.uam.mindtrack.data.repository.Result
import ni.edu.uam.mindtrack.data.remote.UserDto
import ni.edu.uam.mindtrack.engine.AuthManager

@Composable
fun ProfileScreen(
    viewModel: MindTrackViewModel,
    profileViewModel: ProfileViewModel,
    onEditProfile: () -> Unit,
    onOpenAchievements: () -> Unit,
    onOpenStatistics: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenQuestionnaire: () -> Unit,
    onLogout: () -> Unit,
    onStartFirstSimulation: () -> Unit
) {
    val uiState by profileViewModel.uiState.collectAsState()
    val sessionHistory by viewModel.sessionHistory.collectAsState()
    val achievements by viewModel.achievements.collectAsState()
    
    var isVisible by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        isVisible = true
        val currentUserId = AuthManager.currentUser.value?.id ?: 1L
        profileViewModel.loadProfile(currentUserId)
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { 
                Text(
                    "Cerrar Sesión", 
                    color = PrimaryAccent,
                    fontWeight = FontWeight.Bold
                ) 
            },
            text = { Text("¿Estás seguro de que deseas salir de tu cuenta?") },
            containerColor = SurfaceVariant,
            shape = RoundedCornerShape(16.dp),
            confirmButton = {
                TextButton(onClick = { 
                    showLogoutDialog = false
                    onLogout() 
                }) {
                    Text("Confirmar", color = PrimaryAccent, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar", color = TextMuted)
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is Result.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryAccent)
                    }
                }
                is Result.Error -> {
                    LaunchedEffect(state.message) {
                        snackbarHostState.showSnackbar(state.message)
                    }
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Error al cargar perfil", color = ImpulsiveColor)
                            Spacer(modifier = Modifier.height(8.dp))
                            MindTrackButton(text = "Reintentar", onClick = { profileViewModel.loadProfile(1L) })
                        }
                    }
                }
                is Result.Success -> {
                    val user = state.data
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Header with Avatar and Name
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = MindTrackMotion.sectionEnterTransition(fromTop = true)
                        ) {
                            ProfileHeader(
                                name = user.nombre,
                                email = user.correo,
                                memberSince = "Jun 2026", // Mocked as not in DTO
                                isPremium = true,
                                profileImageUri = user.fotoPerfil,
                                onOpenSettings = onOpenSettings
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Bio Section
                        if (!user.biografia.isNullOrBlank()) {
                            Text(
                                text = user.biografia,
                                modifier = Modifier.padding(horizontal = 24.dp),
                                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }

                        // Quick Stats
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = MindTrackMotion.sectionEnterTransition()
                        ) {
                            Column {
                                QuickStatsRow(sessionHistory, achievements)
                                
                                Spacer(modifier = Modifier.height(32.dp))
                                
                                // Dominant Profile Section
                                SectionHeader(title = "PERFIL DOMINANTE")
                                DominantProfileCard(
                                    sessionHistory = sessionHistory,
                                    onOpenStatistics = onOpenStatistics,
                                    onStartFirstSimulation = onStartFirstSimulation
                                )

                                Spacer(modifier = Modifier.height(32.dp))

                                // Recent Achievements
                                SectionHeader(
                                    title = "LOGROS RECIENTES",
                                    actionText = "Ver todos >",
                                    onActionClick = onOpenAchievements
                                )
                                RecentAchievementsRow(achievements)

                                Spacer(modifier = Modifier.height(32.dp))

                                 // Account Actions
                                 AccountActions(
                                     onEditProfile = onEditProfile,
                                     onOpenQuestionnaire = onOpenQuestionnaire,
                                     onLogout = { showLogoutDialog = true }
                                 )

                                Spacer(modifier = Modifier.height(40.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(
    name: String,
    email: String,
    memberSince: String,
    isPremium: Boolean,
    profileImageUri: String?,
    onOpenSettings: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, end = 16.dp)
    ) {
        IconButton(
            onClick = onOpenSettings,
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                Icons.Default.Settings,
                contentDescription = "Ajustes",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    modifier = Modifier.size(120.dp),
                    shape = CircleShape,
                    color = PrimaryAccent.copy(alpha = 0.6f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (profileImageUri != null) {
                            AsyncImage(
                                model = profileImageUri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = name.firstOrNull()?.toString() ?: "U",
                                style = MaterialTheme.typography.displayMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Black
                                )
                            )
                        }
                    }
                }
                Surface(
                    modifier = Modifier.size(32.dp),
                    shape = CircleShape,
                    color = RationalColor,
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.background)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = name,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            if (isPremium) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = PrimaryAccent.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = PrimaryAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "PREMIUM - Miembro desde $memberSince",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = PrimaryAccent,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickStatsRow(history: List<SessionResult>, achievements: List<Achievement>) {
    val totalSessions = history.size
    val activeDays = history.map { it.date.substringBefore(" ") }.distinct().size
    val unlockedAchievements = achievements.count { it.unlocked }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        StatCard(number = totalSessions.toString(), label = "Sesiones")
        StatCard(number = activeDays.toString(), label = "Días activo")
        StatCard(number = unlockedAchievements.toString(), label = "Logros")
    }
}

@Composable
fun SectionHeader(
    title: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 1.sp
            )
        )
        if (actionText != null && onActionClick != null) {
            Text(
                text = actionText,
                modifier = Modifier.clickable { onActionClick() },
                style = MaterialTheme.typography.labelSmall.copy(
                    color = PrimaryAccent,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun DominantProfileCard(
    sessionHistory: List<SessionResult>,
    onOpenStatistics: () -> Unit,
    onStartFirstSimulation: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .clickable { if (sessionHistory.isNotEmpty()) onOpenStatistics() },
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        if (sessionHistory.isEmpty()) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Analytics,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "No hay datos suficientes",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    "Realiza tu primera simulación para descubrir tu perfil",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                MindTrackButton(
                    text = "Iniciar Simulación",
                    onClick = onStartFirstSimulation
                )
            }
        } else {
            val total = sessionHistory.size
            val estrategicoCount = sessionHistory.count { it.finalResult.contains("estratégico", true) }
            val racionalCount = sessionHistory.count { it.finalResult.contains("exitoso", true) || it.finalResult.contains("racional", true) }
            val impulsivoCount = sessionHistory.count { it.finalResult.contains("crítico", true) || it.finalResult.contains("impulsivo", true) }

            val dominantProfile = when {
                estrategicoCount >= racionalCount && estrategicoCount >= impulsivoCount -> "Pensador Estratégico"
                racionalCount >= estrategicoCount && racionalCount >= impulsivoCount -> "Analista Exitoso"
                else -> "Perfil Impulsivo"
            }
            
            val dominantDesc = when {
                estrategicoCount >= racionalCount && estrategicoCount >= impulsivoCount -> "Equilibras intuición y análisis"
                racionalCount >= estrategicoCount && racionalCount >= impulsivoCount -> "Priorizas la lógica y resultados"
                else -> "Actúas rápido bajo presión"
            }

            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = BalancedColor.copy(alpha = 0.2f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("⚖️", fontSize = 28.sp)
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = dominantProfile,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "$dominantDesc • $estrategicoCount de $total sesiones",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                ProfileProgressBar("Estratégico", estrategicoCount.toFloat() / total, BalancedColor)
                Spacer(modifier = Modifier.height(12.dp))
                ProfileProgressBar("Racional", racionalCount.toFloat() / total, RationalColor)
                Spacer(modifier = Modifier.height(12.dp))
                ProfileProgressBar("Impulsivo", impulsivoCount.toFloat() / total, ImpulsiveColor)
            }
        }
    }
}

@Composable
fun ProfileProgressBar(label: String, progress: Float, color: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(100.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.15f)
        )
    }
}

@Composable
fun RecentAchievementsRow(achievements: List<Achievement>) {
    val unlocked = achievements.filter { it.unlocked }.take(4)
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        unlocked.forEach { achievement ->
            Surface(
                modifier = Modifier.size(64.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(achievement.emoji, fontSize = 32.sp)
                }
            }
        }
        if (unlocked.size < 4) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                }
            }
        }
    }
}

@Composable
fun AccountActions(
    onEditProfile: () -> Unit,
    onOpenQuestionnaire: () -> Unit,
    onLogout: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        ActionButton(
            text = "Editar Perfil",
            icon = Icons.Default.Edit,
            onClick = onEditProfile
        )
        Spacer(modifier = Modifier.height(12.dp))
        ActionButton(
            text = "Evaluación de personalidad",
            icon = Icons.Default.Analytics,
            onClick = onOpenQuestionnaire
        )
        Spacer(modifier = Modifier.height(12.dp))
        ActionButton(
            text = "Cerrar Sesión",
            icon = Icons.AutoMirrored.Filled.Logout,
            onClick = onLogout,
            isDestructive = true
        )
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (isDestructive) ImpulsiveColor else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDestructive) ImpulsiveColor else MaterialTheme.colorScheme.onSurface
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}
