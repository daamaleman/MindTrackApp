package ni.edu.uam.mindtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import ni.edu.uam.mindtrack.navigation.MindTrackNavGraph
import ni.edu.uam.mindtrack.ui.theme.MindTrackTheme
import ni.edu.uam.mindtrack.viewmodel.MindTrackViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: MindTrackViewModel = viewModel()
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            
            MindTrackTheme(darkTheme = isDarkMode) {
                MindTrackNavGraph(viewModel = viewModel)
            }
        }
    }
}
