package com.androidfinalproject.hacktok.router.graph

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.androidfinalproject.hacktok.router.routes.MainRoute
import com.androidfinalproject.hacktok.ui.post.PostDetailScreenRoot
import com.androidfinalproject.hacktok.ui.post.PostDetailViewModel
import com.androidfinalproject.hacktok.ui.search.SearchDashboardScreen
import com.androidfinalproject.hacktok.ui.search.SearchDashboardScreenRoot
import com.androidfinalproject.hacktok.ui.search.SearchViewModel
import org.bson.types.ObjectId

fun NavGraphBuilder.testGraph(
    navController: NavController,
) {
    navigation(
        startDestination = MainRoute.SearchDashboard.route,
        route = MainRoute.Graph.route
    ) {
        composable(
            route = MainRoute.SearchDashboard.route,
            enterTransition = {
                fadeIn(animationSpec = tween(300)) +
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Start,
                            animationSpec = tween(300)
                        )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300)) +
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.End,
                            animationSpec = tween(300)
                        )
            }
        ) {
            PostDetailScreenRoot (
                postId = ObjectId(),
                viewModel = PostDetailViewModel(),
                onNavigateBack = {},
                onUserProfileNavigate = {}
            )
        }
    }
}