package ni.edu.uam.mindtrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ni.edu.uam.mindtrack.data.OnboardingPreferences
import ni.edu.uam.mindtrack.data.remote.RetrofitClient
import ni.edu.uam.mindtrack.data.repository.UserRepository
import ni.edu.uam.mindtrack.data.repository.SessionRepository

class MindTrackViewModelFactory(private val onboardingPreferences: OnboardingPreferences) : ViewModelProvider.Factory {
    private val userRepository = UserRepository(RetrofitClient.apiService)
    private val sessionRepository = SessionRepository(RetrofitClient.apiService)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MindTrackViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MindTrackViewModel(onboardingPreferences, sessionRepository, userRepository) as T
        }
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(userRepository) as T
        }
        if (modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditProfileViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

