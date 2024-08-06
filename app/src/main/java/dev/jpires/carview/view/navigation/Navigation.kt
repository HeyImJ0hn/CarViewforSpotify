package dev.jpires.carview.view.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.jpires.carview.ui.theme.CarViewForSpotifyTheme
import dev.jpires.carview.view.screens.CarScreen
import dev.jpires.carview.view.screens.LoginScreen
import dev.jpires.carview.viewmodel.ViewModel

@Composable
fun NavigationHost(viewModel: ViewModel, startDestination: String) {
    val navController = rememberNavController()
    val themeMode by viewModel.themeMode.collectAsState()
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.LoginScreen.route) {
            CarViewForSpotifyTheme(themeMode) {
                LoginScreen(viewModel, navController)
            }
        }
        composable(Screen.CarScreen.route) {
            CarViewForSpotifyTheme(themeMode) {
                CarScreen(viewModel, navController)
            }
        }
    }
}