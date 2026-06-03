package ni.edu.uam.mindtrack.data.repository

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String, val exception: Throwable? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
