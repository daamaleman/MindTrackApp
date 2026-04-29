package ni.edu.uam.mindtrack.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ni.edu.uam.mindtrack.ui.components.MindTrackBottomBar
import ni.edu.uam.mindtrack.ui.screens.HistoryScreen
import ni.edu.uam.mindtrack.ui.screens.HomeScreen
import ni.edu.uam.mindtrack.ui.screens.ResultScreen
import ni.edu.uam.mindtrack.ui.screens.ScenarioScreen
import ni.edu.uam.mindtrack.ui.screens.SettingsScreen
import ni.edu.uam.mindtrack.ui.screens.SettingsScreen
import ni.edu.uam.mindtrack.viewmodel.MindTrackViewModel

@Composable
fun MindTrackNavGraph() {
    val navController = rememberNavController()
    val viewModel: MindTrackViewModel = viewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            // Show bottom bar on Home, History, and Settings
            if (currentRoute == Routes.Home.route || currentRoute == Routes.History.route || currentRoute == Routes.Settings.route) {
                MindTrackBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.Home.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(500)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(500)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(500)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(500)
                )
            }
        ) {
            composable(Routes.Home.route) {
                HomeScreen(
                    onStartSimulation = {
                        viewModel.resetSession()
                        navController.navigate(Routes.Scenario.route)
                    },
                    onViewHistory = {
                        navController.navigate(Routes.History.route)
                    }
                )
            }
            composable(Routes.Scenario.route) {
                ScenarioScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onFinish = {
                        navController.navigate(Routes.Result.route) {
                            popUpTo(Routes.Home.route)
                        }
                    }
                )
            }
            composable(Routes.Result.route) {
                ResultScreen(
                    viewModel = viewModel,
                    onBackHome = {
                        navController.popBackStack(Routes.Home.route, inclusive = false)
                    }
                )
            }
            composable(Routes.History.route) {
                HistoryScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.Settings.route) {
                SettingsScreen()
            }
        }
    }
}
