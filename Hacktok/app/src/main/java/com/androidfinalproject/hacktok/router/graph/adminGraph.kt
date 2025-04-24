package com.androidfinalproject.hacktok.router.graph

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseInOut
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
import com.androidfinalproject.hacktok.ui.statistic.StatisticsScreenRoot
import com.androidfinalproject.hacktok.ui.adminManage.userManagement.UserDetailScreen
import com.androidfinalproject.hacktok.ui.statistic.StatisticViewModel
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

fun NavGraphBuilder.adminNavigation(navController: NavController) {
    navigation(
        startDestination = AdminRoute.Statistic.route,
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
            AdminManagementScreenRoot()
        }

        composable(
            route = AdminRoute.Statistic.route,
            enterTransition = {
                fadeIn(animationSpec = tween(300)) +
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(300, easing = EaseInOut)
                        )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300)) +
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(300, easing = EaseInOut)
                        )
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(300)) +
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(300, easing = EaseInOut)
                        )
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(300)) +
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(300, easing = EaseInOut)
                        )
            }
        ) {
            StatisticsScreenRoot(
                viewModel = StatisticViewModel(),
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(AdminRoute.UserDetail.route) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            UserDetailScreen(
                userId = userId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun AdminGraph(
    navController: NavHostController,
    startDestination: String = AdminRoute.AdminDashboard.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(AdminRoute.AdminDashboard.route) {
            AdminManagementScreenRoot()
        }
        composable(AdminRoute.Statistic.route) {
            StatisticsScreenRoot(
                viewModel = StatisticViewModel(),
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(AdminRoute.UserDetail.route) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            UserDetailScreen(
                userId = userId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}