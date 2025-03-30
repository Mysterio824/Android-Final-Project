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
import com.androidfinalproject.hacktok.ui.auth.LoginScreen
import com.androidfinalproject.hacktok.ui.auth.LoginState
import com.androidfinalproject.hacktok.ui.profile.UserProfileScreen
import com.androidfinalproject.hacktok.ui.post.PostDetailScreen
import com.androidfinalproject.hacktok.ui.post.PostDetailState
import com.androidfinalproject.hacktok.ui.search.SearchDashboardScreen
import com.androidfinalproject.hacktok.ui.search.SearchUiState
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme
import org.bson.types.ObjectId
import com.androidfinalproject.hacktok.ui.mainDashboard.DashboardScreen
import com.androidfinalproject.hacktok.ui.currentProfile.CurrentProfileScreen

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
                selectedTabIndex = 2, // change this to change tab
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
    DashboardScreen()
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
                postId = ObjectId(),
                state = PostDetailState(
                    post = MockData.mockPosts.first(),
                    comments = MockData.mockComments,
                    isCommentsVisible = true
                ),
                onAction = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserProfilePreview() {
    MainAppTheme {  // Add theme wrapper
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(800.dp)
        ) {
            val samplePosts = MockData.mockPosts

            UserProfileScreen(
                user = MockData.mockUsers.first(),
                posts = samplePosts,
                isFriend = false,
                isBlocked = false,
                onSendFriendRequest = {},
                onUnfriend = {},
                onChat = {},
                onBlock = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CurrentProfilePreview() {
    MainAppTheme {  // Add theme wrapper
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

//@Preview(showBackground = true)
//@Composable
//fun AdminManagementScreenPreview() {
//    MainAppTheme {
//        Box(
//            modifier = Modifier
//                .width(400.dp)
//                .height(800.dp)
//        ) {
//            AdminManagementScreen(
//                viewModel = AdminManagementViewModel()
//            )
//        }
//    }
//}