package ni.edu.uam.mindtrack.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ni.edu.uam.mindtrack.engine.GameEngine
import ni.edu.uam.mindtrack.model.Achievement
import ni.edu.uam.mindtrack.model.AchievementCategory
import ni.edu.uam.mindtrack.model.Option
import ni.edu.uam.mindtrack.model.Scenario
import ni.edu.uam.mindtrack.model.PlayerState
import ni.edu.uam.mindtrack.model.SessionResult
import java.time.LocalDate
import java.time.ZoneId
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class MindTrackViewModel : ViewModel() {
    private val gameEngine = GameEngine()

    private val initialState = PlayerState(
        energy = 80,
        stress = 20,
        progress = 0,
        money = 100
    )

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleTheme() {
        _isDarkMode.value = !_isDarkMode.value
    }

    val scenarios = listOf(
        Scenario(
            id = 1,
            title = "Trabajo",
            question = "Te ofrecen un proyecto de alta prioridad con un plazo muy corto. ¿Cómo procedes?",
            options = listOf(
                Option("Aceptar y trabajar horas extra", 2, -20, 15, 25, 30),
                Option("Negociar plazos más largos", 3, -5, 5, 10, 10),
                Option("Rechazar para evitar estrés", 4, 10, -10, 0, -5)
            )
        ),
        Scenario(
            id = 2,
            title = "Crisis",
            question = "El proyecto tiene errores críticos a mitad de camino. ¿Qué haces?",
            options = listOf(
                Option("Resolverlo solo sin dormir", 5, -30, 25, 30, 0),
                Option("Pedir ayuda al equipo", 5, -10, 10, 20, -10),
                Option("Ignorar y esperar que no se note", null, -5, 20, -20, -20)
            )
        ),
        Scenario(
            id = 3,
            title = "Estudio",
            question = "Tienes un examen importante mañana pero estás agotado.",
            options = listOf(
                Option("Estudiar toda la noche", 5, -25, 20, 25, 0),
                Option("Repasar y dormir temprano", 5, 10, -10, 15, 0),
                Option("No estudiar y confiar en la suerte", null, 15, -15, -10, 0)
            )
        ),
        Scenario(
            id = 4,
            title = "Social",
            question = "Tus amigos te invitan a salir, pero tienes gastos pendientes.",
            options = listOf(
                Option("Ir y gastar en grande", 5, 15, -10, -5, -40),
                Option("Ir y controlar el gasto", 5, 5, -5, 0, -15),
                Option("Quedarte en casa ahorrando", 5, 10, -5, 5, 0)
            )
        ),
        Scenario(
            id = 5,
            title = "Decisión Final",
            question = "¿Cómo quieres cerrar este ciclo?",
            options = listOf(
                Option("Invertir en crecimiento personal", null, -10, 5, 20, -30),
                Option("Tomar unas vacaciones merecidas", null, 30, -25, 0, -40),
                Option("Seguir trabajando duro", null, -20, 15, 15, 20)
            )
        )
    )

    private val _playerState = MutableStateFlow(initialState)
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private val _currentScenarioId = MutableStateFlow(1)
    val currentScenarioId: StateFlow<Int> = _currentScenarioId.asStateFlow()

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

    private var knownUnlockedAchievementIds: Set<String> = emptySet()

    init {
        recalculateStreaks()
        recalculateAchievements()
    }

    fun resetSession() {
        _playerState.value = initialState
        _currentScenarioId.value = 1
        _gameFinished.value = false
        _finalResult.value = ""
        _decisionPath.value = emptyList()
    }

    fun consumeNewlyUnlocked(id: String) {
        _newlyUnlocked.value = _newlyUnlocked.value - id
    }

    fun selectOption(option: Option) {
        if (_gameFinished.value) return

        _playerState.value = gameEngine.applyOption(_playerState.value, option)
        _decisionPath.value = _decisionPath.value + (_currentScenarioId.value)

        if (option.nextScenarioId == null) {
            finishSimulation()
        } else {
            _currentScenarioId.value = option.nextScenarioId
        }
    }

    private fun finishSimulation() {
        val result = gameEngine.evaluateFinal(_playerState.value, _decisionPath.value)
        _finalResult.value = result
        _gameFinished.value = true

        val newResult = SessionResult(
            id = UUID.randomUUID().toString(),
            finalResult = result,
            date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()),
            finalState = _playerState.value.copy(),
            choicesMade = _decisionPath.value.size,
            path = _decisionPath.value
        )
        _sessionHistory.value = listOf(newResult) + _sessionHistory.value
        recalculateStreaks()
        recalculateAchievements()
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
        }

        _achievements.value = achievements
        knownUnlockedAchievementIds = unlockedIds
    }

    private fun buildAchievements(history: List<SessionResult>): List<Achievement> {
        val totalSessions = history.size
        val totalDecisions = history.sumOf { it.choicesMade }
        val bestStreakValue = _bestStreak.value
        val successSessions = history.count { it.finalResult.contains("Éxito", ignoreCase = true) }
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
                name = "Primer paso",
                description = "Completa tu primera simulación.",
                emoji = "🌱",
                category = AchievementCategory.CONSTANCIA,
                unlocked = totalSessions >= 1,
                progress = progressPercent(totalSessions, 1),
                unlockedDate = firstSessionDateAtOrAfter(newestHistory, 1)
            ),
            achievement(
                id = "steady_3",
                name = "Ritmo de 3",
                description = "Mantén tres simulaciones completadas.",
                emoji = "🔥",
                category = AchievementCategory.CONSTANCIA,
                unlocked = totalSessions >= 3,
                progress = progressPercent(totalSessions, 3),
                unlockedDate = firstSessionDateAtOrAfter(newestHistory, 3)
            ),
            achievement(
                id = "streak_7",
                name = "Racha de 7",
                description = "Consigue 7 días seguidos con actividad.",
                emoji = "⚡",
                category = AchievementCategory.CONSTANCIA,
                unlocked = bestStreakValue >= 7,
                progress = progressPercent(bestStreakValue, 7),
                unlockedDate = firstStreakDateAtOrAfter(history, 7)
            ),
            achievement(
                id = "unstoppable",
                name = "Imparable",
                description = "Alcanza 14 días seguidos de constancia.",
                emoji = "🏆",
                category = AchievementCategory.CONSTANCIA,
                unlocked = bestStreakValue >= 14,
                progress = progressPercent(bestStreakValue, 14),
                unlockedDate = firstStreakDateAtOrAfter(history, 14)
            ),
            achievement(
                id = "thinker",
                name = "Pensador",
                description = "Consigue resultados de Éxito equilibrado.",
                emoji = "🧠",
                category = AchievementCategory.MAESTRIA,
                unlocked = successSessions >= 2,
                progress = progressPercent(successSessions, 2),
                unlockedDate = firstDateWhenCountReached(history, 2) { it.finalResult.contains("Éxito", ignoreCase = true) }
            ),
            achievement(
                id = "strategist",
                name = "Estratega",
                description = "Completa sesiones con progreso alto.",
                emoji = "♟️",
                category = AchievementCategory.MAESTRIA,
                unlocked = highProgressSessions >= 3,
                progress = progressPercent(highProgressSessions, 3),
                unlockedDate = firstDateWhenCountReached(history, 3) { it.finalState.progress >= 80 }
            ),
            achievement(
                id = "master",
                name = "Maestro",
                description = "Mantén el progreso alto con poco estrés.",
                emoji = "🎯",
                category = AchievementCategory.MAESTRIA,
                unlocked = masterySessions >= 5,
                progress = progressPercent(masterySessions, 5),
                unlockedDate = firstDateWhenCountReached(history, 5) { it.finalState.progress >= 70 && it.finalState.stress <= 40 }
            ),
            achievement(
                id = "visionary",
                name = "Visionario",
                description = "Acumula 25 decisiones en total.",
                emoji = "✨",
                category = AchievementCategory.MAESTRIA,
                unlocked = totalDecisions >= 25,
                progress = progressPercent(totalDecisions, 25),
                unlockedDate = firstDateWhenDecisionCountReached(history)
            ),
            achievement(
                id = "explorer",
                name = "Explorador",
                description = "Recorre al menos 3 rutas distintas.",
                emoji = "🧭",
                category = AchievementCategory.EXPLORACION,
                unlocked = distinctPaths >= 3,
                progress = progressPercent(distinctPaths, 3),
                unlockedDate = firstDateWhenDistinctPathReached(history, 3)
            ),
            achievement(
                id = "cartographer",
                name = "Cartógrafo",
                description = "Descubre 5 rutas distintas.",
                emoji = "🗺️",
                category = AchievementCategory.EXPLORACION,
                unlocked = distinctPaths >= 5,
                progress = progressPercent(distinctPaths, 5),
                unlockedDate = firstDateWhenDistinctPathReached(history, 5)
            ),
            achievement(
                id = "curious",
                name = "Curioso",
                description = "Visita los 5 escenarios del juego.",
                emoji = "👀",
                category = AchievementCategory.EXPLORACION,
                unlocked = distinctScenarios.size >= 5,
                progress = progressPercent(distinctScenarios.size, 5),
                unlockedDate = explorationUnlockDate
            ),
            achievement(
                id = "chronicle",
                name = "Cronista",
                description = "Completa 12 simulaciones.",
                emoji = "📜",
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
        return scenarios.find { it.id == _currentScenarioId.value }
    }

    fun getScenarioProgress(): Float {
        val totalSteps = 4f 
        val stepsTaken = _decisionPath.value.size.toFloat()
        return (stepsTaken + 1) / (totalSteps + 1)
    }
}
