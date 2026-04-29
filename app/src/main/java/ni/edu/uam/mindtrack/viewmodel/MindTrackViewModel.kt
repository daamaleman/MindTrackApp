package ni.edu.uam.mindtrack.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ni.edu.uam.mindtrack.engine.DecisionEngine
import ni.edu.uam.mindtrack.model.Option
import ni.edu.uam.mindtrack.model.Scenario
import ni.edu.uam.mindtrack.model.SessionResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class MindTrackViewModel : ViewModel() {
    private val decisionEngine = DecisionEngine()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleTheme() {
        _isDarkMode.value = !_isDarkMode.value
    }

    val scenarios = listOf(
        Scenario(
            id = 1,
            category = "💼 Trabajo & Carrera",
            emoji = "💼",
            question = "Te ofrecen un aumento del 30% pero debes mudarte en 2 semanas. ¿Qué haces?",
            options = listOf(
                Option("Analizo pros y contras detalladamente antes de decidir", "rational"),
                Option("Acepto de inmediato, es demasiado bueno para rechazarlo", "impulsive"),
                Option("Negocio más tiempo para tomar la decisión con calma", "balanced")
            )
        ),
        Scenario(
            id = 2,
            category = "💰 Finanzas",
            emoji = "💰",
            question = "Recibes una herencia inesperada de $10,000. ¿Cómo actúas?",
            options = listOf(
                Option("La invierto en un portafolio diversificado tras investigar opciones", "rational"),
                Option("La gasto en un viaje que siempre quise hacer, ¡YOLO!", "impulsive"),
                Option("Ahorro la mitad y uso el resto en algo significativo para mí", "balanced")
            )
        ),
        Scenario(
            id = 3,
            category = "❤️ Relaciones",
            emoji = "❤️",
            question = "Tu mejor amigo te pide un favor enorme que te incomoda. ¿Qué haces?",
            options = listOf(
                Option("Evalúo el impacto en mí antes de dar una respuesta definitiva", "rational"),
                Option("Digo que sí de inmediato para no decepcionarlo", "impulsive"),
                Option("Hablo honestamente sobre mis limitaciones y busco un punto medio", "balanced")
            )
        )
    )

    private val _currentScenarioIndex = MutableStateFlow(0)
    val currentScenarioIndex: StateFlow<Int> = _currentScenarioIndex.asStateFlow()

    private val _userChoices = mutableListOf<String>()
    
    private val _sessionHistory = MutableStateFlow<List<SessionResult>>(emptyList())
    val sessionHistory: StateFlow<List<SessionResult>> = _sessionHistory.asStateFlow()

    private val _currentResult = MutableStateFlow("")
    val currentResult: StateFlow<String> = _currentResult.asStateFlow()

    fun selectOption(type: String) {
        _userChoices.add(type)
    }

    fun nextScenario(): Boolean {
        if (_currentScenarioIndex.value < scenarios.size - 1) {
            _currentScenarioIndex.value += 1
            return true
        }
        return false
    }

    fun finishSession() {
        val profile = decisionEngine.analyze(_userChoices)
        _currentResult.value = profile
        
        val newResult = SessionResult(
            id = UUID.randomUUID().toString(),
            profile = profile,
            date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()),
            decisionCount = _userChoices.size
        )
        _sessionHistory.value = listOf(newResult) + _sessionHistory.value
    }

    fun resetSession() {
        _currentScenarioIndex.value = 0
        _userChoices.clear()
        _currentResult.value = ""
    }
    
    fun getDistribution(): Map<String, Float> {
        val total = _userChoices.size.toFloat()
        if (total == 0f) return emptyMap()
        
        val counts = _userChoices.groupingBy { it }.eachCount()
        return mapOf(
            "Racional" to (counts.getOrDefault("rational", 0).toFloat() / total),
            "Impulsivo" to (counts.getOrDefault("impulsive", 0).toFloat() / total),
            "Equilibrado" to (counts.getOrDefault("balanced", 0).toFloat() / total)
        )
    }
}
