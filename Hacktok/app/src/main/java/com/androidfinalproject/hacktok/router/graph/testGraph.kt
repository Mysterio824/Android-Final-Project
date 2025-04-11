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
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.router.routes.TestRoute
import com.androidfinalproject.hacktok.ui.friendList.FriendListAction
import com.androidfinalproject.hacktok.ui.friendList.FriendListScreenRoot
import com.androidfinalproject.hacktok.ui.friendList.FriendListViewModel
import com.androidfinalproject.hacktok.ui.post.PostDetailScreenRoot
import com.androidfinalproject.hacktok.ui.post.PostDetailViewModel
import com.androidfinalproject.hacktok.ui.statistic.StatisticViewModel
import com.androidfinalproject.hacktok.ui.statistic.StatisticsScreenRoot

fun NavGraphBuilder.testNavigation(navController: NavController) {
    navigation(
        startDestination = TestRoute.screen1.route,
        route = TestRoute.Graph.route
    ) {
        composable(
            route = TestRoute.screen1.route,
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
            FriendListScreenRoot (
                viewModel = FriendListViewModel(""),
                onUserProfileView = {},
                onChatWithFriend = {}
            )

        }

        composable(
            route = TestRoute.screen2.route,
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
            StatisticsScreenRoot (
                viewModel = StatisticViewModel(),
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}