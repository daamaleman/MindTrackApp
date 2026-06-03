package ni.edu.uam.mindtrack.model

import android.net.Uri

data class User(
    val id: Long? = null,
    val fullName: String,
    val email: String,
    val password: String,
    val profileImageUri: Uri? = null
)
