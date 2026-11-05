package com.example.cp3406_assessment3_younghoocheon.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cp3406_assessment3_younghoocheon.ui.components.screenBackground
import com.example.cp3406_assessment3_younghoocheon.ui.screens.HomeScreen
import com.example.cp3406_assessment3_younghoocheon.ui.screens.LearnScreen
import com.example.cp3406_assessment3_younghoocheon.ui.screens.LibraryScreen
import com.example.cp3406_assessment3_younghoocheon.ui.screens.PracticeScreen
import com.example.cp3406_assessment3_younghoocheon.ui.screens.PrivacyScreen
import com.example.cp3406_assessment3_younghoocheon.ui.screens.SettingsScreen
import com.example.cp3406_assessment3_younghoocheon.ui.screens.StatsScreen
import com.example.cp3406_assessment3_younghoocheon.ui.screens.WelcomeScreen

object Routes {
    const val WELCOME = "welcome"
    const val HOME = "home"
    const val LEARN = "learn"
    const val LIBRARY = "library"
    const val STATS = "stats"
    const val SETTINGS = "settings"
    const val PRIVACY = "privacy"
    const val TOPIC_ARG = "topic"
    const val PRACTICE = "practice/{topic}"

    fun practice(topic: String) = "practice/$topic"
}

private data class Tab(val route: String, val label: String, val icon: ImageVector)

private val tabs = listOf(
    Tab(Routes.HOME, "Home", Icons.Filled.Home),
    Tab(Routes.LEARN, "Learn", Icons.Filled.PlayArrow),
    Tab(Routes.LIBRARY, "Library", Icons.Filled.Search),
    Tab(Routes.STATS, "Stats", Icons.Filled.Star),
    Tab(Routes.SETTINGS, "Settings", Icons.Filled.Settings),
)

private fun NavHostController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = tabs.any { it.route == currentRoute }

    Scaffold(
        modifier = Modifier.screenBackground(),
        containerColor = Color.Transparent,
        bottomBar = {
            if (showBottomBar) {
                FloatingTabBar(
                    currentRoute = currentRoute,
                    onSelect = { navController.navigateToTab(it) },
                )
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.WELCOME,
            modifier = Modifier.padding(padding),
        ) {
            composable(Routes.WELCOME) {
                WelcomeScreen(
                    onGetStarted = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.WELCOME) { inclusive = true }
                        }
                    },
                )
            }
            composable(Routes.HOME) {
                HomeScreen(
                    onStartPractice = { topic -> navController.navigate(Routes.practice(topic)) },
                    onOpenLearn = { navController.navigateToTab(Routes.LEARN) },
                )
            }
            composable(Routes.LEARN) { LearnScreen() }
            composable(Routes.LIBRARY) { LibraryScreen() }
            composable(Routes.STATS) { StatsScreen() }
            composable(Routes.SETTINGS) {
                SettingsScreen(onOpenPrivacy = { navController.navigate(Routes.PRIVACY) })
            }
            composable(
                route = Routes.PRACTICE,
                arguments = listOf(navArgument(Routes.TOPIC_ARG) { type = NavType.StringType }),
            ) { entry ->
                PracticeScreen(
                    topic = entry.arguments?.getString(Routes.TOPIC_ARG) ?: "All",
                    onClose = { navController.popBackStack() },
                )
            }
            composable(Routes.PRIVACY) {
                PrivacyScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}

@Composable
private fun FloatingTabBar(currentRoute: String?, onSelect: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(6.dp)
                .selectableGroup(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            tabs.forEach { tab ->
                TabItem(
                    tab = tab,
                    selected = currentRoute == tab.route,
                    onClick = { onSelect(tab.route) },
                )
            }
        }
    }
}

@Composable
private fun TabItem(tab: Tab, selected: Boolean, onClick: () -> Unit) {
    val container = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
    val content = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier
            .heightIn(min = 48.dp)
            .clip(CircleShape)
            .background(container)
            .selectable(selected = selected, role = Role.Tab, onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = tab.icon,
            contentDescription = if (selected) null else tab.label,
            tint = content,
        )
        AnimatedVisibility(visible = selected) {
            Row {
                Spacer(Modifier.width(6.dp))
                Text(
                    text = tab.label,
                    style = MaterialTheme.typography.labelLarge,
                    color = content,
                    maxLines = 1,
                )
            }
        }
    }
}