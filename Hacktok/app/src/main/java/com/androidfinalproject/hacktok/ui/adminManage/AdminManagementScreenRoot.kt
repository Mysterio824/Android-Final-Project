package com.androidfinalproject.hacktok.ui.adminManage

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.androidfinalproject.hacktok.ui.adminManage.reportManagement.ReportManagementTab
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
        composable("adminManagement?tab={tab}") { backStackEntry ->
            val tabIndex = backStackEntry.arguments?.getString("tab")?.toIntOrNull() ?: 0
            AdminManagementScreen(
                navController = navController,
                modifier = modifier,
                onAction = viewModel::onAction,
                startTabIndex = tabIndex
            )
        }
        composable("userDetail/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            UserDetailScreen(
                userId = userId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToReportManagement = {
                    // Navigate to AdminManagementScreen with the Reports tab selected
                    navController.navigate("adminManagement?tab=1") {
                        popUpTo("adminManagement") { inclusive = true }
                    }
                }
            )
        }
    }
}