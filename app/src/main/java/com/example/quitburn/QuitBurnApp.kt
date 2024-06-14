package com.example.quitburn

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.quitburn.data.PermissionManager
import com.example.quitburn.ui.home.HomeViewModel
import com.example.quitburn.ui.navigation.QuitBurnNavHost

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun QuitBurnApp(
    permissionManager: PermissionManager,
    viewModel: HomeViewModel,
    navController: NavHostController = rememberNavController()
) {
    QuitBurnNavHost(
        permissionManager = permissionManager,
        viewModel = viewModel,
        navController = navController
    )
}