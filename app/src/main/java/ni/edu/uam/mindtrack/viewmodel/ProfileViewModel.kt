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
import ni.edu.uam.mindtrack.engine.AuthManager

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<Result<UserDto>>(Result.Loading)
    val uiState: StateFlow<Result<UserDto>> = _uiState.asStateFlow()

    init {
        val currentUserId = AuthManager.currentUser.value?.id ?: 1L
        loadProfile(currentUserId)
    }

    fun loadProfile(id: Long) {
        viewModelScope.launch {
            _uiState.value = Result.Loading
            _uiState.value = userRepository.getUsuario(id)
        }
    }
}
