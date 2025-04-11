package com.androidfinalproject.hacktok

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.androidfinalproject.hacktok.router.graph.adminNavigation
import com.androidfinalproject.hacktok.router.graph.authNavigation
import com.androidfinalproject.hacktok.router.graph.mainNavigation
import com.androidfinalproject.hacktok.router.graph.testNavigation
import com.androidfinalproject.hacktok.router.routes.AuthRoute
import com.androidfinalproject.hacktok.router.routes.MainRoute
import com.androidfinalproject.hacktok.router.routes.TestRoute

@Composable
@Preview
fun App() {
    val navController = rememberNavController()
    Log.d("Navigation", "Starting at: ${AuthRoute.Graph.route}")
    NavHost(
        navController = navController,
        startDestination = AuthRoute.Graph.route
    ) {
        authNavigation(navController)
        adminNavigation(navController)
        mainNavigation(navController)
        testNavigation(navController)
    }
}