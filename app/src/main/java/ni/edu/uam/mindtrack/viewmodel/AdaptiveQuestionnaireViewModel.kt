package ni.edu.uam.mindtrack.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ni.edu.uam.mindtrack.R
import java.util.*
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.sqrt

// Un ViewModel independiente que implementa un motor de cuestionario adaptativo simple.
// Contiene modelo de estado, banco de preguntas y reglas básicas de adaptación.

enum class QuestionType { LIKERT, MULTIPLE_CHOICE, FORCED_CHOICE, OPEN, SCENARIO }

data class Question(
    val id: String,
    val type: QuestionType,
    val text: String,
    val options: List<String> = emptyList(),
    val weights: Map<String, Double> = emptyMap(),
    val optionWeights: List<Map<String, Double>> = emptyList(),
    val tags: List<String> = emptyList(),
    val followUpRules: List<(TraitState) -> String?> = emptyList()
)

data class TraitState(
    val scores: MutableMap<String, Double> = mutableMapOf(),
    val supports: MutableMap<String, Int> = mutableMapOf(),
    val conflicts: MutableMap<String, Int> = mutableMapOf()
)

data class ProfileReport(
    val mainLabel: String,
    val traitScores: Map<String, Double>,
    val confidences: Map<String, Double>,
    val strengths: List<String>,
    val growthAreas: List<String>,
    val patterns: List<String>,
    val globalConfidence: Double
)

class AdaptiveQuestionnaireViewModel(context: Context) : ViewModel() {
    // Rasgos iniciales (0..100, 50 = neutral)
    private val initialTraits = listOf(
        "Extraversion",
        "EmotionalStability",
        "Empathy",
        "Leadership",
        "Impulsivity",
        "Creativity",
        "Analytical",
        "RiskTolerance",
        "DecisionStyle",
        "SelfEsteem"
    )

    var traitState by mutableStateOf(TraitState())
        private set

    // preguntas ya realizadas
    val askedQuestions = mutableStateListOf<String>()

    // conteo de cuántas preguntas han afectado a cada rasgo
    private val traitQuestionCount: MutableMap<String, Int> = mutableMapOf()

    // confianza por rasgo (0..1)
    val confidencePerTrait: MutableMap<String, Double> = mutableMapOf()

    // historial simple
    val history = mutableStateListOf<Triple<String, String, Long>>() // id, answer, ts

    // Cola para follow-ups
    private val pendingFollowUps: Queue<String> = LinkedList()
    // Evitar encolar follow-ups repetidos
    private val followUpEnqueueCount: MutableMap<String, Int> = mutableMapOf()
    private val maxFollowUpsPerId = 1

    // Banco de preguntas
    private val questions: List<Question>

    // Estado UI: pregunta actual (id) y contenido
    var currentQuestion by mutableStateOf<Question?>(null)
        private set

    init {
        // Inicializar rasgos en 50
        for (t in initialTraits) {
            traitState.scores[t] = 50.0
            traitState.supports[t] = 0
            traitState.conflicts[t] = 0
            traitQuestionCount[t] = 0
            confidencePerTrait[t] = 0.5
        }

        questions = buildQuestionBank(context)
        currentQuestion = selectNextQuestion()
    }

    private fun buildQuestionBank(context: Context): List<Question> {
        val bank = mutableListOf<Question>()

        bank += Question(
            id = "Q001",
            type = QuestionType.LIKERT,
            text = context.getString(R.string.q001_text),
            options = listOf("1","2","3","4","5"),
            weights = mapOf("Extraversion" to 1.0)
        )

        bank += Question(
            id = "Q002",
            type = QuestionType.LIKERT,
            text = context.getString(R.string.q002_text),
            options = listOf("1","2","3","4","5"),
            weights = mapOf("Analytical" to 0.9, "Extraversion" to -0.2)
        )

        bank += Question(
            id = "Q004",
            type = QuestionType.LIKERT,
            text = context.getString(R.string.q004_text),
            options = listOf("1","2","3","4","5"),
            weights = mapOf("EmotionalStability" to 1.0)
        )

        bank += Question(
            id = "Q011",
            type = QuestionType.LIKERT,
            text = context.getString(R.string.q011_text),
            options = listOf("1","2","3","4","5"),
            weights = mapOf("Leadership" to 1.0, "Extraversion" to 0.4)
        )

        bank += Question(
            id = "Q021",
            type = QuestionType.LIKERT,
            text = context.getString(R.string.q021_text),
            options = listOf("1","2","3","4","5"),
            weights = mapOf("EmotionalStability" to 1.0)
        )

        bank += Question(
            id = "Q031",
            type = QuestionType.FORCED_CHOICE,
            text = context.getString(R.string.q031_text),
            options = listOf(context.getString(R.string.q031_opt1), context.getString(R.string.q031_opt2)),
            optionWeights = listOf(
                mapOf("RiskTolerance" to 0.8, "Impulsivity" to 0.6),
                mapOf("RiskTolerance" to 0.2, "Impulsivity" to -0.4)
            )
        )

        bank += Question(
            id = "Q032",
            type = QuestionType.LIKERT,
            text = context.getString(R.string.q032_text),
            options = listOf("1","2","3","4","5"),
            weights = mapOf("Impulsivity" to 1.0)
        )

        bank += Question(
            id = "Q041",
            type = QuestionType.MULTIPLE_CHOICE,
            text = context.getString(R.string.q041_text),
            options = listOf(
                context.getString(R.string.q041_opt1),
                context.getString(R.string.q041_opt2),
                context.getString(R.string.q041_opt3),
                context.getString(R.string.q041_opt4)
            ),
            optionWeights = listOf(
                mapOf("Creativity" to 0.8, "Analytical" to 0.2),
                mapOf("Creativity" to 0.2, "Analytical" to 1.0),
                mapOf("Creativity" to 0.9, "Analytical" to 0.4),
                mapOf("Creativity" to 0.1, "Analytical" to -0.2)
            )
        )

        bank += Question(
            id = "Q051",
            type = QuestionType.LIKERT,
            text = context.getString(R.string.q051_text),
            options = listOf("1","2","3","4","5"),
            weights = mapOf("Empathy" to 1.0)
        )

        bank += Question(
            id = "Q061",
            type = QuestionType.LIKERT,
            text = context.getString(R.string.q061_text),
            options = listOf("1","2","3","4","5"),
            weights = mapOf("SelfEsteem" to 1.0)
        )

        bank += Question(
            id = "Q091",
            type = QuestionType.OPEN,
            text = context.getString(R.string.q091_text),
            weights = mapOf("SelfEsteem" to 0.3, "Empathy" to 0.3)
        )

        return bank
    }

    private fun selectNextQuestion(): Question? {
        val nextFollow = synchronized(pendingFollowUps) { pendingFollowUps.poll() }
        if (nextFollow != null) {
            val q = questions.find { it.id == nextFollow }
            if (q != null && !askedQuestions.contains(q.id)) return q
        }

        val trait = traitState.supports.minByOrNull { it.value }?.key
        val candidate = questions.firstOrNull { q ->
            !askedQuestions.contains(q.id) && (
                q.weights.keys.contains(trait) || q.optionWeights.any { it.keys.contains(trait) }
            )
        }
        if (candidate != null) return candidate

        return questions.firstOrNull { !askedQuestions.contains(it.id) }
    }

    fun submitAnswer(questionId: String, answer: String, optionIndex: Int? = null, likertValue: Int? = null) {
        val q = questions.find { it.id == questionId } ?: return
        val ts = System.currentTimeMillis()
        history.add(Triple(questionId, answer, ts))
        askedQuestions.add(questionId)

        val affectedTraits = mutableSetOf<String>()
        affectedTraits.addAll(q.weights.keys)
        q.optionWeights.forEach { affectedTraits.addAll(it.keys) }
        for (t in affectedTraits) traitQuestionCount[t] = (traitQuestionCount[t] ?: 0) + 1

        when (q.type) {
            QuestionType.LIKERT -> {
                val v = likertValue ?: answer.toIntOrNull() ?: 3
                val normalized = (v - 3).toDouble() / 2.0 // -1..1
                for ((trait, weight) in q.weights) {
                    applyTraitDelta(trait, normalized * weight)
                }
            }
            QuestionType.MULTIPLE_CHOICE, QuestionType.FORCED_CHOICE -> {
                val idx = optionIndex ?: answer.toIntOrNull() ?: 0
                val ow = if (idx in q.optionWeights.indices) q.optionWeights[idx] else q.optionWeights.firstOrNull() ?: emptyMap()
                for ((trait, weight) in ow) {
                    applyTraitDelta(trait, weight)
                }
            }
            QuestionType.OPEN, QuestionType.SCENARIO -> {
                val text = answer.lowercase(Locale.getDefault())
                val lenFactor = (text.length.coerceAtMost(200)).toDouble() / 200.0
                for ((trait, weight) in q.weights) {
                    applyTraitDelta(trait, lenFactor * weight * 0.6)
                }
            }
        }

        for (rule in q.followUpRules) {
            val followId = rule(traitState)
            if (followId != null) synchronized(pendingFollowUps) {
                val already = followUpEnqueueCount[followId] ?: 0
                if (already < maxFollowUpsPerId) {
                    pendingFollowUps.add(followId)
                    followUpEnqueueCount[followId] = already + 1
                }
            }
        }

        recomputeConfidences()
        currentQuestion = selectNextQuestion()
    }

    private fun applyTraitDelta(trait: String, deltaRaw: Double) {
        val sensitivity = 20.0
        val delta = deltaRaw * sensitivity
        val old = traitState.scores[trait] ?: 50.0
        val newVal = (old + delta).coerceIn(0.0, 100.0)
        traitState.scores[trait] = newVal

        if (deltaRaw > 0.1) traitState.supports[trait] = (traitState.supports[trait] ?: 0) + 1
        else if (deltaRaw < -0.1) traitState.conflicts[trait] = (traitState.conflicts[trait] ?: 0) + 1
    }

    fun getProfileSnapshot(): Map<String, Double> = traitState.scores.toMap()

    fun reset() {
        traitState = TraitState()
        for (t in initialTraits) {
            traitState.scores[t] = 50.0
            traitState.supports[t] = 0
            traitState.conflicts[t] = 0
            traitQuestionCount[t] = 0
            confidencePerTrait[t] = 0.5
        }
        askedQuestions.clear()
        history.clear()
        pendingFollowUps.clear()
        currentQuestion = selectNextQuestion()
    }

    private fun recomputeConfidences() {
        for ((trait, _) in traitState.scores) {
            val supports = traitState.supports[trait] ?: 0
            val conflicts = traitState.conflicts[trait] ?: 0
            val total = max(1, traitQuestionCount[trait] ?: (supports + conflicts))
            val diff = (supports - conflicts).toDouble()
            val x = diff / sqrt(total.toDouble())
            val conf = 1.0 / (1.0 + exp(-x))
            confidencePerTrait[trait] = conf.coerceIn(0.0, 1.0)
        }
    }

    fun generateProfileReport(context: Context): ProfileReport {
        recomputeConfidences()
        val scores = traitState.scores.toMap()
        val confs = confidencePerTrait.toMap()

        val sorted = scores.entries.sortedByDescending { it.value }
        val top = sorted.take(3)
        val mainLabel = when (top.firstOrNull()?.key) {
            "Extraversion" -> context.getString(R.string.trait_extravert)
            "Analytical" -> context.getString(R.string.trait_analytical)
            "Empathy" -> context.getString(R.string.trait_empathic)
            "Leadership" -> context.getString(R.string.trait_leader)
            "Creativity" -> context.getString(R.string.trait_creative)
            else -> context.getString(R.string.trait_balanced)
        }

        val strengths = top.map { "${it.key}: ${(it.value).toInt()}" }
        val growth = scores.entries.sortedBy { it.value }.take(3).map { "${it.key}: ${(it.value).toInt()}" }

        val patterns = mutableListOf<String>()
        if ((scores["Impulsivity"] ?: 50.0) > 65 && (scores["Empathy"] ?: 50.0) < 45) {
            patterns.add(context.getString(R.string.pattern_impulsive))
        }
        if ((scores["EmotionalStability"] ?: 50.0) < 40) patterns.add(context.getString(R.string.pattern_anxiety))
        if ((scores["Leadership"] ?: 50.0) > 65) patterns.add(context.getString(R.string.pattern_leadership))

        val globalConfidence = if (confs.isNotEmpty()) confs.values.average().coerceIn(0.0, 1.0) else 0.5

        return ProfileReport(
            mainLabel = mainLabel,
            traitScores = scores,
            confidences = confs,
            strengths = strengths,
            growthAreas = growth,
            patterns = patterns,
            globalConfidence = globalConfidence
        )
    }
}
