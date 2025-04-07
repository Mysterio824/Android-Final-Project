package com.androidfinalproject.hacktok.router.graph

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.androidfinalproject.hacktok.router.routes.MainRoute
import com.androidfinalproject.hacktok.ui.editProfile.EditProfileScreenRoot
import com.androidfinalproject.hacktok.ui.editProfile.EditProfileViewModel
import com.androidfinalproject.hacktok.ui.profile.UserProfileScreenRoot
import com.androidfinalproject.hacktok.ui.profile.UserProfileViewModel
import com.androidfinalproject.hacktok.ui.post.PostDetailScreenRoot
import com.androidfinalproject.hacktok.ui.post.PostDetailViewModel
import com.androidfinalproject.hacktok.ui.friendList.FriendListScreenRoot
import com.androidfinalproject.hacktok.ui.friendList.FriendListViewModel
import com.androidfinalproject.hacktok.ui.mainDashboard.DashboardScreenRoot
import com.androidfinalproject.hacktok.ui.mainDashboard.DashboardViewModel

fun NavGraphBuilder.mainNavigation(navController: NavController) {
    navigation(
        startDestination = "dashboard",
        route = MainRoute.Graph.route
    ) {
        // Dashboard Screen
        composable(
            route = "dashboard",
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
            }
        ) {
            DashboardScreenRoot(
                viewModel = DashboardViewModel(),
                onUserProfileNavigate = { userId ->
                    navController.navigate("${MainRoute.UserDetail.route}/$userId")
                },
                onPostDetailNavigate = { postId ->
                    navController.navigate("${MainRoute.PostDetail.route}/$postId")
                },
                onUserChatNavigate = { userId ->
                    navController.navigate("${MainRoute.ChatRoom.route}/user/$userId")
                },
                onGroupChatNavigate = { groupId ->
                    navController.navigate("${MainRoute.ChatRoom.route}/group/$groupId")
                },
                onFriendListNavigate = { userId ->
                    navController.navigate("${MainRoute.FriendList.route}/$userId")
                },
                onEditProfileNavigate = {
                    navController.navigate(MainRoute.EditProfile.route)
                },
                onPostEditNavigate = { postId ->
                    navController.navigate("${MainRoute.EditPost.route}/$postId")
                }
            )
        }

        // Post Detail Screen
        composable(
            route = "${MainRoute.PostDetail.route}/{postId}",
            arguments = listOf(
                navArgument("postId") { type = NavType.StringType; nullable = true }
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
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(300, easing = EaseInOut)
                        )
            }
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")

            PostDetailScreenRoot(
                viewModel = PostDetailViewModel(postId!!),
                onNavigateBack = {
                    navController.popBackStack()
                },
                onUserProfileNavigate = { userId ->
                    navController.navigate("${MainRoute.UserDetail.route}/$userId")
                }
            )
        }

        // User Profile Screen
        composable(
            route = "${MainRoute.UserDetail.route}/{userId}",
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType; nullable = true }
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
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(300, easing = EaseInOut)
                        )
            }
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")

            UserProfileScreenRoot(
                viewModel = UserProfileViewModel(userId!!),
                onChatWithFriend = { friendId ->
                    navController.navigate("${MainRoute.ChatRoom.route}/user/$friendId")
                },
                onGoToPost = { postId ->
                    navController.navigate("${MainRoute.PostDetail.route}/$postId")
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Friend List Screen
        composable(
            route = "${MainRoute.FriendList.route}/{userId}",
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
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(300, easing = EaseInOut)
                        )
            }
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")

            FriendListScreenRoot(
                viewModel = FriendListViewModel(userId!!),
                onNavigateBack = {
                    navController.popBackStack()
                },
                onChatWithFriend = { user ->
                    navController.navigate("${MainRoute.ChatRoom.route}/user/${user.id}")
                },
                onUserProfileView = { user ->
                    navController.navigate("${MainRoute.UserDetail.route}/${user.id}")
                }
            )
        }

        // Chat Room Screen (handles both user and group chats)
        composable(
            route = "${MainRoute.ChatRoom.route}/{chatType}/{chatId}",
            arguments = listOf(
                navArgument("chatType") { type = NavType.StringType },
                navArgument("chatId") { type = NavType.StringType; nullable = true }
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
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(300, easing = EaseInOut)
                        )
            }
        ) { backStackEntry ->
            val chatType = backStackEntry.arguments?.getString("chatType") ?: "user"
            val chatId = backStackEntry.arguments?.getString("chatId")

//            ChatScreenRoot(
//                viewModel = ChatViewModel(),
//                chatType = chatType,
//                chatId = chatId,
//                onNavigateBack = {
//                    navController.popBackStack()
//                },
//                onUserProfileView = { userId ->
//                    navController.navigate("${MainRoute.UserDetail.route}/$userId")
//                }
//            )
        }

        // Edit Profile Screen
        composable(
            route = MainRoute.EditProfile.route,
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
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(300, easing = EaseInOut)
                        )
            }
        ) {
            val userId = "" //chagne later
            EditProfileScreenRoot(
                viewModel = EditProfileViewModel(userId),
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Edit Post Screen
        composable(
            route = "${MainRoute.EditPost.route}/{postId}",
            arguments = listOf(
                navArgument("postId") { type = NavType.StringType }
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
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(300, easing = EaseInOut)
                        )
            }
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""

//            EditPostScreenRoot(
//                viewModel = EditPostViewModel(),
//                postId = postId,
//                onNavigateBack = {
//                    navController.popBackStack()
//                },
//                onPostSaved = {
//                    navController.navigate("dashboard") {
//                        popUpTo("dashboard") { inclusive = true }
//                    }
//                }
//            )
        }
    }
}