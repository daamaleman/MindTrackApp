package ni.edu.uam.mindtrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ni.edu.uam.mindtrack.data.remote.UserDto
import ni.edu.uam.mindtrack.data.repository.Result
import ni.edu.uam.mindtrack.data.repository.UserRepository

class EditProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _updateState = MutableStateFlow<Result<UserDto>?>(null)
    val updateState: StateFlow<Result<UserDto>?> = _updateState.asStateFlow()

    fun updateProfile(id: Long, name: String, email: String, photoUrl: String, bio: String) {
        viewModelScope.launch {
            _updateState.value = Result.Loading
            val userDto = UserDto(id, name, email, photoUrl, bio)
            _updateState.value = userRepository.updateUsuario(id, userDto)
        }
    }

    fun clearUpdateState() {
        _updateState.value = null
    }
}
