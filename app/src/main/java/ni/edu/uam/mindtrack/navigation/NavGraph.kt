package ni.edu.uam.mindtrack.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ni.edu.uam.mindtrack.ui.components.MindTrackBottomBar
import ni.edu.uam.mindtrack.ui.screens.*
import ni.edu.uam.mindtrack.viewmodel.MindTrackViewModel

@Composable
fun MindTrackNavGraph(viewModel: MindTrackViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val onboardingCompleted by viewModel.onboardingCompleted.collectAsState()

    if (onboardingCompleted == null) return

    fun isForwardNavigation(initial: String?, target: String?): Boolean {
        val order = listOf(
            Routes.Onboarding.route,
            Routes.Login.route,
            Routes.Home.route,
            Routes.Statistics.route,
            Routes.Profile.route,
            Routes.Settings.route
        )
        val initialIdx = order.indexOf(initial)
        val targetIdx = order.indexOf(target)

        if (target == Routes.Scenario.route || target == Routes.Result.route) return true

        if (initialIdx != -1 && targetIdx != -1) {
            return targetIdx > initialIdx
        }

        return true
    }

    Scaffold(
        bottomBar = {
            if (currentRoute == Routes.Home.route || currentRoute == Routes.Statistics.route ||
                currentRoute == Routes.Profile.route || currentRoute == Routes.Settings.route) {
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
            startDestination = if (onboardingCompleted == true) Routes.Login.route else Routes.Onboarding.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                val target = targetState.destination.route
                val initial = initialState.destination.route
                val isForward = isForwardNavigation(initial, target)
                
                slideIntoContainer(
                    if (isForward) AnimatedContentTransitionScope.SlideDirection.Left 
                    else AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(500)
                )
            },
            exitTransition = {
                val target = targetState.destination.route
                val initial = initialState.destination.route
                val isForward = isForwardNavigation(initial, target)
                
                slideOutOfContainer(
                    if (isForward) AnimatedContentTransitionScope.SlideDirection.Left 
                    else AnimatedContentTransitionScope.SlideDirection.Right,
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
            composable(Routes.Onboarding.route) {
                OnboardingScreen(
                    viewModel = viewModel,
                    onFinish = {
                        navController.navigate(Routes.Login.route) {
                            popUpTo(Routes.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.Login.route) {
                LoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(Routes.Register.route)
                    },
                    onNavigateToOnboarding = {
                        navController.navigate(Routes.Onboarding.route)
                    }
                )
            }
            composable(Routes.Register.route) {
                RegisterScreen(
                    viewModel = viewModel,
                    onRegisterSuccess = {
                        navController.navigate(Routes.Home.route) {
                            popUpTo(Routes.Login.route) { inclusive = true }
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
            composable(Routes.Statistics.route) {
                StatisticsScreen(
                    viewModel = viewModel,
                    onStartSimulation = {
                        viewModel.resetSession()
                        navController.navigate(Routes.Scenario.route)
                    },
                    onOpenHistory = {
                        navController.navigate(Routes.History.route)
                    }
                )
            }
            composable(Routes.History.route) {
                HistoryScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel,
                    onEditProfile = { navController.navigate(Routes.EditProfile.route) },
                    onOpenAchievements = { navController.navigate(Routes.Achievements.route) },
                    onOpenStatistics = { navController.navigate(Routes.Statistics.route) },
                    onOpenSettings = { navController.navigate(Routes.Settings.route) },
                    onLogout = {
                        viewModel.logout()
                        navController.navigate(Routes.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onStartFirstSimulation = {
                        navController.navigate(Routes.Home.route)
                    }
                )
            }
            composable(Routes.EditProfile.route) {
                EditProfileScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }
            composable(Routes.Achievements.route) {
                AchievementsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.Settings.route) {
                SettingsScreen(viewModel = viewModel)
            }
        }
    }
}
