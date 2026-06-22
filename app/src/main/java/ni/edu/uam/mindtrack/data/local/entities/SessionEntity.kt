package ni.edu.uam.mindtrack.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ni.edu.uam.mindtrack.model.PlayerState
import ni.edu.uam.mindtrack.model.SessionResult

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey
    val id: String,
    val finalResult: String,
    val date: String,
    val energy: Int,
    val stress: Int,
    val progress: Int,
    val money: Int,
    val choicesMade: Int,
    val path: String // Guardado como string separado por comas
)

fun SessionEntity.toDomain(): SessionResult {
    return SessionResult(
        id = id,
        finalResult = finalResult,
        date = date,
        finalState = PlayerState(energy, stress, progress, money),
        choicesMade = choicesMade,
        path = if (path.isEmpty()) emptyList() else path.split(",").map { it.toInt() }
    )
}

fun SessionResult.toEntity(): SessionEntity {
    return SessionEntity(
        id = id,
        finalResult = finalResult,
        date = date,
        energy = finalState.energy,
        stress = finalState.stress,
        progress = finalState.progress,
        money = finalState.money,
        choicesMade = choicesMade,
        path = path.joinToString(",")
    )
}
