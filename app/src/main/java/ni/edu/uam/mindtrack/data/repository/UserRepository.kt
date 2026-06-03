package ni.edu.uam.mindtrack.data.repository

import ni.edu.uam.mindtrack.data.remote.MindTrackApiService
import ni.edu.uam.mindtrack.data.remote.UserDto

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
}
