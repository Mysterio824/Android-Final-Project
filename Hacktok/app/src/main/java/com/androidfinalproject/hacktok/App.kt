package com.androidfinalproject.hacktok

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.androidfinalproject.hacktok.router.routes.AuthRoute
import com.androidfinalproject.hacktok.router.graph.authNavigation
import com.androidfinalproject.hacktok.ui.theme.LoginAppTheme

@Composable
@Preview
fun App() {
    LoginAppTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = AuthRoute.AuthGraph.route
        ) {
            authNavigation(navController)
//            mainNavigation(navController)
        }
    }
}

