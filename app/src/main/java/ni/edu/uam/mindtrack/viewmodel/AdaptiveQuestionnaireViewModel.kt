package ni.edu.uam.mindtrack.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
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
    // Para LIKERT y OPEN simple: weights por rasgo. Para MULTIPLE_CHOICE/FORCED_CHOICE,
    // se pueden usar optionWeights donde cada opción tiene su propio mapa de pesos.
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

class AdaptiveQuestionnaireViewModel : ViewModel() {
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

    // Banco de preguntas (reducido pero representativo)
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

        questions = buildQuestionBank()
        currentQuestion = selectNextQuestion()
    }

    private fun buildQuestionBank(): List<Question> {
        val bank = mutableListOf<Question>()

        bank += Question(
            id = "Q001",
            type = QuestionType.LIKERT,
            text = "En reuniones sociales me siento cómodo iniciando conversaciones.",
            options = listOf("1","2","3","4","5"),
            weights = mapOf("Extraversion" to 1.0)
        )

        bank += Question(
            id = "Q002",
            type = QuestionType.LIKERT,
            text = "Cuando me enfrento a un problema complejo, prefiero pensar solo hasta tener una solución.",
            options = listOf("1","2","3","4","5"),
            weights = mapOf("Analytical" to 0.9, "Extraversion" to -0.2)
        )

        bank += Question(
            id = "Q004",
            type = QuestionType.LIKERT,
            text = "En general, me siento tranquilo/a ante situaciones nuevas.",
            options = listOf("1","2","3","4","5"),
            weights = mapOf("EmotionalStability" to 1.0)
        )

        bank += Question(
            id = "Q011",
            type = QuestionType.LIKERT,
            text = "Disfruto trabajar en proyectos donde soy responsable de coordinar al equipo.",
            options = listOf("1","2","3","4","5"),
            weights = mapOf("Leadership" to 1.0, "Extraversion" to 0.4),
            followUpRules = listOf({ ts ->
                val score = ts.scores["Leadership"] ?: 50.0
                if (score > 65.0) "Q302" else null
            })
        )

        bank += Question(
            id = "Q021",
            type = QuestionType.LIKERT,
            text = "Me es fácil recuperar la calma después de una noticia negativa.",
            options = listOf("1","2","3","4","5"),
            weights = mapOf("EmotionalStability" to 1.0)
        )

        bank += Question(
            id = "Q031",
            type = QuestionType.FORCED_CHOICE,
            text = "Tienes una oportunidad de inversión arriesgada con alta ganancia:",
            options = listOf("Intento","Prefiero seguro"),
            optionWeights = listOf(
                mapOf("RiskTolerance" to 0.8, "Impulsivity" to 0.6),
                mapOf("RiskTolerance" to 0.2, "Impulsivity" to -0.4)
            ),
            followUpRules = listOf({ ts ->
                val rt = ts.scores["RiskTolerance"] ?: 50.0
                if (rt > 65.0) "Q307" else null
            })
        )

        bank += Question(
            id = "Q032",
            type = QuestionType.LIKERT,
            text = "Tiendo a actuar sin pensar cuando estoy emocionado/a.",
            options = listOf("1","2","3","4","5"),
            weights = mapOf("Impulsivity" to 1.0),
            followUpRules = listOf({ ts ->
                val score = ts.scores["Impulsivity"] ?: 50.0
                if (score > 60.0) "Q304" else null
            })
        )

        bank += Question(
            id = "Q041",
            type = QuestionType.MULTIPLE_CHOICE,
            text = "Cuando resuelves un problema, ¿qué describe mejor tu estilo?",
            options = listOf("Busco patrones","Paso a paso","Combino ideas","Confío en intuición"),
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
            text = "Me resulta sencillo ponerme en el lugar de otros y entender cómo se sienten.",
            options = listOf("1","2","3","4","5"),
            weights = mapOf("Empathy" to 1.0),
            followUpRules = listOf({ ts ->
                val score = ts.scores["Empathy"] ?: 50.0
                if (score < 45.0) "Q305" else null
            })
        )

        bank += Question(
            id = "Q061",
            type = QuestionType.LIKERT,
            text = "Me considero capaz de superar nuevos retos.",
            options = listOf("1","2","3","4","5"),
            weights = mapOf("SelfEsteem" to 1.0)
        )

        bank += Question(
            id = "Q091",
            type = QuestionType.OPEN,
            text = "¿Qué te motiva más: impacto en otros, crecimiento personal, reconocimiento o seguridad? Explícalo.",
            weights = mapOf("SelfEsteem" to 0.3, "Empathy" to 0.3)
        )

        // Extender con más preguntas para cubrir rasgos.
        bank += Question(
            id = "Q003",
            type = QuestionType.FORCED_CHOICE,
            text = "Si tu equipo necesita una decisión rápida, ¿prefieres?:",
            options = listOf("Tomar control y decidir","Consultar y construir consenso"),
            optionWeights = listOf(
                mapOf("Leadership" to 0.7, "DecisionStyle" to 0.6),
                mapOf("Leadership" to 0.3, "DecisionStyle" to 0.4)
            )
        )

        bank += Question(
            id = "Q005",
            type = QuestionType.MULTIPLE_CHOICE,
            text = "¿Cómo sueles afrontar una decisión importante?",
            options = listOf("Buscar datos y pros/cons","Confiar en mi intuición","Pedir opiniones y votar","Probar y ajustar después"),
            optionWeights = listOf(
                mapOf("Analytical" to 1.0, "DecisionStyle" to 0.5),
                mapOf("Analytical" to -0.2, "DecisionStyle" to 1.0),
                mapOf("Analytical" to 0.2, "Empathy" to 0.3, "DecisionStyle" to 0.6),
                mapOf("Creativity" to 0.2, "DecisionStyle" to 0.5)
            )
        )

        bank += Question(
            id = "Q007",
            type = QuestionType.LIKERT,
            text = "Me cuesta dejar planes abiertos y sin decidir.",
            options = listOf("1","2","3","4","5"),
            weights = mapOf("Impulsivity" to -0.6, "Analytical" to 0.4)
        )

        bank += Question(
            id = "Q008",
            type = QuestionType.SCENARIO,
            text = "Imagina que estás en una sala con desconocidos: ¿prefieres sentarte con quien ya conoces o abrirte a alguien nuevo?",
            options = listOf("Quedarme con conocidos","Abrirme a alguien nuevo"),
            optionWeights = listOf(
                mapOf("Extraversion" to -0.8, "Empathy" to 0.2),
                mapOf("Extraversion" to 0.8, "Empathy" to 0.3)
            ),
            followUpRules = listOf({ ts ->
                val score = ts.scores["Extraversion"] ?: 50.0
                if (score < 40.0) "Q303" else null
            })
        )

        bank += Question(
            id = "Q012",
            type = QuestionType.FORCED_CHOICE,
            text = "¿Cuál describe mejor tu estilo en reuniones?:",
            options = listOf("Hablo primero","Escucho y luego comento"),
            optionWeights = listOf(
                mapOf("Extraversion" to 0.8),
                mapOf("Analytical" to 0.2)
            )
        )

        bank += Question(
            id = "Q013",
            type = QuestionType.MULTIPLE_CHOICE,
            text = "Un compañero propone una idea que no me convence — ¿qué haces?",
            options = listOf("Le dices directamente","Preguntas para entender mejor","Sugieres probar la idea a pequeña escala"),
            optionWeights = listOf(
                mapOf("Impulsivity" to 0.6, "Empathy" to -0.3),
                mapOf("Empathy" to 0.6, "Analytical" to 0.2),
                mapOf("Analytical" to 0.4, "Leadership" to 0.3)
            )
        )

        bank += Question(
            id = "Q014",
            type = QuestionType.OPEN,
            text = "Cuenta una situación en la que tu opinión influyó en el grupo.",
            weights = mapOf("Leadership" to 0.6, "SelfEsteem" to 0.3)
        )

        bank += Question(
            id = "Q022",
            type = QuestionType.LIKERT,
            text = "Con qué frecuencia sientes preocupación que interfiere con tus planes diarios? (1=Siempre..5=Nunca)",
            options = listOf("1","2","3","4","5"),
            weights = mapOf("EmotionalStability" to -1.0)
        )

        bank += Question(
            id = "Q023",
            type = QuestionType.FORCED_CHOICE,
            text = "Si notas que una persona cercana se aleja sin explicación, ¿cómo reaccionas?",
            options = listOf("Me preocupo intensamente","Le doy espacio","Le escribo para preguntar"),
            optionWeights = listOf(
                mapOf("EmotionalStability" to -0.8, "Empathy" to 0.2),
                mapOf("EmotionalStability" to 0.4, "Empathy" to 0.1),
                mapOf("Empathy" to 0.6)
            )
        )

        bank += Question(
            id = "Q033",
            type = QuestionType.MULTIPLE_CHOICE,
            text = "En una discusión acalorada, ¿cuál es tu respuesta más típica?",
            options = listOf("Interrumpo para defenderme","Me retiro y pienso","Uso el humor para bajar la tensión"),
            optionWeights = listOf(
                mapOf("Impulsivity" to 1.0, "Empathy" to -0.2),
                mapOf("Impulsivity" to -0.5, "Analytical" to 0.2),
                mapOf("Empathy" to 0.2, "Impulsivity" to -0.2)
            )
        )

        bank += Question(
            id = "Q042",
            type = QuestionType.OPEN,
            text = "Te piden diseñar una solución con recursos limitados — describe el primer paso que tomarías.",
            weights = mapOf("Creativity" to 0.6, "Analytical" to 0.4)
        )

        bank += Question(
            id = "Q043",
            type = QuestionType.FORCED_CHOICE,
            text = "¿Prefieres leer ficción que estimule la imaginación o textos técnicos?",
            options = listOf("Ficción","Técnico"),
            optionWeights = listOf(
                mapOf("Creativity" to 0.7, "Analytical" to 0.2),
                mapOf("Creativity" to 0.1, "Analytical" to 0.8)
            )
        )

        bank += Question(
            id = "Q052",
            type = QuestionType.LIKERT,
            text = "Un amigo confiesa un error grave. ¿Qué haces? (1=Juzgo .. 5=Ofrezco apoyo)",
            options = listOf("1","2","3","4","5"),
            weights = mapOf("Empathy" to 1.0)
        )

        bank += Question(
            id = "Q071",
            type = QuestionType.FORCED_CHOICE,
            text = "Cuando aprendes algo nuevo:",
            options = listOf("Lo practico mucho","Lo leo y lo analizo","Lo comparto y discuto"),
            optionWeights = listOf(
                mapOf("Analytical" to 0.6, "Creativity" to 0.2),
                mapOf("Analytical" to 0.9),
                mapOf("Extraversion" to 0.6, "Empathy" to 0.3)
            )
        )

        bank += Question(
            id = "Q081",
            type = QuestionType.SCENARIO,
            text = "Eres líder de un equipo con plazos ajustados. Un miembro propone ahorrar tiempo reduciendo controles de calidad. ¿Qué haces?",
            options = listOf("Aceptar para cumplir plazos","Mantener controles y negociar plazo","Proponer prueba reducida"),
            optionWeights = listOf(
                mapOf("RiskTolerance" to 0.6, "Leadership" to 0.2),
                mapOf("Leadership" to 0.8, "Analytical" to 0.4),
                mapOf("Analytical" to 0.6, "Leadership" to 0.5)
            )
        )

        bank += Question(
            id = "Q091b",
            type = QuestionType.OPEN,
            text = "Describe la persona que crees que eres en tres adjetivos.",
            weights = mapOf("SelfEsteem" to 0.3, "Empathy" to 0.2)
        )

        bank += Question(
            id = "Q201",
            type = QuestionType.FORCED_CHOICE,
            text = "Prefieres resolver ahora aunque no sea perfecto o esperar para hacerlo perfecto?",
            options = listOf("Resolver ahora","Esperar y perfeccionar"),
            optionWeights = listOf(
                mapOf("Impulsivity" to 0.6, "DecisionStyle" to 80.0),
                mapOf("Analytical" to 0.8, "DecisionStyle" to 20.0)
            )
        )

        bank += Question(
            id = "Q202",
            type = QuestionType.LIKERT,
            text = "¿Prefieres resolver ahora aunque no sea perfecto o esperar para hacerlo perfecto? (1=Siempre ahora..5=Siempre esperar)",
            options = listOf("1","2","3","4","5"),
            weights = mapOf("Impulsivity" to -0.6)
        )

        bank += Question(
            id = "Q203",
            type = QuestionType.OPEN,
            text = "Imagina que alguien te critica en público por algo que no hiciste. Escribe 1-2 frases de cómo responderías.",
            weights = mapOf("EmotionalStability" to 0.3, "SelfEsteem" to 0.2)
        )

        bank += Question(
            id = "Q204",
            type = QuestionType.MULTIPLE_CHOICE,
            text = "Ordena: Seguridad, Innovación, Comunidad, Autonomía (elige la más importante)",
            options = listOf("Seguridad","Innovación","Comunidad","Autonomía"),
            optionWeights = listOf(
                mapOf("RiskTolerance" to 0.2),
                mapOf("Creativity" to 0.8),
                mapOf("Empathy" to 0.7),
                mapOf("Leadership" to 0.5)
            )
        )

        bank += Question(
            id = "Q205",
            type = QuestionType.FORCED_CHOICE,
            text = "Elige entre: Estabilidad vs Excitación (¿qué valoras más?)",
            options = listOf("Estabilidad","Excitación"),
            optionWeights = listOf(
                mapOf("EmotionalStability" to 0.8),
                mapOf("RiskTolerance" to 0.8)
            )
        )

        // Extender aún más el banco para cubrir una gama amplia de rasgos (llegamos a ~45 preguntas)
        bank += Question(
            id = "Q206",
            type = QuestionType.LIKERT,
            text = "Cuando alguien me pide ayuda, lo hago incluso si me implica un esfuerzo extra.",
            options = listOf("1","2","3","4","5"),
            weights = mapOf("Empathy" to 1.0)
        )

        bank += Question(
            id = "Q207",
            type = QuestionType.LIKERT,
            text = "Prefiero planificar antes de actuar en tareas importantes.",
            options = listOf("1","2","3","4","5"),
            weights = mapOf("Analytical" to 1.0, "Impulsivity" to -0.6)
        )

        bank += Question(
            id = "Q208",
            type = QuestionType.FORCED_CHOICE,
            text = "En una discusión, ¿tiendes a?:",
            options = listOf("Defender mi punto con firmeza","Buscar acuerdos"),
            optionWeights = listOf(
                mapOf("Impulsivity" to 0.6, "Extraversion" to 0.2),
                mapOf("Empathy" to 0.6, "Analytical" to 0.2)
            )
        )

        bank += Question(
            id = "Q209",
            type = QuestionType.MULTIPLE_CHOICE,
            text = "¿Qué te motiva más en el trabajo?",
            options = listOf("Impacto en otros","Logros personales","Reconocimiento","Seguridad económica"),
            optionWeights = listOf(
                mapOf("Empathy" to 0.8),
                mapOf("SelfEsteem" to 0.7),
                mapOf("SelfEsteem" to 0.4, "Extraversion" to 0.2),
                mapOf("EmotionalStability" to 0.6)
            )
        )

        bank += Question(
            id = "Q210",
            type = QuestionType.OPEN,
            text = "Describe una situación en la que cambiaste de opinión después de nueva información.",
            weights = mapOf("Analytical" to 0.5, "Open" to 0.2)
        )

        bank += Question(
            id = "Q211",
            type = QuestionType.LIKERT,
            text = "Disfruto las actividades que requieren imaginación y creación.",
            options = listOf("1","2","3","4","5"),
            weights = mapOf("Creativity" to 1.0)
        )

        bank += Question(
            id = "Q212",
            type = QuestionType.FORCED_CHOICE,
            text = "¿En un debate público prefieres?:",
            options = listOf("Hablar y persuadir","Escuchar y refutar después"),
            optionWeights = listOf(
                mapOf("Extraversion" to 0.7, "Leadership" to 0.4),
                mapOf("Analytical" to 0.6, "Empathy" to 0.2)
            )
        )

        bank += Question(
            id = "Q213",
            type = QuestionType.SCENARIO,
            text = "Tienes que elegir entre dos tareas: una segura con poco impacto y otra innovadora con riesgo. ¿Qué eliges?",
            options = listOf("Segura","Innovadora"),
            optionWeights = listOf(
                mapOf("RiskTolerance" to -0.6, "EmotionalStability" to 0.2),
                mapOf("RiskTolerance" to 0.8, "Creativity" to 0.4)
            ),
            followUpRules = listOf({ ts ->
                val score = ts.scores["RiskTolerance"] ?: 50.0
                if (score > 60.0) "Q307" else null
            })
        )

        bank += Question(
            id = "Q214",
            type = QuestionType.LIKERT,
            text = "Me siento cómodo/a asumiendo responsabilidad sobre errores del equipo.",
            options = listOf("1","2","3","4","5"),
            weights = mapOf("Leadership" to 1.0, "SelfEsteem" to 0.4)
        )

        bank += Question(
            id = "Q215",
            type = QuestionType.MULTIPLE_CHOICE,
            text = "Cuando trabajas en equipo eres más de:",
            options = listOf("Guiar el proceso","Apoyar tareas","Proponer ideas","Coordinar comunicación"),
            optionWeights = listOf(
                mapOf("Leadership" to 0.8),
                mapOf("Empathy" to 0.6),
                mapOf("Creativity" to 0.6),
                mapOf("Leadership" to 0.4, "Analytical" to 0.3)
            )
        )

        bank += Question(
            id = "Q216",
            type = QuestionType.FORCED_CHOICE,
            text = "¿Cómo tomas decisiones bajo presión?",
            options = listOf("Actúo rápido","Evalúo rápido opciones"),
            optionWeights = listOf(
                mapOf("Impulsivity" to 0.8),
                mapOf("Analytical" to 0.7)
            )
        )

        bank += Question(
            id = "Q217",
            type = QuestionType.OPEN,
            text = "¿Qué actividades te relajan y por qué?",
            weights = mapOf("EmotionalStability" to 0.4, "Empathy" to 0.2)
        )

        bank += Question(
            id = "Q218",
            type = QuestionType.LIKERT,
            text = "Me siento cómodo/a expresando mis emociones en público.",
            options = listOf("1","2","3","4","5"),
            weights = mapOf("Extraversion" to 0.6, "EmotionalStability" to 0.3)
        )

        bank += Question(
            id = "Q219",
            type = QuestionType.MULTIPLE_CHOICE,
            text = "¿Qué valoras más en un líder?",
            options = listOf("Visión","Empatía","Decisión rápida","Integridad"),
            optionWeights = listOf(
                mapOf("Leadership" to 0.8),
                mapOf("Empathy" to 0.8),
                mapOf("Impulsivity" to 0.2, "Leadership" to 0.4),
                mapOf("SelfEsteem" to 0.6)
            )
        )

        bank += Question(
            id = "Q220",
            type = QuestionType.SCENARIO,
            text = "Si notas un error menor en un informe que ya será enviado, ¿qué haces?",
            options = listOf("Enviar igual","Corregir y avisar"),
            optionWeights = listOf(
                mapOf("Impulsivity" to 0.6),
                mapOf("Analytical" to 0.7, "Leadership" to 0.2)
            )
        )

        // Añadir preguntas de follow-up especializadas
        bank += Question(
            id = "Q301",
            type = QuestionType.OPEN,
            text = "Has mostrado señales de preocupación frecuente. ¿Puedes describir brevemente cuándo aparece la ansiedad y cómo la manejas?",
            weights = mapOf("EmotionalStability" to -0.6)
        )

        bank += Question(
            id = "Q302",
            type = QuestionType.SCENARIO,
            text = "Estás a cargo de un equipo que no cumple plazos. ¿Cómo priorizas las acciones?",
            options = listOf("Redistribuir tareas","Negociar plazos","Incrementar supervisión"),
            optionWeights = listOf(
                mapOf("Leadership" to 0.6),
                mapOf("Leadership" to 0.5, "Analytical" to 0.3),
                mapOf("Leadership" to 0.4, "Impulsivity" to 0.2)
            )
        )

        bank += Question(
            id = "Q303",
            type = QuestionType.SCENARIO,
            text = "En una reunión grande, ¿cómo te sientes más cómodo participando?",
            options = listOf("Preparándome y hablando","Escuchando y respondiendo"),
            optionWeights = listOf(
                mapOf("Extraversion" to 0.8),
                mapOf("Extraversion" to -0.4, "Analytical" to 0.3)
            )
        )

        bank += Question(
            id = "Q304",
            type = QuestionType.OPEN,
            text = "Has mostrado tendencia a decidir rápido bajo emoción. ¿Qué estrategias usas para pausar y reflexionar?",
            weights = mapOf("Impulsivity" to -0.6, "Analytical" to 0.3)
        )

        bank += Question(
            id = "Q305",
            type = QuestionType.OPEN,
            text = "Me gustaría entender mejor tu perspectiva social: ¿qué suele motivarte a apoyar a otros?",
            weights = mapOf("Empathy" to 0.6)
        )

        bank += Question(
            id = "Q306",
            type = QuestionType.OPEN,
            text = "Describe un proyecto creativo que hayas disfrutado y por qué.",
            weights = mapOf("Creativity" to 0.7)
        )

        bank += Question(
            id = "Q307",
            type = QuestionType.FORCED_CHOICE,
            text = "Cuando se presenta riesgo en un proyecto, ¿prefieres?:",
            options = listOf("Mitigar y avanzar","Evitar riesgo"),
            optionWeights = listOf(
                mapOf("RiskTolerance" to 0.8),
                mapOf("RiskTolerance" to -0.6)
            )
        )

        bank += Question(
            id = "Q308",
            type = QuestionType.MULTIPLE_CHOICE,
            text = "Para tomar decisiones complejas, ¿qué recurso valoras más?",
            options = listOf("Datos","Opiniones","Intuición","Experimentos"),
            optionWeights = listOf(
                mapOf("Analytical" to 0.8),
                mapOf("Empathy" to 0.5),
                mapOf("DecisionStyle" to 0.6),
                mapOf("Creativity" to 0.6)
            )
        )

        // Normalizar pesos del banco si hay valores extremos (mantener proporciones)
        val maxAbs = bank.flatMap { q ->
            val w = q.weights.values
            val ow = q.optionWeights.flatMap { it.values }
            (w + ow)
        }.map { kotlin.math.abs(it) }.maxOrNull() ?: 0.0

        if (maxAbs > 3.0) {
            val scale = 3.0 / maxAbs
            for (i in bank.indices) {
                val q = bank[i]
                val scaledWeights = q.weights.mapValues { it.value * scale }
                val scaledOptionWeights = q.optionWeights.map { mw -> mw.mapValues { it.value * scale } }
                bank[i] = q.copy(weights = scaledWeights, optionWeights = scaledOptionWeights)
            }
        }

        return bank
    }

    // Selección simple de la siguiente pregunta
    private fun selectNextQuestion(): Question? {
        // primero follow-ups
        val nextFollow = synchronized(pendingFollowUps) { pendingFollowUps.poll() }
        if (nextFollow != null) {
            val q = questions.find { it.id == nextFollow }
            if (q != null && !askedQuestions.contains(q.id)) return q
        }

        // Elegir rasgo con menor confianza (aquí: menor número de supports)
        val trait = traitState.supports.minByOrNull { it.value }?.key

        // seleccionar pregunta que no esté hecha y que afecte al rasgo
        val candidate = questions.firstOrNull { q ->
            !askedQuestions.contains(q.id) && (
                q.weights.keys.contains(trait) || q.optionWeights.any { it.keys.contains(trait) }
            )
        }
        if (candidate != null) return candidate

        // fallback: cualquier pregunta no preguntada
        return questions.firstOrNull { !askedQuestions.contains(it.id) }
    }

    // Normaliza respuestas y actualiza puntuaciones
    fun submitAnswer(questionId: String, answer: String, optionIndex: Int? = null, likertValue: Int? = null) {
        val q = questions.find { it.id == questionId } ?: return
        val ts = System.currentTimeMillis()
        history.add(Triple(questionId, answer, ts))
        askedQuestions.add(questionId)

        // contar que esta pregunta afectó a ciertos rasgos
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
                // Simple heuristic: longitud y palabras clave
                val text = answer.lowercase(Locale.getDefault())
                val lenFactor = (text.length.coerceAtMost(200)).toDouble() / 200.0 // 0..1
                for ((trait, weight) in q.weights) {
                    applyTraitDelta(trait, lenFactor * weight * 0.6)
                }
            }
        }

        // Aplicar reglas de follow-up si corresponde
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

        // Recalcular confianzas por rasgo y seleccionar siguiente
        recomputeConfidences()
        currentQuestion = selectNextQuestion()
    }

    private fun applyTraitDelta(trait: String, deltaRaw: Double) {
        // deltaRaw está en una escala aproximada -1..1 o mayores para optionWeights
        // convertimos a cambio en 0..100: multiplicador
        val sensitivity = 20.0
        val delta = deltaRaw * sensitivity
        val old = traitState.scores[trait] ?: 50.0
        val newVal = (old + delta).coerceIn(0.0, 100.0)
        traitState.scores[trait] = newVal

        // actualizar supports/conflicts
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

    fun generateProfileReport(): ProfileReport {
        recomputeConfidences()
        val scores = traitState.scores.toMap()
        val confs = confidencePerTrait.toMap()

        val sorted = scores.entries.sortedByDescending { it.value }
        val top = sorted.take(3)
        val mainLabel = when (top.firstOrNull()?.key) {
            "Extraversion" -> "Extravertido"
            "Analytical" -> "Analítico"
            "Empathy" -> "Empático"
            "Leadership" -> "Líder"
            "Creativity" -> "Creativo"
            else -> "Perfil equilibrado"
        }

        val strengths = top.map { "${it.key}: ${(it.value).toInt()}" }
        val growth = scores.entries.sortedBy { it.value }.take(3).map { "${it.key}: ${(it.value).toInt()}" }

        val patterns = mutableListOf<String>()
        if ((scores["Impulsivity"] ?: 50.0) > 65 && (scores["Empathy"] ?: 50.0) < 45) {
            patterns.add("Tendencia a decisiones rápidas con baja empatía contextual")
        }
        if ((scores["EmotionalStability"] ?: 50.0) < 40) patterns.add("Sensibilidad a la ansiedad en situaciones inciertas")
        if ((scores["Leadership"] ?: 50.0) > 65) patterns.add("Toma de roles de liderazgo en grupos")

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




