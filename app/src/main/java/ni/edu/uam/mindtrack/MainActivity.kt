package ni.edu.uam.mindtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ni.edu.uam.mindtrack.navigation.MindTrackNavGraph
import ni.edu.uam.mindtrack.ui.theme.MindTrackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MindTrackTheme {
                MindTrackNavGraph()
            }
        }
    }
}
