package ni.edu.uam.mindtrack.data.remote

import ni.edu.uam.mindtrack.data.remote.UserDto
import retrofit2.Response
import retrofit2.http.*

interface MindTrackApiService {
    @GET("api/usuarios/{id}")
    suspend fun getUsuario(@Path("id") id: Long): Response<UserDto>

    @POST("api/usuarios")
    suspend fun registerUsuario(@Body usuario: UserDto): Response<UserDto>

    @PUT("api/usuarios/{id}")
    suspend fun updateUsuario(@Path("id") id: Long, @Body usuario: UserDto): Response<UserDto>

    @GET("api/sessions")
    suspend fun getSessions(): Response<List<TrackSessionDto>>

    @POST("api/sessions")
    suspend fun createSession(@Body session: TrackSessionDto): Response<TrackSessionDto>
}
