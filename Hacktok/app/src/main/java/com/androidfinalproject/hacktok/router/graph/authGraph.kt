package com.androidfinalproject.hacktok.router.graph

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.androidfinalproject.hacktok.router.routes.AuthRoute
import com.androidfinalproject.hacktok.router.routes.MainRoute
import com.androidfinalproject.hacktok.ui.auth.LoginScreenRoot
import com.androidfinalproject.hacktok.ui.auth.LoginViewModel
import com.androidfinalproject.hacktok.ui.forgotPassword.ForgotPasswordScreenRoot
import com.androidfinalproject.hacktok.ui.forgotPassword.ForgotPasswordViewModel
import com.androidfinalproject.hacktok.ui.resetPassword.ResetPasswordScreenRoot
import com.androidfinalproject.hacktok.ui.resetPassword.ResetPasswordViewModel

fun NavGraphBuilder.authNavigation(navController: NavController) {
    navigation(
        startDestination = AuthRoute.Login.route,
        route = AuthRoute.AuthGraph.route
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
            LoginScreenRoot(
                viewModel = LoginViewModel(),
                onLoginSuccess = {
                    navController.navigate(MainRoute.MainGraph.route) {
                        // Clear the back stack when logging in
                        popUpTo(AuthRoute.AuthGraph.route) { inclusive = true }
                    }
                },
                onForgetPassword = {
                    navController.navigate(AuthRoute.ForgotPassword.route)
                }
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
                viewModel = ForgotPasswordViewModel(),
                onResetSuccess = { email, verificationCode ->
                    navController.navigate("${AuthRoute.PasswordRecovery.route}/$email/$verificationCode")
                },
                onGoBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "${AuthRoute.PasswordRecovery.route}/{email}/{verificationCode}",
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("verificationCode") { type = NavType.StringType }
            ),
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
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val verificationCode = backStackEntry.arguments?.getString("verificationCode") ?: ""

            ResetPasswordScreenRoot(
                viewModel = ResetPasswordViewModel(),
                email = email,
                verificationCode = verificationCode,
                onResetSuccess = {
                    navController.navigate(AuthRoute.Login.route) {
                        // Clear the back stack after password reset
                        popUpTo(AuthRoute.AuthGraph.route) { inclusive = false }
                    }
                },
                onGoBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}