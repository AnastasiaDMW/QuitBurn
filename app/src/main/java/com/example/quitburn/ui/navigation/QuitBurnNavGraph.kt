package com.example.quitburn.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.quitburn.data.PermissionManager
import com.example.quitburn.ui.home.HomeScreen

@Composable
fun QuitBurnNavHost(
    permissionManager: PermissionManager,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route
    ){
        composable(route = HomeDestination.route) {
            HomeScreen(permissionManager)
        }
    }
}