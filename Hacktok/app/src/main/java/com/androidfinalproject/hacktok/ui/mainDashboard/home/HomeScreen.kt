package com.androidfinalproject.hacktok.ui.mainDashboard.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.ui.mainDashboard.home.component.WhatsNewBar
import com.androidfinalproject.hacktok.ui.post.component.PostContent
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

@Composable
fun HomeScreen(
    state: HomeScreenState,
    onAction: (HomeScreenAction) -> Unit
) {
    Column {
        WhatsNewBar(
            query = state.query,
            onQueryChange = { text -> onAction(HomeScreenAction.UpdateStatusText(text)) },
            upload = { onAction(HomeScreenAction.UploadPost) }
        )

        when {
            state.isLoading -> {
                CircularProgressIndicator()
            }
            state.error != null -> {
                Text(text = state.error)
            }
            state.posts.isEmpty() -> {
                Text(text = "No posts available")
            }
            else -> {
                Spacer(modifier = Modifier.weight(1f))

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(), // Ensure LazyColumn fills the screen
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(state.posts) { post ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RectangleShape
                        ) {
                            PostContent(
                                post = post,
                                onPostClick = { onAction(HomeScreenAction.OnPostClick(post.id!!)) },
                                onToggleLike = { onAction(HomeScreenAction.LikePost(post.id!!)) },
                                onUserClick = { onAction(HomeScreenAction.OnUserClick(post.userId)) },
                                onComment = { onAction(HomeScreenAction.OnPostClick(post.id!!)) },
                                onShare = {},
                                onOptionsClick = {}
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WatchLaterScreenPreview() {
    MainAppTheme {
        Box(
            modifier = Modifier
                .width(400.dp)
                .height(800.dp)
        ) {
            HomeScreen(
                state = HomeScreenState(
                    posts = MockData.mockPosts
                ),
                onAction = {}
            )
        }
    }
}