package com.example.cp3406_assessment3_younghoocheon.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
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
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.cp3406_assessment3_younghoocheon.ui.components.screenBackground
import com.example.cp3406_assessment3_younghoocheon.ui.screens.AddCardScreen
import com.example.cp3406_assessment3_younghoocheon.ui.screens.CardsScreen
import com.example.cp3406_assessment3_younghoocheon.ui.screens.DepartmentScreen
import com.example.cp3406_assessment3_younghoocheon.ui.screens.ExploreScreen
import com.example.cp3406_assessment3_younghoocheon.ui.screens.FacultyDetailScreen
import com.example.cp3406_assessment3_younghoocheon.ui.screens.HomeScreen
import com.example.cp3406_assessment3_younghoocheon.ui.screens.LearnScreen
import com.example.cp3406_assessment3_younghoocheon.ui.screens.PracticeHubScreen
import com.example.cp3406_assessment3_younghoocheon.ui.screens.PracticeScreen
import com.example.cp3406_assessment3_younghoocheon.ui.screens.PrivacyScreen
import com.example.cp3406_assessment3_younghoocheon.ui.screens.ProfileScreen
import com.example.cp3406_assessment3_younghoocheon.ui.screens.ProgramDetailScreen
import com.example.cp3406_assessment3_younghoocheon.ui.screens.SettingsScreen
import com.example.cp3406_assessment3_younghoocheon.ui.screens.StatsScreen
import com.example.cp3406_assessment3_younghoocheon.ui.screens.TopicGuideScreen
import com.example.cp3406_assessment3_younghoocheon.ui.screens.WelcomeScreen
import com.example.cp3406_assessment3_younghoocheon.ui.screens.WordBuilderScreen

/** All route names and helpers that build routes with arguments. */
object Routes {
    const val WELCOME = "welcome"
    const val HOME = "home"
    const val EXPLORE = "explore"
    const val PRACTICE_HUB = "practice_hub"
    const val STATS = "stats"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
    const val PRIVACY = "privacy"
    const val CARDS = "cards"
    const val ADD_CARD = "add_card"
    const val LEARN = "learn"
    const val DEPARTMENT = "department"

    const val TOPIC_ARG = "topic"
    const val ID_ARG = "id"
    const val PRACTICE = "practice/{topic}"
    const val BUILDER = "builder/{topic}"
    const val STAFF = "staff/{id}"
    const val PROGRAM = "program/{id}"
    const val TOPIC_GUIDE = "topic_guide/{topic}"

    /** Links such as memorydesk://topic/Privacy open the app on that screen (see AndroidManifest.xml). */
    const val LINK_BASE = "memorydesk://"

    fun practice(topic: String) = "practice/$topic"
    fun builder(topic: String) = "builder/$topic"
    fun staff(id: String) = "staff/$id"
    fun program(id: String) = "program/$id"
    fun topicGuide(topic: String) = "topic_guide/$topic"
}

private data class Tab(val route: String, val label: String, val icon: ImageVector)

private val tabs = listOf(
    Tab(Routes.HOME, "Home", Icons.Filled.Home),
    Tab(Routes.EXPLORE, "Explore", Icons.Filled.Search),
    Tab(Routes.PRACTICE_HUB, "Practice", Icons.Filled.PlayArrow),
    Tab(Routes.STATS, "Progress", Icons.Filled.Star),
    Tab(Routes.PROFILE, "Profile", Icons.Filled.Person),
)

private fun NavHostController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

private fun stringArgument(name: String): List<NamedNavArgument> =
    listOf(navArgument(name) { type = NavType.StringType })

/** The navigation graph: a bottom bar for the five main tabs and full screens for details and activities. */
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = tabs.any { it.route == currentRoute }
    val back: () -> Unit = { navController.popBackStack() }

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
                    onStartPractice = { navController.navigate(Routes.practice(it)) },
                    onOpenPracticeHub = { navController.navigateToTab(Routes.PRACTICE_HUB) },
                    onOpenExplore = { navController.navigateToTab(Routes.EXPLORE) },
                    onOpenStaff = { navController.navigate(Routes.staff(it)) },
                    onOpenLearn = { navController.navigate(Routes.LEARN) },
                    onOpenCards = { navController.navigate(Routes.CARDS) },
                    onAddCard = { navController.navigate(Routes.ADD_CARD) },
                )
            }
            composable(Routes.EXPLORE) {
                ExploreScreen(
                    onOpenStaff = { navController.navigate(Routes.staff(it)) },
                    onOpenProgram = { navController.navigate(Routes.program(it)) },
                    onOpenDepartment = { navController.navigate(Routes.DEPARTMENT) },
                    onOpenLearn = { navController.navigate(Routes.LEARN) },
                )
            }
            composable(
                route = Routes.PRACTICE_HUB,
                deepLinks = listOf(navDeepLink { uriPattern = Routes.LINK_BASE + "practice" }),
            ) {
                PracticeHubScreen(
                    onStartFlashcards = { navController.navigate(Routes.practice(it)) },
                    onStartBuilder = { navController.navigate(Routes.builder(it)) },
                    onOpenGuide = { navController.navigate(Routes.topicGuide(it)) },
                )
            }
            composable(Routes.STATS) { StatsScreen() }
            composable(Routes.PROFILE) {
                ProfileScreen(
                    onOpenCards = { navController.navigate(Routes.CARDS) },
                    onOpenExplore = { navController.navigateToTab(Routes.EXPLORE) },
                    onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                    onOpenPrivacy = { navController.navigate(Routes.PRIVACY) },
                )
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    onBack = back,
                    onOpenPrivacy = { navController.navigate(Routes.PRIVACY) },
                )
            }
            composable(Routes.PRIVACY) { PrivacyScreen(onBack = back) }
            composable(Routes.CARDS) {
                CardsScreen(
                    onBack = back,
                    onAddCard = { navController.navigate(Routes.ADD_CARD) },
                )
            }
            composable(Routes.ADD_CARD) { AddCardScreen(onBack = back) }
            composable(
                route = Routes.LEARN,
                deepLinks = listOf(navDeepLink { uriPattern = Routes.LINK_BASE + "learn" }),
            ) { LearnScreen(onBack = back) }
            composable(Routes.DEPARTMENT) {
                DepartmentScreen(
                    onBack = back,
                    onOpenStaff = { navController.navigate(Routes.staff(it)) },
                )
            }
            composable(
                route = Routes.PRACTICE,
                arguments = stringArgument(Routes.TOPIC_ARG),
            ) { entry ->
                PracticeScreen(
                    topic = entry.arguments?.getString(Routes.TOPIC_ARG) ?: "All",
                    onClose = back,
                )
            }
            composable(
                route = Routes.TOPIC_GUIDE,
                arguments = stringArgument(Routes.TOPIC_ARG),
                deepLinks = listOf(navDeepLink { uriPattern = Routes.LINK_BASE + "topic/{topic}" }),
            ) { entry ->
                val topic = entry.arguments?.getString(Routes.TOPIC_ARG).orEmpty()
                TopicGuideScreen(
                    topic = topic,
                    onBack = back,
                    onPractice = { navController.navigate(Routes.practice(topic)) },
                )
            }
            composable(
                route = Routes.BUILDER,
                arguments = stringArgument(Routes.TOPIC_ARG),
            ) { entry ->
                WordBuilderScreen(
                    topic = entry.arguments?.getString(Routes.TOPIC_ARG) ?: "All",
                    onClose = back,
                )
            }
            composable(
                route = Routes.STAFF,
                arguments = stringArgument(Routes.ID_ARG),
            ) { entry ->
                FacultyDetailScreen(
                    staffId = entry.arguments?.getString(Routes.ID_ARG).orEmpty(),
                    onBack = back,
                )
            }
            composable(
                route = Routes.PROGRAM,
                arguments = stringArgument(Routes.ID_ARG),
            ) { entry ->
                ProgramDetailScreen(
                    programId = entry.arguments?.getString(Routes.ID_ARG).orEmpty(),
                    onBack = back,
                    onOpenStaff = { navController.navigate(Routes.staff(it)) },
                )
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
            .padding(horizontal = 12.dp),
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
