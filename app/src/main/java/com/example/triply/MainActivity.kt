package com.example.triply

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.triply.navigation.*
import com.example.triply.ui.screens.*
import com.example.triply.ui.theme.TriplyTheme
import com.example.triply.ui.viewmodels.ProfileViewModel
import com.example.triply.ui.viewmodels.ProfileViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val repository = (context.applicationContext as TriplyApplication).repository
            val profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(repository))
            val profileUiState by profileViewModel.uiState.collectAsState()

            val useDarkTheme = profileUiState.isDarkMode ?: isSystemInDarkTheme()

            TriplyTheme(darkTheme = useDarkTheme) {
                TriplyApp()
            }
        }
    }
}

private data class BottomNavItem(
    val nameResId: Int,
    val route: Any,
    val icon: ImageVector
)

@Composable
fun TriplyApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = remember {
        listOf(
            BottomNavItem(R.string.nav_trips, MyTripsDestination, Icons.Default.Home),
            BottomNavItem(R.string.nav_calendar, CalendarDestination, Icons.Default.DateRange),
            BottomNavItem(R.string.nav_stats, StatisticsDestination, Icons.Default.Star),
            BottomNavItem(R.string.nav_profile, ProfileDestination, Icons.Default.AccountCircle)
        )
    }

    val shouldShowBottomBar = bottomNavItems.any { item ->
        currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true
    }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = shouldShowBottomBar,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                NavigationBar(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .clip(RoundedCornerShape(32.dp)),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 12.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true
                        val label = stringResource(item.nameResId)
                        
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
                                    item.icon,
                                    contentDescription = label,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MyTripsDestination,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(tween(300)) + slideInHorizontally(tween(300)) { 200 } },
            exitTransition = { fadeOut(tween(300)) + slideOutHorizontally(tween(300)) { -200 } },
            popEnterTransition = { fadeIn(tween(300)) + slideInHorizontally(tween(300)) { -200 } },
            popExitTransition = { fadeOut(tween(300)) + slideOutHorizontally(tween(300)) { 200 } }
        ) {
            composable<MyTripsDestination> {
                MyTripsScreen(
                    onAddTripClick = { navController.navigate(CreateEditTripDestination()) },
                    onTripClick = { navController.navigate(TripDetailsDestination(it)) }
                )
            }
            composable<CalendarDestination> { CalendarScreen() }
            composable<StatisticsDestination> { StatisticsScreen() }
            composable<ProfileDestination> { ProfileScreen() }

            composable<CreateEditTripDestination> {
                CreateEditTripScreen(
                    tripId = it.toRoute<CreateEditTripDestination>().tripId,
                    navigateBack = { navController.popBackStack() }
                )
            }

            composable<TripDetailsDestination> {
                val tripId = it.toRoute<TripDetailsDestination>().tripId
                TripDetailsScreen(
                    tripId = tripId,
                    onBackClick = { navController.popBackStack() },
                    onEditTripClick = { navController.navigate(CreateEditTripDestination(it)) },
                    onPlacesClick = { navController.navigate(PlacesDestination(it)) },
                    onScheduleClick = { navController.navigate(ScheduleDestination(it)) },
                    onExpensesClick = { navController.navigate(ExpensesDestination(it)) },
                    onNotesClick = { navController.navigate(NotesDestination(it)) },
                    onPhotosClick = { navController.navigate(PhotosDestination(it)) },
                    onPackingListClick = { navController.navigate(PackingListDestination(it)) },
                    onDocumentsClick = { navController.navigate(DocumentsDestination(it)) }
                )
            }

            composable<PlacesDestination> {
                PlacesScreen(
                    tripId = it.toRoute<PlacesDestination>().tripId,
                    onBackClick = { navController.popBackStack() },
                    onViewMapClick = { navController.navigate(TripMapDestination(it)) }
                )
            }

            composable<TripMapDestination> {
                TripMapScreen(
                    tripId = it.toRoute<TripMapDestination>().tripId,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable<ScheduleDestination> {
                ScheduleScreen(
                    tripId = it.toRoute<ScheduleDestination>().tripId,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable<ExpensesDestination> {
                ExpensesScreen(
                    tripId = it.toRoute<ExpensesDestination>().tripId,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable<NotesDestination> {
                NotesScreen(
                    tripId = it.toRoute<NotesDestination>().tripId,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable<PhotosDestination> {
                PhotosScreen(
                    tripId = it.toRoute<PhotosDestination>().tripId,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable<PackingListDestination> {
                PackingListScreen(
                    tripId = it.toRoute<PackingListDestination>().tripId,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable<DocumentsDestination> {
                DocumentsScreen(
                    tripId = it.toRoute<DocumentsDestination>().tripId,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
