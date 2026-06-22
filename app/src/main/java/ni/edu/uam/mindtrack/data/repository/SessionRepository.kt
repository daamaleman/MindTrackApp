package ni.edu.uam.mindtrack.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ni.edu.uam.mindtrack.data.local.dao.SessionDao
import ni.edu.uam.mindtrack.data.local.entities.SessionEntity
import ni.edu.uam.mindtrack.data.local.entities.toDomain
import ni.edu.uam.mindtrack.data.local.entities.toEntity
import ni.edu.uam.mindtrack.data.remote.MindTrackApiService
import ni.edu.uam.mindtrack.data.remote.TrackSessionDto
import ni.edu.uam.mindtrack.model.SessionResult

class SessionRepository(
    private val apiService: MindTrackApiService,
    private val sessionDao: SessionDao
) {
    // CRUD Local con Flow para tiempo real
    val allSessions: Flow<List<SessionResult>> = sessionDao.getAllSessions().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun insertLocalSession(session: SessionResult) {
        sessionDao.insertSession(session.toEntity())
    }

    suspend fun deleteLocalSession(session: SessionResult) {
        sessionDao.deleteSession(session.toEntity())
    }

    suspend fun syncSessions(userId: Long): Result<Unit> {
        return try {
            val response = apiService.getSessions()
            if (response.isSuccessful) {
                val apiSessions = response.body()?.filter { it.idUsuario == userId } ?: emptyList()
                
                val entities = apiSessions.map { dto ->
                    SessionEntity(
                        id = dto.id?.toString() ?: java.util.UUID.randomUUID().toString(),
                        finalResult = dto.estadoAnimo,
                        date = dto.notas ?: "01/01/2026",
                        energy = 80,
                        stress = 20,
                        progress = 0,
                        money = 100,
                        choicesMade = 5,
                        path = ""
                    )
                }
                sessionDao.insertSessions(entities)
                Result.Success(Unit)
            } else {
                Result.Error("Error API: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Sync Error", e)
        }
    }

    suspend fun getSessions(): Result<List<TrackSessionDto>> {
        return try {
            val response = apiService.getSessions()
            if (response.isSuccessful) {
                Result.Success(response.body() ?: emptyList())
            } else {
                Result.Error("Error API: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown Error", e)
        }
    }

    suspend fun createSession(session: TrackSessionDto): Result<TrackSessionDto> {
        return try {
            val response = apiService.createSession(session)
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Error API: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown Error", e)
        }
    }
}
