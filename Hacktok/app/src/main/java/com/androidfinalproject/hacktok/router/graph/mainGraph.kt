package com.androidfinalproject.hacktok.router.graph

import SearchDashboardScreenRoot
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.androidfinalproject.hacktok.router.animation.slideFadeInFromLeft
import com.androidfinalproject.hacktok.router.animation.slideFadeInFromRight
import com.androidfinalproject.hacktok.router.animation.slideFadeOutToLeft
import com.androidfinalproject.hacktok.router.animation.slideFadeOutToRight
import com.androidfinalproject.hacktok.router.routes.MainRoute
import com.androidfinalproject.hacktok.ui.chat.ChatScreenRoot
import com.androidfinalproject.hacktok.ui.currentProfile.CurrentProfileScreenRoot
import com.androidfinalproject.hacktok.ui.editProfile.EditProfileScreenRoot
import com.androidfinalproject.hacktok.ui.profile.UserProfileScreenRoot
import com.androidfinalproject.hacktok.ui.post.PostDetailScreenRoot
import com.androidfinalproject.hacktok.ui.friendList.FriendListScreenRoot
import com.androidfinalproject.hacktok.ui.mainDashboard.DashboardScreenRoot
import com.androidfinalproject.hacktok.ui.messageDashboard.MessageDashboardRoot
import com.androidfinalproject.hacktok.ui.newStory.NewStoryRoot
import com.androidfinalproject.hacktok.ui.messageDashboard.MessageDashboardViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.androidfinalproject.hacktok.router.routes.AuthRoute
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.androidfinalproject.hacktok.ui.changePassword.ChangePasswordScreenRoot
import com.androidfinalproject.hacktok.ui.chatDetail.ChatDetailScreenRoot
import com.androidfinalproject.hacktok.ui.newPost.NewPostScreenRoot
import com.androidfinalproject.hacktok.ui.storydetail.StoryDetailScreenRoot
import com.androidfinalproject.hacktok.ui.secretCrush.SecretCrushScreenRoot
import com.androidfinalproject.hacktok.ui.secretCrush.SecretCrushViewModel

fun NavGraphBuilder.mainNavigation(navController: NavController) {
    navigation(
        startDestination = MainRoute.SecretCrush.route,
        route = MainRoute.Graph.route
    ) {
        // Dashboard Screen
        composable(
            route = MainRoute.Dashboard.route,
            enterTransition = { slideFadeInFromLeft() },
            exitTransition = { slideFadeOutToRight() }
        ) {
            DashboardScreenRoot(
                onUserProfileNavigate = { userId ->
                    navController.navigate("${MainRoute.UserDetail.route}/$userId")
                },
                onPostDetailNavigate = { postId, commentId ->
                    val route = if (commentId != null) {
                        "${MainRoute.PostDetail.route}/$postId?commentId=$commentId"
                    } else {
                        "${MainRoute.PostDetail.route}/$postId"
                    }
                    navController.navigate(route)
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
                onSearchNavigate = {
                    navController.navigate(MainRoute.Search.route)
                },
                onCurrentProfileNavigate = {
                    navController.navigate(MainRoute.Profile.route)
                },
                onMessageDashBoardNavigate = {
                    navController.navigate(MainRoute.ChatDashboard.route)
                },
                onPostEditNavigate = { postId ->
                    navController.navigate("${MainRoute.EditPost.route}/$postId")
                },
                onCreatePostNavigate = {
                    navController.navigate(MainRoute.NewPost.BASE_ROUTE)
                },
                onStoryNavigate = { storyId ->
                    navController.navigate("${MainRoute.StoryDetail.route}/$storyId")
                },
                onCreateStoryNavigate = {
                    navController.navigate(MainRoute.NewStory.route)

                },
                onAuthNavigate = {
                    navController.navigate(AuthRoute.Graph.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onChangePassNavigate = {
                    navController.navigate(MainRoute.ChangePass.route)
                },
                onUserEditNavigate = {
                    navController.navigate(MainRoute.EditProfile.route)
                },
            )
        }

        composable(
            route = "${MainRoute.NewPost.BASE_ROUTE}?postId={postId}",
            arguments = listOf(
                navArgument("postId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            enterTransition = { slideFadeInFromRight() },
            exitTransition = { slideFadeOutToLeft() }
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")

            NewPostScreenRoot(
                postId = postId,
                onClose = {
                    navController.navigate("dashboard") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                },
                onPost = {
                    navController.navigate("dashboard") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                },
            )
        }

        composable(
            route = MainRoute.Search.route,
            enterTransition = { slideFadeInFromRight() },
            exitTransition = { slideFadeOutToLeft() }
        ){ backStackEntry ->
            SearchDashboardScreenRoot(
                onUserClick = { navController.navigate("${MainRoute.UserDetail.route}/$it") },
                onPostClick = { navController.navigate("${MainRoute.PostDetail.route}/$it") },
                onNavigateBack = {
                    navController.popBackStack()
                },
            )
        }


        composable(
            route = MainRoute.Profile.route,
            enterTransition = { slideFadeInFromRight() },
            exitTransition = { slideFadeOutToLeft() }
        ){
            CurrentProfileScreenRoot (
                onPostClickNavigation = {
                    navController.navigate("${MainRoute.PostDetail.route}/$it") },
                onPostEditNavigation = { postId ->
                    navController.navigate("${MainRoute.NewPost.BASE_ROUTE}?postId=$postId")
                },
                onNewPostNavigation = { navController.navigate(MainRoute.NewPost.BASE_ROUTE) },
                onFriendListNavigation = { navController.navigate("${MainRoute.FriendList.route}/$it") },
                onProfileEditNavigation = { navController.navigate(MainRoute.EditProfile.route) },
                onNavigateBack = {
                    navController.popBackStack()
                },
            )
        }

        composable(
            route = MainRoute.ChatDashboard.route,
            enterTransition = { slideFadeInFromRight() },
            exitTransition = { slideFadeOutToLeft() }
        ){
            MessageDashboardRoot (
                viewModel = hiltViewModel(),
                onNewGroup = {},
                onNewChat = {},
                onGoToChat = { userId ->
                    navController.navigate("${MainRoute.ChatRoom.route}/user/$userId")
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
            )
        }

        // Post Detail Screen
        composable(
            route = "${MainRoute.PostDetail.route}/{postId}?commentId={commentId}",
            arguments = listOf(
                navArgument("postId") { type = NavType.StringType; nullable = true },
                navArgument("commentId") { type = NavType.StringType; nullable = true; defaultValue = null }
            ),
            enterTransition = { slideFadeInFromLeft() },
            exitTransition = { slideFadeOutToRight() }
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId")
            val commentId = backStackEntry.arguments?.getString("commentId")

            PostDetailScreenRoot(
                postId = postId!!,
                commentId = commentId,
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
            enterTransition = { slideFadeInFromLeft() },
            exitTransition = { slideFadeOutToRight() }
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            if (userId == null) {
                navController.popBackStack()
                return@composable
            }

            UserProfileScreenRoot(
                userId = userId,
                onChatWithFriend = { chatId ->
                    navController.navigate("${MainRoute.ChatRoom.route}/user/$chatId")
                },
                onGoToPost = { postId ->
                    navController.navigate("${MainRoute.PostDetail.route}/$postId")
                },
                onGoToFriendList = { chatId ->
                    navController.navigate("${MainRoute.FriendList.route}/$chatId")
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Friend List Screen
        composable(
            route = "${MainRoute.FriendList.route}/{userId}",
            enterTransition = { slideFadeInFromLeft() },
            exitTransition = { slideFadeOutToRight() }
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")

            FriendListScreenRoot(
                userId = userId!!,
                onChatWithFriend = { user ->
                    navController.navigate("${MainRoute.ChatRoom.route}/user/${user}")
                },
                onUserProfileView = { user ->
                    navController.navigate("${MainRoute.UserDetail.route}/${user}")
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
            enterTransition = { slideFadeInFromRight() },
            exitTransition = { slideFadeOutToLeft() }
        ) { backStackEntry ->
            val chatType = backStackEntry.arguments?.getString("chatType") ?: "user"
            val chatId = backStackEntry.arguments?.getString("chatId")

            if (chatId == null) {
                navController.popBackStack()
                return@composable
            }

            when (chatType) {
                "user" -> ChatScreenRoot(
                    userId = chatId,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToManageUser = { userId ->
                        navController.navigate("${MainRoute.UserDetail.route}/$userId")
                    },
                    onChatOptionNavigate = {
                        navController.navigate("${MainRoute.ChatOption.route}/$it")
                    }
                )
                "group" -> {
                    // TODO: Implement group chat screen
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = "Group chat feature coming soon",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }

        composable(
            route = "${MainRoute.ChatOption.route}/{chatId}",
            enterTransition = { slideFadeInFromLeft() },
            exitTransition = { slideFadeOutToRight() }
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId")

            ChatDetailScreenRoot(
                userId = chatId!!,
                isGroup = false,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onUserProfileNavigate = {
                    navController.navigate("${MainRoute.Profile.route}/$it")
                }
            )
        }

        // Edit Profile Screen
        composable(
            route = MainRoute.EditProfile.route,
            enterTransition = { slideFadeInFromRight() },
            exitTransition = { slideFadeOutToLeft() }
        ) {
            EditProfileScreenRoot(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Change password
        composable(
            route = MainRoute.ChangePass.route,
            enterTransition = { slideFadeInFromRight() },
            exitTransition = { slideFadeOutToLeft() }
        ) {
            ChangePasswordScreenRoot(
                onResetSuccess = {
                    navController.navigate(MainRoute.Dashboard.route)
                },
                onGoBack = {
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
            enterTransition = { slideFadeInFromRight() },
            exitTransition = { slideFadeOutToLeft() }
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

        // New Story Screen
        composable(
            route = MainRoute.NewStory.route,
            enterTransition = { slideFadeInFromRight() },
            exitTransition = { slideFadeOutToLeft() }
        ) {
            NewStoryRoot(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Secret Crush Screen
        composable(
            route = MainRoute.SecretCrush.route,
            enterTransition = { slideFadeInFromRight() },
            exitTransition = { slideFadeOutToLeft() }
        ) {
            SecretCrushScreenRoot(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "${MainRoute.StoryDetail.route}/{storyId}",
            arguments = listOf(navArgument("storyId") { type = NavType.StringType }),
            enterTransition = { slideFadeInFromRight() },
            exitTransition = { slideFadeOutToLeft() }
        ) { backStackEntry ->
            val storyId = backStackEntry.arguments?.getString("storyId")

            StoryDetailScreenRoot(
                onClose = { navController.popBackStack() }
            )
        }

    }
}