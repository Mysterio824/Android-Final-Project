package com.androidfinalproject.hacktok

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.ui.adminManage.AdminManagementScreen
import com.androidfinalproject.hacktok.ui.adminManage.AdminManagementState
import com.androidfinalproject.hacktok.ui.auth.LoginScreen
import com.androidfinalproject.hacktok.ui.auth.LoginState
import com.androidfinalproject.hacktok.ui.commentStatistic.CommentStatistics
import com.androidfinalproject.hacktok.ui.commentStatistic.CommentStatisticsScreen
import com.androidfinalproject.hacktok.ui.post.PostDetailScreen
import com.androidfinalproject.hacktok.ui.post.PostDetailState
import com.androidfinalproject.hacktok.ui.search.SearchDashboardScreen
import com.androidfinalproject.hacktok.ui.search.SearchUiState
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme
import com.androidfinalproject.hacktok.ui.mainDashboard.DashboardScreen
import com.androidfinalproject.hacktok.ui.currentProfile.CurrentProfileScreen
import com.androidfinalproject.hacktok.ui.editProfile.EditProfileScreen
import com.androidfinalproject.hacktok.ui.editProfile.EditProfileState
import com.androidfinalproject.hacktok.ui.mainDashboard.DashboardState
import com.androidfinalproject.hacktok.ui.messageDashboard.MessageDashboardScreen
import com.androidfinalproject.hacktok.ui.messageDashboard.MessageDashboardState
import com.androidfinalproject.hacktok.ui.userStatistic.UserStatisticsScreen
import com.androidfinalproject.hacktok.ui.userStatistic.UserStatisticsState

@Preview(showBackground = true)
@Composable
fun SearchDashboardScreenPreview() {
    MainAppTheme {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(800.dp)
        ) {
            val previewState = SearchUiState(
                selectedTabIndex = 2,
                users = MockData.mockUsers,
                filteredUsers = MockData.mockUsers,
                posts = MockData.mockPosts,
                filteredPosts = MockData.mockPosts
            )

            SearchDashboardScreen(
                state = previewState,
                onAction = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    MainAppTheme {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(800.dp)
        ) {
            LoginScreen (
                state = LoginState(
                    isLoginMode = false
                ),
                onAction = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDashboardScreen() {
    MainAppTheme {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(800.dp)
        ) {
            DashboardScreen(
                state = DashboardState(
                    posts = MockData.mockPosts
                ),
                onAction = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PostDetailScreenPreview() {
    MainAppTheme {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(800.dp)
        ) {
            PostDetailScreen(
                postId = "",
                state = PostDetailState(
                    post = MockData.mockPosts.first(),
                    comments = MockData.mockComments
                ),
                onAction = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditUserPreview() {
    MainAppTheme {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(800.dp)
        ) {
            val user = MockData.mockUsers.first()

            EditProfileScreen(
                state = EditProfileState(
                    username = user.username,
                    fullName = user.fullName ?: "Unknown",
                    email = user.email,
                    bio = user.bio ?: "",
                    role = user.role,
                    errorState = emptyMap()
                ),
                onAction = {}
            )
        }
    }
}

@Preview
@Composable
fun UserStatisticScreenReview() {
    MainAppTheme {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(800.dp)
        ) {
            UserStatisticsScreen(
                state = MockData.sampleUserStatisticsState,
                onAction = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CurrentProfilePreview() {
    MainAppTheme {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(800.dp)
        ) {
            val samplePosts = MockData.mockPosts

            CurrentProfileScreen (
                user = MockData.mockUsers.first(),
                posts = samplePosts,
                friendCount = 16,
                navController = NavController(context = LocalContext.current),
                onProfileEdit = {},
                onPostEdit = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminManagementScreenPreview() {
    MainAppTheme {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(800.dp)
        ) {
            AdminManagementScreen(
                state = AdminManagementState(
                    selectedTab = 3
                ),
                onAction = {},
                modifier = Modifier
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatHomeScreenPreview() {
    MainAppTheme {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(800.dp)
        ) {
            MessageDashboardScreen(
                state = MessageDashboardState(
                    userList = MockData.mockUsers,

                ),
                onAction = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CommentStatisticScreenPreview() {
    MainAppTheme {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(800.dp)
        ) {
            CommentStatisticsScreen(
                statistics = CommentStatistics(
                    dailyComments = 100,
                    monthlyComments = 2000,
                    yearlyComments = 100000,
                    bannedComments = 0
                )
            )
        }
    }
}