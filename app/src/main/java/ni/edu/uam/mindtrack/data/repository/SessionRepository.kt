package ni.edu.uam.mindtrack.data.repository

import ni.edu.uam.mindtrack.data.remote.MindTrackApiService
import ni.edu.uam.mindtrack.data.remote.TrackSessionDto

class SessionRepository(private val apiService: MindTrackApiService) {
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
