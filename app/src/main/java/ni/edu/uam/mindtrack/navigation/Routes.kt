package ni.edu.uam.mindtrack.navigation

sealed class Routes(val route: String) {
    object Login : Routes("login")
    object Register : Routes("register")
    object Onboarding : Routes("onboarding")
    object Home : Routes("home")
    object Scenario : Routes("scenario")
    object Result : Routes("result")
    object History : Routes("history")
    object Settings : Routes("settings")
}
