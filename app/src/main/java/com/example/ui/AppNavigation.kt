package com.example.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.DreamRepository

@Composable
fun AppNavigation(repository: DreamRepository) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "onboarding") {
        composable("onboarding") {
            OnboardingScreen(onFinish = {
                navController.navigate("home") {
                    popUpTo("onboarding") { inclusive = true }
                }
            })
        }
        composable("home") {
            HomeScreen(
                repository = repository,
                onAddDream = { navController.navigate("add_dream") },
                onDreamClick = { id -> navController.navigate("dream_detail/$id") }
            )
        }
        composable("add_dream") {
            AddDreamScreen(
                repository = repository,
                onBack = { navController.popBackStack() },
                onSave = { navController.popBackStack() }
            )
        }
        composable(
            route = "dream_detail/{dreamId}",
            arguments = listOf(navArgument("dreamId") { type = NavType.IntType })
        ) { backStackEntry ->
            val dreamId = backStackEntry.arguments?.getInt("dreamId") ?: 0
            DreamDetailScreen(
                dreamId = dreamId,
                repository = repository,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
