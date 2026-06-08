package ni.edu.uam.mindtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.collectAsState
import androidx.core.content.ContextCompat
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

        // Solicitar permisos de notificación en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != 
                PackageManager.PERMISSION_GRANTED) {
                registerForActivityResult(ActivityResultContracts.RequestPermission()) {}.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            val context = applicationContext
            val prefs = OnboardingPreferences(context)
            val factory = MindTrackViewModelFactory(prefs, context)
            val viewModel: MindTrackViewModel = viewModel(factory = factory)
            val isDarkMode by viewModel.isDarkMode.collectAsState()

            MindTrackTheme(darkTheme = isDarkMode) {
                MindTrackNavGraph(viewModel = viewModel)
            }
        }
    }
}
