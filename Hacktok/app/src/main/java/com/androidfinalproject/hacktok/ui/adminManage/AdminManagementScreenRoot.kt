package com.androidfinalproject.hacktok.ui.adminManage

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.androidfinalproject.hacktok.ui.adminManage.userManagement.UserDetailScreen

@Composable
fun AdminManagementScreenRoot(
    modifier: Modifier = Modifier,
    viewModel: AdminManagementViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "adminManagement"
    ) {
        composable("adminManagement") {
            AdminManagementScreen(
                navController = navController,
                modifier = modifier,
                onAction = viewModel::onAction
            )
        }
        composable("userDetail/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            UserDetailScreen(
                userId = userId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}