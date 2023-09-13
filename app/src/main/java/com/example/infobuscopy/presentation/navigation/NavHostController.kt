package com.example.infobuscopy.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.infobuscopy.presentation.screens.MainScreen

sealed class NavRoute(val route: String) {
    object Main : NavRoute("main")
}

@Composable
fun NavHostController() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = NavRoute.Main.route) {
        composable(NavRoute.Main.route) {
            MainScreen()
        }
    }
}