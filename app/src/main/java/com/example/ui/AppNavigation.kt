package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.data.DreamRepository
import com.example.ui.theme.*

data class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun AppNavigation(repository: DreamRepository) {
    val navController = rememberNavController()

    val bottomNavItems = listOf(
        BottomNavItem("home", "الرئيسية", Icons.Filled.Cloud, Icons.Outlined.Cloud),
        BottomNavItem("journal", "اليوميات", Icons.Filled.Book, Icons.Outlined.Book),
        BottomNavItem("stats", "الإحصائيات", Icons.Filled.Insights, Icons.Outlined.Insights),
        BottomNavItem("settings", "الإعدادات", Icons.Filled.Settings, Icons.Outlined.Settings)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        containerColor = DeepSpace
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = "onboarding",
                modifier = Modifier.fillMaxSize()
            ) {
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
                        onJournalClick = { navController.navigate("journal") },
                        onAddDreamClick = { navController.navigate("add_dream") },
                        onSleepClick = { navController.navigate("sleep") }
                    )
                }
                composable("journal") {
                    JournalScreen(
                        repository = repository,
                        onAddDream = { navController.navigate("add_dream") },
                        onDreamClick = { id -> navController.navigate("dream_detail/$id") }
                    )
                }
                composable("stats") {
                    StatsScreen(repository = repository)
                }
                composable("sleep") {
                    SleepScreen(
                        repository = repository,
                        onWakeUp = { durationHours ->
                            // Here we could navigate to add_dream with pre-filled sleep hours, but for simplicity:
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    )
                }
                composable("settings") {
                    SettingsScreen(
                        repository = repository,
                        onBack = { navController.popBackStack() }
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

            if (showBottomBar) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .align(androidx.compose.ui.Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth()
                        .glassmorphism(),
                ) {
                    NavigationBar(
                        containerColor = androidx.compose.ui.graphics.Color.Transparent,
                        contentColor = MoonWhite,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        tonalElevation = 0.dp
                    ) {
                        bottomNavItems.forEach { item ->
                            val isSelected = currentRoute == item.route
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.title,
                                        modifier = if (isSelected) Modifier.breathingPulse() else Modifier
                                    )
                                },
                                label = { Text(item.title) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = LucidBlue,
                                    selectedTextColor = LucidBlue,
                                    indicatorColor = AuroraPurple.copy(alpha = 0.3f), // Glow active state
                                    unselectedIconColor = GrayText,
                                    unselectedTextColor = GrayText
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
