package com.androidfinalproject.hacktok.router.graph

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.androidfinalproject.hacktok.router.routes.AdminRoute
import com.androidfinalproject.hacktok.ui.adminManage.AdminManagementScreenRoot
import com.androidfinalproject.hacktok.ui.adminManage.AdminManagementViewModel

fun NavGraphBuilder.adminNavigation(navController: NavController) {
    navigation(
        startDestination = AdminRoute.AdminDashboard.route,
        route = AdminRoute.Graph.route
    ) {
        composable(
            route = AdminRoute.AdminDashboard.route,
            enterTransition = {
                fadeIn(animationSpec = tween(300)) +
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Start,
                            animationSpec = tween(300, easing = FastOutSlowInEasing)
                        )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300)) +
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.End,
                            animationSpec = tween(300, easing = FastOutSlowInEasing)
                        )
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(300)) +
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Start,
                            animationSpec = tween(300, easing = FastOutSlowInEasing)
                        )
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(300)) +
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.End,
                            animationSpec = tween(300, easing = FastOutSlowInEasing)
                        )
            }
        ) {
            AdminManagementScreenRoot(
                viewModel = AdminManagementViewModel(

                ),
                onPostNavigation = {},
                onUserNavigation = {},
                onCommentNavigation = {},
                onReportNavigation = {}
            )
        }
    }
}