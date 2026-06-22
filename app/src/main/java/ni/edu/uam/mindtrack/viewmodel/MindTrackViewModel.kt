package ni.edu.uam.mindtrack.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ni.edu.uam.mindtrack.data.OnboardingPreferences
import ni.edu.uam.mindtrack.engine.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ni.edu.uam.mindtrack.R
import ni.edu.uam.mindtrack.engine.GameEngine
import ni.edu.uam.mindtrack.model.Achievement
import ni.edu.uam.mindtrack.model.AchievementCategory
import ni.edu.uam.mindtrack.model.Option
import ni.edu.uam.mindtrack.model.Scenario
import ni.edu.uam.mindtrack.model.PlayerState
import ni.edu.uam.mindtrack.model.SessionResult
import ni.edu.uam.mindtrack.model.UserProfile
import ni.edu.uam.mindtrack.data.remote.TrackSessionDto
import ni.edu.uam.mindtrack.data.remote.UserDto
import ni.edu.uam.mindtrack.data.remote.EstadisticaDto
import ni.edu.uam.mindtrack.data.remote.LogroDto
import ni.edu.uam.mindtrack.data.repository.SessionRepository
import ni.edu.uam.mindtrack.data.repository.UserRepository
import ni.edu.uam.mindtrack.data.repository.Result
import ni.edu.uam.mindtrack.util.NotificationHelper
import java.time.LocalDate
import java.time.ZoneId
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class MindTrackViewModel(
    private val onboardingPreferences: OnboardingPreferences,
    private val sessionRepository: SessionRepository,
    private val userRepository: UserRepository,
    private val notificationHelper: NotificationHelper,
    private val context: Context
) : ViewModel() {
    private val gameEngine = GameEngine()

    private val initialState = PlayerState(
        energy = 80,
        stress = 20,
        progress = 0,
        money = 100
    )

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Onboarding persistence state
    private val _onboardingCompleted = MutableStateFlow<Boolean?>(null)
    val onboardingCompleted: StateFlow<Boolean?> = _onboardingCompleted.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _userProfile = MutableStateFlow(
        UserProfile(
            name = context.getString(R.string.guest_user_name),
            email = "invitado@mindtrack.ni",
            memberSince = SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date()),
            isPremium = false
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _avatarUri = MutableStateFlow<Uri?>(null)
    val avatarUri: StateFlow<Uri?> = _avatarUri.asStateFlow()

    fun toggleTheme() {
        _isDarkMode.value = !_isDarkMode.value
    }

    // Último informe del cuestionario (para mostrar pantalla de resultados)
    private val _lastQuestionnaireReport = MutableStateFlow<ProfileReport?>(null)
    val lastQuestionnaireReport: StateFlow<ProfileReport?> = _lastQuestionnaireReport.asStateFlow()

    // Guardar un informe de cuestionario adaptativo como una sesión en el historial
    fun addQuestionnaireSession(report: ProfileReport) {
        val dateString = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        val summary = "${report.mainLabel} • Confianza ${(report.globalConfidence * 100).toInt()}%"
        val newResult = SessionResult(
            id = UUID.randomUUID().toString(),
            finalResult = summary,
            date = dateString,
            finalState = initialState.copy(),
            choicesMade = report.traitScores.size,
            path = emptyList()
        )

        _sessionHistory.value = listOf(newResult) + _sessionHistory.value
        // Guardar también el último informe para presentarlo en pantalla dedicada
        _lastQuestionnaireReport.value = report
        recalculateStreaks()
        recalculateAchievements()

        // Intentar guardar en API (no bloqueante)
        viewModelScope.launch {
            val currentUserId = AuthManager.currentUser.value?.id ?: 1L
            val sessionDto = TrackSessionDto(
                id = null,
                idUsuario = currentUserId,
                estadoAnimo = summary,
                notas = dateString
            )
            try {
                sessionRepository.createSession(sessionDto)
            } catch (e: Exception) {
                // Silencioso: si falla la red, ya quedó en memoria local
            }
        }
    }

    // Piscina completa de escenarios (Localizados)
    private val scenarioPool: List<Scenario>
        get() = listOf(
            Scenario(
                id = 1,
                title = context.getString(R.string.sc_work_title),
                question = context.getString(R.string.sc_work_q),
                options = listOf(
                    Option(context.getString(R.string.sc_work_opt1), 2, -20, 15, 25, 30),
                    Option(context.getString(R.string.sc_work_opt2), 3, -5, 5, 10, 10),
                    Option(context.getString(R.string.sc_work_opt3), 4, 10, -10, 0, -5)
                )
            ),
            Scenario(
                id = 2,
                title = context.getString(R.string.sc_crisis_title),
                question = context.getString(R.string.sc_crisis_q),
                options = listOf(
                    Option(context.getString(R.string.sc_crisis_opt1), 5, -30, 25, 30, 0),
                    Option(context.getString(R.string.sc_crisis_opt2), 5, -10, 10, 20, -10),
                    Option(context.getString(R.string.sc_crisis_opt3), null, -5, 20, -20, -20)
                )
            ),
            Scenario(
                id = 3,
                title = context.getString(R.string.sc_study_title),
                question = context.getString(R.string.sc_study_q),
                options = listOf(
                    Option(context.getString(R.string.sc_study_opt1), 5, -25, 20, 25, 0),
                    Option(context.getString(R.string.sc_study_opt2), 5, 10, -10, 15, 0),
                    Option(context.getString(R.string.sc_study_opt3), null, 15, -15, -10, 0)
                )
            ),
            Scenario(
                id = 4,
                title = context.getString(R.string.sc_social_title),
                question = context.getString(R.string.sc_social_q),
                options = listOf(
                    Option(context.getString(R.string.sc_social_opt1), 5, 15, -10, -5, -40),
                    Option(context.getString(R.string.sc_social_opt2), 5, 5, -5, 0, -15),
                    Option(context.getString(R.string.sc_social_opt3), 5, 10, -5, 5, 0)
                )
            ),
            Scenario(
                id = 5,
                title = context.getString(R.string.sc_final_title),
                question = context.getString(R.string.sc_final_q),
                options = listOf(
                    Option(context.getString(R.string.sc_final_opt1), null, -10, 5, 20, -30),
                    Option(context.getString(R.string.sc_final_opt2), null, 30, -25, 0, -40),
                    Option(context.getString(R.string.sc_final_opt3), null, -20, 15, 15, 20)
                )
            )
        )

    private val _currentScenarios = MutableStateFlow<List<Scenario>>(emptyList())
    val currentScenarios: StateFlow<List<Scenario>> = _currentScenarios.asStateFlow()

    private val _playerState = MutableStateFlow(initialState)
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private val _currentStepIndex = MutableStateFlow(0)
    val currentStepIndex: StateFlow<Int> = _currentStepIndex.asStateFlow()

    private val _gameFinished = MutableStateFlow(false)
    val gameFinished: StateFlow<Boolean> = _gameFinished.asStateFlow()

    private val _finalResult = MutableStateFlow("")
    val finalResult: StateFlow<String> = _finalResult.asStateFlow()

    private val _sessionHistory = MutableStateFlow<List<SessionResult>>(emptyList())
    val sessionHistory: StateFlow<List<SessionResult>> = _sessionHistory.asStateFlow()

    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak.asStateFlow()

    private val _bestStreak = MutableStateFlow(0)
    val bestStreak: StateFlow<Int> = _bestStreak.asStateFlow()

    private val _decisionPath = MutableStateFlow<List<Int>>(emptyList())
    val decisionPath: StateFlow<List<Int>> = _decisionPath.asStateFlow()

    private val _achievements = MutableStateFlow<List<Achievement>>(emptyList())
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()

    private val _newlyUnlocked = MutableStateFlow<Set<String>>(emptySet())
    val newlyUnlocked: StateFlow<Set<String>> = _newlyUnlocked.asStateFlow()

    private val _apiConnectionError = MutableStateFlow(false)
    val apiConnectionError: StateFlow<Boolean> = _apiConnectionError.asStateFlow()

    private var knownUnlockedAchievementIds: Set<String> = emptySet()

    init {
        viewModelScope.launch {
            AuthManager.currentUser.collect { user ->
                if (user != null) {
                    setUser(user, user.profileImageUri?.toString())
                    // Al cambiar de usuario, sincronizar
                    loadSessionsFromApi()
                } else {
                    // Limpiar el perfil al cerrar sesión
                    _userProfile.value = UserProfile(
                        name = context.getString(R.string.guest_user_name),
                        email = "invitado@mindtrack.ni",
                        memberSince = SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date()),
                        isPremium = false
                    )
                }
            }
        }

        // Observar base de datos local (Room) reactivamente
        viewModelScope.launch {
            sessionRepository.allSessions.collect { localSessions ->
                _sessionHistory.value = localSessions
                recalculateStreaks()
                recalculateAchievements()
            }
        }

        // Leer el flag de DataStore y exponerlo
        viewModelScope.launch {
            onboardingPreferences.onboardingCompletedFlow.collectLatest { completed ->
                _onboardingCompleted.value = completed
            }
        }
        viewModelScope.launch {
            onboardingPreferences.notificationsEnabledFlow.collectLatest { enabled ->
                _notificationsEnabled.value = enabled
            }
        }
        loadSessionsFromApi()
        generateRandomScenarios()
    }

    private fun generateRandomScenarios() {
        val randomPool = scenarioPool.filter { it.id != 5 }.shuffled()
        val selected = randomPool.take(2) + scenarioPool.first { it.id == 5 }
        _currentScenarios.value = selected
    }

    fun loadSessionsFromApi() {
        viewModelScope.launch {
            val currentUserId = AuthManager.currentUser.value?.id ?: 1L
            
            // Sincronizar API -> Room
            val syncResult = sessionRepository.syncSessions(currentUserId)
            _apiConnectionError.value = syncResult is Result.Error

            // Cargar Estadísticas Reales del API
            when (val statsResult = userRepository.getEstadistica(currentUserId)) {
                is Result.Success -> {
                    _currentStreak.value = statsResult.data.rachaActual
                }
                else -> {}
            }
        }
    }

    fun resetSession() {
        _playerState.value = initialState
        _currentStepIndex.value = 0
        _gameFinished.value = false
        _finalResult.value = ""
        _decisionPath.value = emptyList()
        generateRandomScenarios()
    }

    fun consumeNewlyUnlocked(id: String) {
        _newlyUnlocked.value = _newlyUnlocked.value - id
    }

    fun selectOption(option: Option) {
        if (_gameFinished.value) return
        
        val scenarios = _currentScenarios.value
        val currentScenario = scenarios.getOrNull(_currentStepIndex.value) ?: return

        // Aplicar los efectos de la opción
        _playerState.value = gameEngine.applyOption(_playerState.value, option)
        
        // Registrar la decisión tomada
        _decisionPath.value = _decisionPath.value + (currentScenario.id)

        // Determinar siguiente acción
        val nextStep = _currentStepIndex.value + 1
        if (nextStep < scenarios.size && option.nextScenarioId != null) {
            _currentStepIndex.value = nextStep
        } else {
            finishSimulation()
        }
    }

    private fun finishSimulation() {
        val result = gameEngine.evaluateFinal(_playerState.value, _decisionPath.value)
        _finalResult.value = result
        _gameFinished.value = true

        val dateString = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        val newResult = SessionResult(
            id = UUID.randomUUID().toString(),
            finalResult = result,
            date = dateString,
            finalState = _playerState.value.copy(),
            choicesMade = _decisionPath.value.size,
            path = _decisionPath.value
        )
        
        // Enviar notificación de simulación terminada
        notificationHelper.showSimulationFinishedNotification(result)

        // Guardar en la API y en Room
        viewModelScope.launch {
            val currentUserId = AuthManager.currentUser.value?.id ?: 1L
            
            // 1. Guardar localmente en Room (CRUD: Create)
            sessionRepository.insertLocalSession(newResult)

            // 2. Guardar en API
            val sessionDto = TrackSessionDto(
                id = null,
                idUsuario = currentUserId,
                estadoAnimo = result,
                notas = dateString
            )
            sessionRepository.createSession(sessionDto)

            val statsDto = EstadisticaDto(
                id = null,
                idUsuario = currentUserId,
                sesionesCompletadas = _sessionHistory.value.size,
                rachaActual = _currentStreak.value
            )
            userRepository.updateEstadistica(statsDto)

            _achievements.value.filter { it.unlocked }.forEach { localAchievement ->
                val logroDto = LogroDto(
                    id = null,
                    idUsuario = currentUserId,
                    titulo = localAchievement.name,
                    desbloqueado = true
                )
                userRepository.unlockLogro(logroDto)
            }
        }
    }

    fun filterSessionsByPeriod(period: String): List<SessionResult> {
        val now = System.currentTimeMillis()
        val cutoff = when (period) {
            "Semana" -> now - 7L * 24 * 60 * 60 * 1000
            "Mes" -> now - 30L * 24 * 60 * 60 * 1000
            "Año" -> now - 365L * 24 * 60 * 60 * 1000
            else -> 0L
        }
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        return _sessionHistory.value.filter {
            runCatching { formatter.parse(it.date)?.time ?: 0L }.getOrDefault(0L) >= cutoff
        }
    }

    private fun recalculateStreaks() {
        val uniqueDates = _sessionHistory.value
            .mapNotNull { parseSessionDate(it.date) }
            .toSet()

        _currentStreak.value = calculateCurrentStreak(uniqueDates)
        _bestStreak.value = calculateBestStreak(uniqueDates)
    }

    private fun recalculateAchievements() {
        val history = _sessionHistory.value.sortedBy { parseSessionMillis(it.date) }
        val achievements = buildAchievements(history)
        val unlockedIds = achievements.filter { it.unlocked }.mapTo(mutableSetOf()) { it.id }
        val newlyUnlockedIds = unlockedIds - knownUnlockedAchievementIds

        if (newlyUnlockedIds.isNotEmpty()) {
            _newlyUnlocked.value = _newlyUnlocked.value + newlyUnlockedIds
            
            // Enviar notificaciones para nuevos logros
            newlyUnlockedIds.forEach { id ->
                achievements.find { it.id == id }?.let { achievement ->
                    notificationHelper.showAchievementUnlockedNotification(achievement.name)
                }
            }
        }

        _achievements.value = achievements
        knownUnlockedAchievementIds = unlockedIds
    }

    private fun buildAchievements(history: List<SessionResult>): List<Achievement> {
        val totalSessions = history.size
        val totalDecisions = history.sumOf { it.choicesMade }
        val bestStreakValue = _bestStreak.value
        val successSessions = history.count { it.finalResult.contains("Éxito", ignoreCase = true) || it.finalResult.contains("Success", ignoreCase = true) }
        val highProgressSessions = history.count { it.finalState.progress >= 80 }
        val masterySessions = history.count { it.finalState.progress >= 70 && it.finalState.stress <= 40 }
        val distinctPaths = history.map { it.path.joinToString(separator = "-") }.toSet().size

        val distinctScenarios = mutableSetOf<Int>()
        var explorationUnlockDate: String? = null
        history.forEach { session ->
            distinctScenarios.addAll(session.path)
            if (explorationUnlockDate == null && distinctScenarios.size >= 5) {
                explorationUnlockDate = session.date
            }
        }

        val newestHistory = history

        return listOf(
            achievement(
                id = "first_step",
                name = context.getString(R.string.ach_first_step_name),
                description = context.getString(R.string.ach_first_step_desc),
                emoji = "🚀",
                category = AchievementCategory.CONSTANCIA,
                unlocked = totalSessions >= 1,
                progress = progressPercent(totalSessions, 1),
                unlockedDate = firstSessionDateAtOrAfter(newestHistory, 1)
            ),
            achievement(
                id = "steady_3",
                name = context.getString(R.string.ach_steady_3_name),
                description = context.getString(R.string.ach_steady_3_desc),
                emoji = "🎯",
                category = AchievementCategory.CONSTANCIA,
                unlocked = totalSessions >= 3,
                progress = progressPercent(totalSessions, 3),
                unlockedDate = firstSessionDateAtOrAfter(newestHistory, 3)
            ),
            achievement(
                id = "streak_7",
                name = context.getString(R.string.ach_streak_7_name),
                description = context.getString(R.string.ach_streak_7_desc),
                emoji = "⚡",
                category = AchievementCategory.CONSTANCIA,
                unlocked = bestStreakValue >= 7,
                progress = progressPercent(bestStreakValue, 7),
                unlockedDate = firstStreakDateAtOrAfter(history, 7)
            ),
            achievement(
                id = "unstoppable",
                name = context.getString(R.string.ach_unstoppable_name),
                description = context.getString(R.string.ach_unstoppable_desc),
                emoji = "🔥",
                category = AchievementCategory.CONSTANCIA,
                unlocked = bestStreakValue >= 14,
                progress = progressPercent(bestStreakValue, 14),
                unlockedDate = firstStreakDateAtOrAfter(history, 14)
            ),
            achievement(
                id = "thinker",
                name = context.getString(R.string.ach_thinker_name),
                description = context.getString(R.string.ach_thinker_desc),
                emoji = "🧠",
                category = AchievementCategory.MAESTRIA,
                unlocked = successSessions >= 2,
                progress = progressPercent(successSessions, 2),
                unlockedDate = firstDateWhenCountReached(history, 2) { it.finalResult.contains("Éxito", ignoreCase = true) || it.finalResult.contains("Success", ignoreCase = true) }
            ),
            achievement(
                id = "strategist",
                name = context.getString(R.string.ach_strategist_name),
                description = context.getString(R.string.ach_strategist_desc),
                emoji = "♟️",
                category = AchievementCategory.MAESTRIA,
                unlocked = highProgressSessions >= 3,
                progress = progressPercent(highProgressSessions, 3),
                unlockedDate = firstDateWhenCountReached(history, 3) { it.finalState.progress >= 80 }
            ),
            achievement(
                id = "master",
                name = context.getString(R.string.ach_master_name),
                description = context.getString(R.string.ach_master_desc),
                emoji = "🏆",
                category = AchievementCategory.MAESTRIA,
                unlocked = masterySessions >= 5,
                progress = progressPercent(masterySessions, 5),
                unlockedDate = firstDateWhenCountReached(history, 5) { it.finalState.progress >= 70 && it.finalState.stress <= 40 }
            ),
            achievement(
                id = "visionary",
                name = context.getString(R.string.ach_visionary_name),
                description = context.getString(R.string.ach_visionary_desc),
                emoji = "✨",
                category = AchievementCategory.MAESTRIA,
                unlocked = totalDecisions >= 25,
                progress = progressPercent(totalDecisions, 25),
                unlockedDate = firstDateWhenDecisionCountReached(history)
            ),
            achievement(
                id = "explorer",
                name = context.getString(R.string.ach_explorer_name),
                description = context.getString(R.string.ach_explorer_desc),
                emoji = "🗺️",
                category = AchievementCategory.EXPLORACION,
                unlocked = distinctPaths >= 3,
                progress = progressPercent(distinctPaths, 3),
                unlockedDate = firstDateWhenDistinctPathReached(history, 3)
            ),
            achievement(
                id = "cartographer",
                name = context.getString(R.string.ach_cartographer_name),
                description = context.getString(R.string.ach_cartographer_desc),
                emoji = "🧭",
                category = AchievementCategory.EXPLORACION,
                unlocked = distinctPaths >= 5,
                progress = progressPercent(distinctPaths, 5),
                unlockedDate = firstDateWhenDistinctPathReached(history, 5)
            ),
            achievement(
                id = "curious",
                name = context.getString(R.string.ach_curious_name),
                description = context.getString(R.string.ach_curious_desc),
                emoji = "🔍",
                category = AchievementCategory.EXPLORACION,
                unlocked = distinctScenarios.size >= 5,
                progress = progressPercent(distinctScenarios.size, 5),
                unlockedDate = explorationUnlockDate
            ),
            achievement(
                id = "chronicle",
                name = context.getString(R.string.ach_chronicle_name),
                description = context.getString(R.string.ach_chronicle_desc),
                emoji = "📖",
                category = AchievementCategory.EXPLORACION,
                unlocked = totalSessions >= 12,
                progress = progressPercent(totalSessions, 12),
                unlockedDate = firstSessionDateAtOrAfter(newestHistory, 12)
            )
        )
    }

    private fun achievement(
        id: String,
        name: String,
        description: String,
        emoji: String,
        category: AchievementCategory,
        unlocked: Boolean,
        progress: Int,
        unlockedDate: String?
    ): Achievement {
        return Achievement(
            id = id,
            name = name,
            description = description,
            emoji = emoji,
            category = category,
            unlocked = unlocked,
            progress = progress.coerceIn(0, 100),
            unlockedDate = unlockedDate
        )
    }

    private fun progressPercent(current: Int, threshold: Int): Int {
        if (threshold <= 0) return 0
        return ((current.toFloat() / threshold.toFloat()) * 100f).toInt().coerceIn(0, 100)
    }

    private fun firstSessionDateAtOrAfter(history: List<SessionResult>, threshold: Int): String? {
        if (history.isEmpty()) return null
        return history.getOrNull(threshold - 1)?.date
    }

    private fun firstDateWhenCountReached(
        history: List<SessionResult>,
        threshold: Int,
        predicate: (SessionResult) -> Boolean
    ): String? {
        if (threshold <= 0) return null
        var count = 0
        history.forEach { session ->
            if (predicate(session)) {
                count++
                if (count >= threshold) return session.date
            }
        }
        return null
    }

    private fun firstDateWhenDecisionCountReached(history: List<SessionResult>): String? {
        var count = 0
        history.forEach { session ->
            count += session.choicesMade
            if (count >= 25) return session.date
        }
        return null
    }

    private fun firstDateWhenDistinctPathReached(history: List<SessionResult>, threshold: Int): String? {
        if (threshold <= 0) return null
        val seen = mutableSetOf<String>()
        history.forEach { session ->
            if (seen.add(session.path.joinToString(separator = "-")) && seen.size >= threshold) {
                return session.date
            }
        }
        return null
    }

    private fun firstStreakDateAtOrAfter(history: List<SessionResult>, threshold: Int): String? {
        if (threshold <= 0) return null
        val uniqueDates = history
            .mapNotNull { parseSessionDate(it.date) }
            .distinct()
            .sorted()

        if (uniqueDates.isEmpty()) return null

        var streak = 1
        for (index in 1 until uniqueDates.size) {
            streak = if (uniqueDates[index] == uniqueDates[index - 1].plusDays(1)) {
                streak + 1
            } else {
                1
            }
            if (streak >= threshold) {
                val targetDate = uniqueDates[index]
                return history.firstOrNull { parseSessionDate(it.date) == targetDate }?.date
            }
        }
        return null
    }

    private fun parseSessionDate(date: String): LocalDate? {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return runCatching {
            val parsed = formatter.parse(date) ?: return null
            parsed.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        }.getOrNull()
    }

    private fun parseSessionMillis(date: String): Long {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return runCatching { formatter.parse(date)?.time ?: 0L }.getOrDefault(0L)
    }

    private fun calculateCurrentStreak(dates: Set<LocalDate>): Int {
        if (dates.isEmpty()) return 0

        var streak = 0
        var cursor = LocalDate.now()

        while (dates.contains(cursor)) {
            streak += 1
            cursor = cursor.minusDays(1)
        }

        return streak
    }

    private fun calculateBestStreak(dates: Set<LocalDate>): Int {
        if (dates.isEmpty()) return 0

        val sortedDates = dates.sorted()
        var best = 1
        var current = 1

        for (index in 1 until sortedDates.size) {
            current = if (sortedDates[index] == sortedDates[index - 1].plusDays(1)) {
                current + 1
            } else {
                1
            }
            best = maxOf(best, current)
        }

        return best
    }

    fun getCurrentScenario(): Scenario? {
        return _currentScenarios.value.getOrNull(_currentStepIndex.value)
    }

    fun getScenarioProgress(): Float {
        val totalSteps = _currentScenarios.value.size.toFloat()
        val stepsTaken = _decisionPath.value.size.toFloat()
        return (stepsTaken + 1) / (totalSteps + 1)
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            onboardingPreferences.setOnboardingCompleted(true)
            _onboardingCompleted.value = true
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            onboardingPreferences.setNotificationsEnabled(enabled)
            _notificationsEnabled.value = enabled
        }
    }

    fun logout() {
        resetSession()
    }

    fun deleteSession(session: SessionResult) {
        viewModelScope.launch {
            sessionRepository.deleteLocalSession(session)
        }
    }

    fun setUser(user: ni.edu.uam.mindtrack.model.User, profileImageUri: String? = null) {
        _userProfile.value = UserProfile(
            name = user.fullName,
            email = user.email,
            memberSince = SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date()),
            isPremium = false,
            profileImageUri = profileImageUri ?: user.profileImageUri?.toString()
        )
        _avatarUri.value = (profileImageUri ?: user.profileImageUri?.toString())?.let { Uri.parse(it) }
    }

    fun updateProfile(newProfile: UserProfile): Boolean {
        val currentUser = AuthManager.currentUser.value ?: return false
        val updated = AuthManager.updateProfile(
            fullName = newProfile.name,
            email = newProfile.email,
            profileImageUri = newProfile.profileImageUri?.let(Uri::parse)
        )
        if (!updated) return false

        _userProfile.value = newProfile
        _avatarUri.value = newProfile.profileImageUri?.let(Uri::parse)
        return true
    }

    fun loginWithApi(email: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val userIdToTry = 1L 
            
            when (val result = userRepository.getUsuario(userIdToTry)) {
                is Result.Success -> {
                    val userDto = result.data
                    val localUser = ni.edu.uam.mindtrack.model.User(
                        id = userDto.id,
                        fullName = userDto.nombre,
                        email = userDto.correo,
                        password = "" 
                    )
                    AuthManager.loginSilently(localUser)
                    setUser(localUser, userDto.fotoPerfil)
                    onResult(true, null)
                }
                is Result.Error -> {
                    registerWithApi("Usuario MindTrack", email, null, onResult)
                }
                else -> {}
            }
        }
    }

    fun registerWithApi(name: String, email: String, photoUri: Uri?, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val newUserDto = UserDto(
                id = null,
                nombre = name,
                correo = email,
                fotoPerfil = photoUri?.toString(),
                biografia = "Nueva cuenta"
            )
            when (val regResult = userRepository.registerUsuario(newUserDto)) {
                is Result.Success -> {
                    val registeredDto = regResult.data
                    val localUser = ni.edu.uam.mindtrack.model.User(
                        id = registeredDto.id,
                        fullName = registeredDto.nombre,
                        email = registeredDto.correo,
                        password = ""
                    )
                    AuthManager.loginSilently(localUser)
                    setUser(localUser, registeredDto.fotoPerfil)
                    onResult(true, null)
                }
                is Result.Error -> onResult(false, regResult.message)
                else -> {}
            }
        }
    }
}
