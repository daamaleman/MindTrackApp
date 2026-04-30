package ni.edu.uam.mindtrack.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ni.edu.uam.mindtrack.ui.screens.*
import ni.edu.uam.mindtrack.viewmodel.MindTrackViewModel

@Composable
fun MindTrackNavGraph() {
    val navController = rememberNavController()
    val viewModel: MindTrackViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.Login.route,
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
        composable(Routes.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.Register.route)
                }
            )
        }

        composable(Routes.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

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
    }
}
