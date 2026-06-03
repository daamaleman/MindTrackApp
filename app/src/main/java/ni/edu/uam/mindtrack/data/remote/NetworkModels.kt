package ni.edu.uam.mindtrack.data.remote

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("fotoPerfil") val fotoPerfil: String? = null,
    @SerializedName("biografia") val biografia: String? = null
)

data class TrackSessionDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("idUsuario") val idUsuario: Long,
    @SerializedName("estadoAnimo") val estadoAnimo: String,
    @SerializedName("notas") val notas: String? = null
)

data class EstadisticaDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("idUsuario") val idUsuario: Long,
    @SerializedName("sesionesCompletadas") val sesionesCompletadas: Int,
    @SerializedName("rachaActual") val rachaActual: Int
)

data class LogroDto(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("idUsuario") val idUsuario: Long,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("desbloqueado") val desbloqueado: Boolean
)
