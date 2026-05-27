package ni.edu.uam.mindtrack.model

data class PlayerState(
    val energy: Int,
    val stress: Int,
    val progress: Int,
    val money: Int
)

data class Option(
    val text: String,
    val nextScenarioId: Int?, // null = final
    val energyEffect: Int,
    val stressEffect: Int,
    val progressEffect: Int,
    val moneyEffect: Int
)

data class Scenario(
    val id: Int,
    val title: String,
    val question: String,
    val options: List<Option>
)

data class SessionResult(
    val id: String,
    val finalResult: String,
    val date: String,
    val finalState: PlayerState,
    val choicesMade: Int,
    val path: List<Int>
)

data class UserProfile(
    val name: String,
    val email: String,
    val memberSince: String,
    val isPremium: Boolean = false,
    val profileImageUri: String? = null,
    val age: String = "",
    val gender: String = "",
    val bio: String = "",
    val dailyReminder: Boolean = false
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val unlocked: Boolean = false,
    val icon: String = "🏆"
)
