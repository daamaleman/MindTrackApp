package ni.edu.uam.mindtrack.navigation

sealed class Routes(val route: String) {
    object Login : Routes("login")
    object Register : Routes("register")
    object Home : Routes("home")
    object Scenario : Routes("scenario")
    object Result : Routes("result")
    object Statistics : Routes("statistics")
    object Achievements : Routes("achievements")
    object History : Routes("history")
    object Settings : Routes("settings")
    object Profile : Routes("profile")
    object EditProfile : Routes("edit_profile")
    object Achievements : Routes("achievements")
    object Statistics : Routes("statistics")
}
