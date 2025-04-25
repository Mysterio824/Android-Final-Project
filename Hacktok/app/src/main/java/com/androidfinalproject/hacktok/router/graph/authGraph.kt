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
import com.androidfinalproject.hacktok.router.routes.AuthRoute
import com.androidfinalproject.hacktok.router.routes.MainRoute
import com.androidfinalproject.hacktok.ui.auth.AuthScreenRoot
import com.androidfinalproject.hacktok.ui.auth.AuthViewModel
import com.androidfinalproject.hacktok.ui.forgotPassword.ForgotPasswordScreenRoot

fun NavGraphBuilder.authNavigation(
    navController: NavController,
    onGoogleSignInClicked: () -> Unit,
    authViewModel: AuthViewModel
) {
    navigation(
        startDestination = AuthRoute.Login.route,
        route = AuthRoute.Graph.route
    ) {
        composable(
            route = AuthRoute.Login.route,
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
            AuthScreenRoot(
                viewModel = authViewModel,
                onLoginSuccess = { isAdmin ->
                    if (isAdmin) {
                        navController.navigate(AdminRoute.Graph.route) {
                            popUpTo(AuthRoute.Graph.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(MainRoute.Graph.route) {
                            popUpTo(AuthRoute.Graph.route) { inclusive = true }
                        }
                    }
                },
                onForgetPassword = {
                    navController.navigate(AuthRoute.ForgotPassword.route)
                },
                onGoogleSignInClicked = onGoogleSignInClicked
            )
        }

        composable(
            route = AuthRoute.ForgotPassword.route,
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
            ForgotPasswordScreenRoot(
                onGoBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}