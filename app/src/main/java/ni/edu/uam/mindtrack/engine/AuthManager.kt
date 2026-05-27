package ni.edu.uam.mindtrack.engine

import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ni.edu.uam.mindtrack.model.User

object AuthManager {
    private val users = mutableListOf<User>()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validatePassword(password: String): Boolean {
        return password.length >= 6
    }

    fun validateUserRegistration(fullName: String, email: String, password: String): String? {
        return when {
            fullName.isBlank() -> "El nombre no puede estar vacío"
            fullName.length < 3 -> "Nombre demasiado corto"
            email.isBlank() -> "El correo no puede estar vacío"
            !validateEmail(email) -> "Correo electrónico inválido"
            password.isBlank() -> "La contraseña no puede estar vacía"
            !validatePassword(password) -> "La contraseña debe tener al menos 6 caracteres"
            else -> null
        }
    }

    fun validateUserLogin(email: String, password: String): String? {
        return when {
            email.isBlank() -> "El correo no puede estar vacío"
            password.isBlank() -> "La contraseña no puede estar vacía"
            !validateEmail(email) -> "Correo electrónico inválido"
            else -> null
        }
    }

    fun register(user: User): Boolean {
        if (users.any { it.email == user.email }) return false
        users.add(user)
        _currentUser.value = user
        return true
    }

<<<<<<< HEAD
    fun login(email: String, password: String): Boolean {
        val user = users.find { it.email == email && it.password == password } ?: return false
        _currentUser.value = user
        return true
    }

    fun updateProfile(fullName: String, email: String, profileImageUri: Uri? = null): Boolean {
        val current = _currentUser.value ?: return false

        if (email != current.email && users.any { it.email == email }) return false

        val updatedUser = current.copy(
            fullName = fullName,
            email = email,
            profileImageUri = profileImageUri ?: current.profileImageUri
        )

        val index = users.indexOfFirst { it.email == current.email }
        if (index != -1) {
            users[index] = updatedUser
        } else {
            users.add(updatedUser)
        }

        _currentUser.value = updatedUser
        return true
    }

    fun updatePassword(currentPassword: String, newPassword: String): String? {
        val current = _currentUser.value ?: return "Sesión no válida"

        if (current.password != currentPassword) {
            return "La contraseña actual es incorrecta"
        }

        if (!validatePassword(newPassword)) {
            return "La nueva contraseña debe tener al menos 6 caracteres"
        }

        val updatedUser = current.copy(password = newPassword)
        val index = users.indexOfFirst { it.email == current.email }
        if (index != -1) {
            users[index] = updatedUser
        }

        _currentUser.value = updatedUser
        return null
    }

    fun logout() {
        _currentUser.value = null
=======
    fun login(email: String, password: String): User? {
        return users.find { it.email == email && it.password == password }
>>>>>>> main
    }
}
