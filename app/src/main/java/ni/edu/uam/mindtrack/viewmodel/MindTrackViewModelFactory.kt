package ni.edu.uam.mindtrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ni.edu.uam.mindtrack.data.OnboardingPreferences

class MindTrackViewModelFactory(private val onboardingPreferences: OnboardingPreferences) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MindTrackViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MindTrackViewModel(onboardingPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

