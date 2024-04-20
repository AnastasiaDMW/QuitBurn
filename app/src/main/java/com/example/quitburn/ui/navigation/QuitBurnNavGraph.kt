package com.example.quitburn.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.quitburn.data.PermissionManager
import com.example.quitburn.ui.home.HomeScreen
import com.example.quitburn.ui.statistic.StatisticScreen

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
            HomeScreen({navController.navigate(StatisticDestination.route)}, permissionManager)
        }
        composable(route = StatisticDestination.route) {
            StatisticScreen(navigateBack = { navController.popBackStack() })
        }
    }
}