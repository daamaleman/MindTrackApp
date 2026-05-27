package ni.edu.uam.mindtrack.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ni.edu.uam.mindtrack.engine.GameEngine
import ni.edu.uam.mindtrack.model.Option
import ni.edu.uam.mindtrack.model.Scenario
import ni.edu.uam.mindtrack.model.PlayerState
import ni.edu.uam.mindtrack.model.SessionResult
import ni.edu.uam.mindtrack.model.UserProfile
import ni.edu.uam.mindtrack.model.Achievement
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

    private val _userProfile = MutableStateFlow(
        UserProfile(
            name = "Invitado",
            email = "invitado@mindtrack.ni",
            memberSince = SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date()),
            isPremium = false
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _achievements = MutableStateFlow(
        listOf(
            Achievement("1", "Primer paso", "Completa tu primera simulación", true, "🎯"),
            Achievement("2", "Analista", "Obtén un resultado racional", false, "🧠"),
            Achievement("3", "Estratega", "Obtén un resultado estratégico", true, "⚖️"),
            Achievement("4", "Constancia", "Realiza simulaciones 3 días seguidos", true, "🔥"),
            Achievement("5", "Maestro", "Desbloquea todos los perfiles", false, "👑")
        )
    )
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()

    private val _avatarUri = MutableStateFlow<Uri?>(null)
    val avatarUri: StateFlow<Uri?> = _avatarUri.asStateFlow()

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

    private val _decisionPath = MutableStateFlow<List<Int>>(emptyList())
    val decisionPath: StateFlow<List<Int>> = _decisionPath.asStateFlow()

    fun resetSession() {
        _playerState.value = initialState
        _currentScenarioId.value = 1
        _gameFinished.value = false
        _finalResult.value = ""
        _decisionPath.value = emptyList()
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
    }

    fun getCurrentScenario(): Scenario? {
        return scenarios.find { it.id == _currentScenarioId.value }
    }

    fun getScenarioProgress(): Float {
        val totalSteps = 4f 
        val stepsTaken = _decisionPath.value.size.toFloat()
        return (stepsTaken + 1) / (totalSteps + 1)
    }

    fun logout() {
        // En una app real limpiaríamos tokens, etc.
        resetSession()
    }

    fun setUser(user: ni.edu.uam.mindtrack.model.User, profileImageUri: String? = null) {
        _userProfile.value = UserProfile(
            name = user.fullName,
            email = user.email,
            memberSince = SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date()),
            isPremium = false,
            profileImageUri = profileImageUri
        )
        _avatarUri.value = profileImageUri?.let { Uri.parse(it) }
    }

    fun updateProfile(newProfile: UserProfile) {
        _userProfile.value = newProfile
    }

    fun setAvatarUri(uri: Uri?) {
        _avatarUri.value = uri
    }
}
