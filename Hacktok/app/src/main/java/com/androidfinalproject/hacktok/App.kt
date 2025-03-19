package com.androidfinalproject.hacktok

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.androidfinalproject.hacktok.router.routes.MainRoute // Import MainRoute
import com.androidfinalproject.hacktok.router.graph.testGraph // Import testGraph
import com.androidfinalproject.hacktok.ui.theme.LoginAppTheme

@Composable
@Preview
fun App() {
    LoginAppTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = MainRoute.MainGraph.route // Thay đổi startDestination
        ) {
            testGraph(navController) // Chỉ cần testGraph, không cần authNavigation
        }
    }
}