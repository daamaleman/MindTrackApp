package ni.edu.uam.mindtrack.engine

import ni.edu.uam.mindtrack.model.User

object AuthManager {
    private val users = mutableListOf<User>()

    fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validatePassword(password: String): Boolean {
        return password.length >= 6
    }

    /**
     * Valida los datos de registro y devuelve un mensaje de error si algo está mal,
     * o null si todo es válido.
     */
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

    /**
     * Valida los datos de login y devuelve un mensaje de error si algo está mal,
     * o null si todo es válido.
     */
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
        return true
    }

    fun login(email: String, password: String): Boolean {
        return users.any { it.email == email && it.password == password }
    }
}
