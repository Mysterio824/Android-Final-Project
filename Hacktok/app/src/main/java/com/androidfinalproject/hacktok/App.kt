package com.androidfinalproject.hacktok

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.androidfinalproject.hacktok.router.graph.authNavigation
import com.androidfinalproject.hacktok.router.graph.dashboardNavigation
import com.androidfinalproject.hacktok.router.graph.friendListNavigation
import com.androidfinalproject.hacktok.router.graph.testGraph
import com.androidfinalproject.hacktok.router.routes.DashboardRoute

import com.androidfinalproject.hacktok.router.routes.FriendListRoute
import com.androidfinalproject.hacktok.router.routes.MainRoute
import com.androidfinalproject.hacktok.ui.search.SearchViewModel
import com.androidfinalproject.hacktok.router.graph.authNavigation
import com.androidfinalproject.hacktok.router.graph.friendListNavigation
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

@Composable
@Preview
fun App() {
    MainAppTheme {
        val navController = rememberNavController()
        Log.d("Navigation", "Starting at: ${DashboardRoute.Graph.route}")
        NavHost(
            navController = navController,
            startDestination = DashboardRoute.Graph.route
        ) {
            dashboardNavigation(navController)
            authNavigation(navController)
//            friendListNavigation(navController)
        }

    }
}