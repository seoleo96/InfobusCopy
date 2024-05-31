package com.example.infobuscopy.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.infobuscopy.presentation.screens.MainScreen
import com.example.infobuscopy.presentation.screens.SecondScreen

sealed class NavRoute(val route: String) {
    object Main : NavRoute("main")
    object SecondScreen : NavRoute("second")
}

@Composable
fun NavHostController() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = NavRoute.Main.route) {
        composable(NavRoute.Main.route) {
            MainScreen()
        }
        composable(NavRoute.SecondScreen.route) {
            SecondScreen()
        }
    }
}