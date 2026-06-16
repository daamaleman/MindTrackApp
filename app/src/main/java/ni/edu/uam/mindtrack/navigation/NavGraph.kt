package ni.edu.uam.mindtrack.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ni.edu.uam.mindtrack.data.OnboardingPreferences
import ni.edu.uam.mindtrack.engine.AuthManager
import ni.edu.uam.mindtrack.ui.components.MindTrackBottomBar
import ni.edu.uam.mindtrack.ui.screens.*
import ni.edu.uam.mindtrack.ui.theme.MindTrackMotion
import ni.edu.uam.mindtrack.viewmodel.EditProfileViewModel
import ni.edu.uam.mindtrack.viewmodel.MindTrackViewModel
import ni.edu.uam.mindtrack.viewmodel.MindTrackViewModelFactory
import ni.edu.uam.mindtrack.viewmodel.ProfileViewModel
import ni.edu.uam.mindtrack.viewmodel.AdaptiveQuestionnaireViewModel
import ni.edu.uam.mindtrack.ui.screens.QuestionnaireScreen

@Composable
fun MindTrackNavGraph(viewModel: MindTrackViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val onboardingCompleted by viewModel.onboardingCompleted.collectAsState()
    val currentUser by AuthManager.currentUser.collectAsState()
    val context = LocalContext.current
    val factory = MindTrackViewModelFactory(OnboardingPreferences(context), context)

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
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
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
            startDestination = when {
                onboardingCompleted != true -> Routes.Onboarding.route
                currentUser != null && currentUser?.id != null -> Routes.Home.route
                else -> Routes.Login.route
            },
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding()),
            enterTransition = {
                MindTrackMotion.navEnter(
                    isForward = isForwardNavigation(
                        initialState.destination.route,
                        targetState.destination.route
                    )
                )
            },
            exitTransition = {
                MindTrackMotion.navExit(
                    isForward = isForwardNavigation(
                        initialState.destination.route,
                        targetState.destination.route
                    )
                )
            },
            popEnterTransition = {
                MindTrackMotion.navEnter(isForward = false)
            },
            popExitTransition = {
                MindTrackMotion.navExit(isForward = false)
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
                    viewModel = viewModel,
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
                val profileViewModel: ProfileViewModel = viewModel(factory = factory)
                ProfileScreen(
                    viewModel = viewModel,
                    profileViewModel = profileViewModel,
                    onEditProfile = { navController.navigate(Routes.EditProfile.route) },
                    onOpenAchievements = { navController.navigate(Routes.Achievements.route) },
                    onOpenStatistics = { navController.navigate(Routes.Statistics.route) },
                    onOpenSettings = { navController.navigate(Routes.Settings.route) },
                    onOpenQuestionnaire = { navController.navigate(Routes.Questionnaire.route) },
                    onLogout = {
                        AuthManager.logout()
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
            composable(Routes.Questionnaire.route) {
                // ViewModel local para el cuestionario adaptativo
                val questionnaireVm: AdaptiveQuestionnaireViewModel = viewModel()
                QuestionnaireScreen(viewModel = questionnaireVm, onFinish = { report ->
                    // Guardar el informe como sesión y mostrar pantalla de resultados dedicada
                    viewModel.addQuestionnaireSession(report)
                    navController.navigate(Routes.QuestionnaireResult.route) {
                        popUpTo(Routes.Home.route)
                    }
                })
            }

            composable(Routes.QuestionnaireResult.route) {
                // Mostrar el informe guardado en MindTrackViewModel
                val report by viewModel.lastQuestionnaireReport.collectAsState()
                QuestionnaireResultScreen(report = report, onBack = {
                    navController.navigate(Routes.Profile.route) {
                        popUpTo(Routes.Home.route)
                    }
                })
            }
            composable(Routes.EditProfile.route) {
                val editProfileViewModel: EditProfileViewModel = viewModel(factory = factory)
                val profileViewModel: ProfileViewModel = viewModel(factory = factory)
                EditProfileScreen(
                    editProfileViewModel = editProfileViewModel,
                    profileViewModel = profileViewModel,
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
