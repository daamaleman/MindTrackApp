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

    // Estadísticas
    @GET("api/estadistica/usuario/{idUsuario}")
    suspend fun getEstadistica(@Path("idUsuario") idUsuario: Long): Response<EstadisticaDto>

    @POST("api/estadistica")
    suspend fun updateEstadistica(@Body estadistica: EstadisticaDto): Response<EstadisticaDto>

    // Logros
    @GET("api/logros/usuario/{idUsuario}")
    suspend fun getLogros(@Path("idUsuario") idUsuario: Long): Response<List<LogroDto>>

    @POST("api/logros")
    suspend fun unlockLogro(@Body logro: LogroDto): Response<LogroDto>
}
