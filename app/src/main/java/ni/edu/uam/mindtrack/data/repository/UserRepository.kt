package ni.edu.uam.mindtrack.data.repository

import ni.edu.uam.mindtrack.data.remote.MindTrackApiService
import ni.edu.uam.mindtrack.data.remote.UserDto
import ni.edu.uam.mindtrack.data.remote.EstadisticaDto
import ni.edu.uam.mindtrack.data.remote.LogroDto

class UserRepository(private val apiService: MindTrackApiService) {
    suspend fun getUsuario(id: Long): Result<UserDto> {
        return try {
            val response = apiService.getUsuario(id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.Success(body)
                } else {
                    Result.Error("Usuario no encontrado (Cuerpo vacío)")
                }
            } else {
                Result.Error("Error API: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown Error", e)
        }
    }

    suspend fun updateUsuario(id: Long, usuario: UserDto): Result<UserDto> {
        return try {
            val response = apiService.updateUsuario(id, usuario)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.Success(body)
                } else {
                    Result.Error("Error al actualizar (Cuerpo vacío)")
                }
            } else {
                Result.Error("Error API: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown Error", e)
        }
    }

    suspend fun registerUsuario(usuario: UserDto): Result<UserDto> {
        return try {
            val response = apiService.registerUsuario(usuario)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.Success(body)
                } else {
                    Result.Error("Error al registrar (Cuerpo vacío)")
                }
            } else {
                Result.Error("Error API: ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown Error", e)
        }
    }

    // Estadísticas
    suspend fun getEstadistica(idUsuario: Long): Result<EstadisticaDto> {
        return try {
            val response = apiService.getEstadistica(idUsuario)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Error al obtener estadísticas")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown Error", e)
        }
    }

    suspend fun updateEstadistica(estadistica: EstadisticaDto): Result<EstadisticaDto> {
        return try {
            val response = apiService.updateEstadistica(estadistica)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Error al actualizar estadísticas")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown Error", e)
        }
    }

    // Logros
    suspend fun getLogros(idUsuario: Long): Result<List<LogroDto>> {
        return try {
            val response = apiService.getLogros(idUsuario)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Error al obtener logros")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown Error", e)
        }
    }

    suspend fun unlockLogro(logro: LogroDto): Result<LogroDto> {
        return try {
            val response = apiService.unlockLogro(logro)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Error al desbloquear logro")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown Error", e)
        }
    }
}
