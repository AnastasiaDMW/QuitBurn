package com.example.quitburn

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.quitburn.data.PermissionManager
import com.example.quitburn.ui.navigation.QuitBurnNavHost

@Composable
fun QuitBurnApp(
    permissionManager: PermissionManager,
    navController: NavHostController = rememberNavController()
) {
    QuitBurnNavHost(permissionManager = permissionManager, navController = navController)
}