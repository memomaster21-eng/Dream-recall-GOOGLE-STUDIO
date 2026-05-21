package com.example.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.data.DreamRepository

@Composable
fun AppNavigation(
    dreamRepository: DreamRepository,
    isFirstLaunch: Boolean,
    onCompleteOnboarding: () -> Unit
) {
    val navController = rememberNavController()
    val startDestination = if (isFirstLaunch) "onboarding" else "home"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("onboarding") {
            OnboardingScreen(onFinish = {
                onCompleteOnboarding()
                navController.navigate("home") {
                    popUpTo("onboarding") { inclusive = true }
                }
            })
        }
        composable("home") {
            HomeScreen(
                repository = dreamRepository,
                onAddDream = { navController.navigate("add") },
                onDreamClick = { id -> navController.navigate("detail/$id") }
            )
        }
        composable("add") {
            AddDreamScreen(
                repository = dreamRepository,
                onBack = { navController.popBackStack() },
                onSave = { navController.popBackStack() }
            )
        }
        composable(
            "detail/{dreamId}",
            arguments = listOf(navArgument("dreamId") { type = NavType.IntType })
        ) { backStackEntry ->
            val dreamId = backStackEntry.arguments?.getInt("dreamId") ?: 0
            DreamDetailScreen(
                dreamId = dreamId,
                repository = dreamRepository,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
