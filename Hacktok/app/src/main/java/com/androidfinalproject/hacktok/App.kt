package com.androidfinalproject.hacktok

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.androidfinalproject.hacktok.router.graph.authNavigation
import com.androidfinalproject.hacktok.router.graph.friendListNavigation
import com.androidfinalproject.hacktok.router.graph.testGraph
import com.androidfinalproject.hacktok.router.routes.FriendListRoute
import com.androidfinalproject.hacktok.router.routes.MainRoute
import com.androidfinalproject.hacktok.ui.search.SearchViewModel
import com.androidfinalproject.hacktok.ui.theme.LoginAppTheme
import com.androidfinalproject.hacktok.router.routes.FriendListRoute
import com.androidfinalproject.hacktok.router.graph.authNavigation
import com.androidfinalproject.hacktok.router.graph.friendListNavigation
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

@Composable
@Preview
fun App() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = MainRoute.Graph.route
    ) {
//            authNavigation(navController)
//            friendListNavigation(navController)
        testGraph(navController)
    }
}