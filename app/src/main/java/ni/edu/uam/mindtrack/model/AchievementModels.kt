package ni.edu.uam.mindtrack.model

enum class AchievementCategory(val title: String) {
    CONSTANCIA("Constancia"),
    MAESTRIA("Maestría"),
    EXPLORACION("Exploración")
}

data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val emoji: String,
    val category: AchievementCategory,
    val unlocked: Boolean,
    val progress: Int,
    val unlockedDate: String? = null
)
