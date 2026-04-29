package ni.edu.uam.mindtrack.model

data class Option(
    val text: String,
    val type: String // "rational" | "impulsive" | "balanced"
)

data class Scenario(
    val id: Int,
    val question: String,
    val category: String,
    val emoji: String,
    val options: List<Option>
)

data class SessionResult(
    val id: String,
    val profile: String,
    val date: String,
    val decisionCount: Int
)
