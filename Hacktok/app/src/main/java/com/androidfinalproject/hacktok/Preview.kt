package com.androidfinalproject.hacktok

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeCompilerApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.router.routes.FriendListRoute
import com.androidfinalproject.hacktok.ui.auth.LoginScreen
import com.androidfinalproject.hacktok.ui.auth.LoginState
import com.androidfinalproject.hacktok.ui.friendList.FriendListScreen
import com.androidfinalproject.hacktok.ui.friendList.FriendListState
import com.androidfinalproject.hacktok.ui.profile.UserProfileScreen
import com.androidfinalproject.hacktok.ui.theme.LoginAppTheme
import com.androidfinalproject.hacktok.ui.post.PostDetailScreen
import com.androidfinalproject.hacktok.ui.post.PostDetailState
import com.androidfinalproject.hacktok.ui.search.SearchDashboardScreen
import com.androidfinalproject.hacktok.ui.search.SearchUiState
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme
import org.bson.types.ObjectId
import java.util.Date
import com.androidfinalproject.hacktok.model.Post

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
private fun FriendListScreenPreview() {
    MainAppTheme {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(800.dp)
        ) {
            val mockUsers = MockData.mockUsers
            FriendListScreen(
              state = FriendListState(
                  filteredUsers = mockUsers,
                  friendIds = setOf(mockUsers[0].id, mockUsers[1].id),
                  searchQuery = "",
              ),
              onAction = {},
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
            val samplePosts = listOf(
                Post(id = ObjectId.get(), user = User(username = "John Doe", email="JohnTerry@example.com"), content = "This is my first post!"),
                Post(id = ObjectId.get(), user = User(username = "John Doe", email="JohnTerry@example.com"), content = "This is my second post!"),
            )

            UserProfileScreen(
                user = User(
                    id = ObjectId.get(),
                    username = "John Doe",
                    email = "johndoe@example.com",
                    createdAt = Date(),
                    isActive = true
                ),
                posts = samplePosts,
                isFriend = false,
                isBlocked = true,
                onSendFriendRequest = {},
                onUnfriend = {},
                onChat = {},
                onBlock = {},
            )
        }
    }
}