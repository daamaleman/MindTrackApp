package ni.edu.uam.mindtrack.data

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import ni.edu.uam.mindtrack.model.User
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class AuthPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    fun loadUsers(): List<User> {
        val raw = prefs.getString(KEY_USERS, null) ?: return emptyList()
        val array = JSONArray(raw)
        return buildList {
            for (index in 0 until array.length()) {
                add(array.getJSONObject(index).toUser())
            }
        }
    }

    fun loadCurrentUser(): User? {
        val raw = prefs.getString(KEY_CURRENT_USER, null) ?: return null
        return JSONObject(raw).toUser()
    }

    fun saveUsers(users: List<User>) {
        val array = JSONArray()
        users.forEach { user ->
            array.put(user.toJson())
        }
        prefs.edit().putString(KEY_USERS, array.toString()).commit()
    }

    fun saveCurrentUser(user: User?) {
        val editor = prefs.edit()
        if (user == null) {
            editor.remove(KEY_CURRENT_USER)
        } else {
            editor.putString(KEY_CURRENT_USER, user.toJson().toString())
        }
        editor.commit()
    }

    fun reset() {
        prefs.edit().remove(KEY_USERS).remove(KEY_CURRENT_USER).commit()
    }

    private fun User.toJson(): JSONObject {
        return JSONObject().apply {
            put("id", id)
            put("fullName", fullName)
            put("email", email)
            put("password", password)
            put("profileImageUri", profileImageUri?.toString())
        }
    }

    private fun JSONObject.toUser(): User {
        val profileImage = if (has("profileImageUri") && !isNull("profileImageUri")) {
            getString("profileImageUri").takeIf { it.isNotBlank() }
        } else {
            null
        }
        val userId = if (has("id") && !isNull("id")) getLong("id") else null
        return User(
            id = userId,
            fullName = getString("fullName"),
            email = getString("email"),
            password = getString("password"),
            profileImageUri = profileImage?.let(Uri::parse)
        )
    }

    companion object {
        private const val KEY_USERS = "users_json"
        private const val KEY_CURRENT_USER = "current_user_json"
    }
}
