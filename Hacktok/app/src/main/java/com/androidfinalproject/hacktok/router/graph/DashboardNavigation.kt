package com.androidfinalproject.hacktok.router.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.androidfinalproject.hacktok.router.routes.DashboardRoute
import com.androidfinalproject.hacktok.ui.mainDashboard.DashboardScreen

fun NavGraphBuilder.dashboardNavigation(navController: NavHostController) {
    composable(DashboardRoute.Graph.route) {
        DashboardScreen()
    }
}
