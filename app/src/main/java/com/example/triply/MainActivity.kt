package com.example.triply

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.triply.navigation.*
import com.example.triply.ui.screens.*
import com.example.triply.ui.theme.TriplyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TriplyTheme {
                TriplyApp()
            }
        }
    }
}

@Composable
fun TriplyApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = MyTripsDestination
    ) {
        composable<MyTripsDestination> {
            MyTripsScreen(
                onAddTripClick = {
                    navController.navigate(CreateEditTripDestination())
                },
                onTripClick = { tripId ->
                    navController.navigate(TripDetailsDestination(tripId))
                }
            )
        }

        composable<CreateEditTripDestination> { backStackEntry ->
            val destination: CreateEditTripDestination = backStackEntry.toRoute()
            CreateEditTripScreen(
                tripId = destination.tripId,
                navigateBack = { navController.popBackStack() }
            )
        }

        composable<TripDetailsDestination> { backStackEntry ->
            val destination: TripDetailsDestination = backStackEntry.toRoute()
            TripDetailsScreen(
                tripId = destination.tripId,
                onBackClick = { navController.popBackStack() },
                onEditTripClick = { tripId ->
                    navController.navigate(CreateEditTripDestination(tripId))
                },
                onPlacesClick = { tripId ->
                    navController.navigate(PlacesDestination(tripId))
                },
                onScheduleClick = { tripId ->
                    navController.navigate(ScheduleDestination(tripId))
                },
                onExpensesClick = { tripId ->
                    navController.navigate(ExpensesDestination(tripId))
                },
                onNotesClick = { tripId ->
                    navController.navigate(NotesDestination(tripId))
                },
                onPhotosClick = { tripId ->
                    navController.navigate(PhotosDestination(tripId))
                }
            )
        }

        composable<PlacesDestination> { backStackEntry ->
            val destination: PlacesDestination = backStackEntry.toRoute()
            PlacesScreen(
                tripId = destination.tripId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<ScheduleDestination> { backStackEntry ->
            val destination: ScheduleDestination = backStackEntry.toRoute()
            ScheduleScreen(
                tripId = destination.tripId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<ExpensesDestination> { backStackEntry ->
            val destination: ExpensesDestination = backStackEntry.toRoute()
            ExpensesScreen(
                tripId = destination.tripId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<NotesDestination> { backStackEntry ->
            val destination: NotesDestination = backStackEntry.toRoute()
            NotesScreen(
                tripId = destination.tripId,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable<PhotosDestination> { backStackEntry ->
            val destination: PhotosDestination = backStackEntry.toRoute()
            PhotosScreen(
                tripId = destination.tripId,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
