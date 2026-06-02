package ni.edu.uam.mindtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import ni.edu.uam.mindtrack.engine.AuthManager
import ni.edu.uam.mindtrack.data.OnboardingPreferences
import ni.edu.uam.mindtrack.viewmodel.MindTrackViewModelFactory
import ni.edu.uam.mindtrack.viewmodel.MindTrackViewModel
import ni.edu.uam.mindtrack.navigation.MindTrackNavGraph
import ni.edu.uam.mindtrack.ui.theme.MindTrackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AuthManager.initialize(applicationContext)
        setContent {
            val prefs = OnboardingPreferences(applicationContext)
            val factory = MindTrackViewModelFactory(prefs)
            val viewModel: MindTrackViewModel = viewModel(factory = factory)
            val isDarkMode by viewModel.isDarkMode.collectAsState()

            MindTrackTheme(darkTheme = isDarkMode) {
                MindTrackNavGraph(viewModel = viewModel)
            }
        }
    }
}
