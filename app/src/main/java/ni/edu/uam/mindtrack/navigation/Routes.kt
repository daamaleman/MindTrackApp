package ni.edu.uam.mindtrack.navigation

sealed class Routes(val route: String) {
    object Home : Routes("home")
    object Scenario : Routes("scenario")
    object Result : Routes("result")
    object History : Routes("history")
}
