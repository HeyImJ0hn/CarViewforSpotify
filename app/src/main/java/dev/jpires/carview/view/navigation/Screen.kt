package dev.jpires.carview.view.navigation

sealed class Screen(val route: String) {
    data object LoginScreen : Screen("login_screen")
    data object CarScreen : Screen("car_screen")
}