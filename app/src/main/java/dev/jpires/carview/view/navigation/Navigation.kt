package dev.jpires.carview.view.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.jpires.carview.view.screens.CarScreen
import dev.jpires.carview.view.screens.LoginScreen
import dev.jpires.carview.viewmodel.ViewModel

@Composable
fun NavigationHost(viewModel: ViewModel) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.LoginScreen.route
    ) {
        composable(Screen.LoginScreen.route) {
            LoginScreen(viewModel, navController)
        }
        composable(Screen.CarScreen.route) {
            CarScreen(viewModel, navController)
        }
    }
}