package com.androidfinalproject.hacktok.ui.mainDashboard.watchLater

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.ui.commonComponent.PostContent
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

@Composable
fun WatchLaterScreen(
    state: WatchLaterState,
    onAction: (WatchLaterAction) -> Unit
) {
    when {
        state.isLoading -> {
            CircularProgressIndicator()
        }
        state.error != null -> {
            Text(text = state.error)
        }
        state.savedPosts.isEmpty() -> {
            Text(text = "No saved posts")
        }
        else -> {
            LazyColumn{
                items(state.savedPosts) { post ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RectangleShape
                    ) {
                        PostContent(
                            post = post,

                            onPostClick = {
                                onAction(WatchLaterAction.OnPostClick(post.id!!))
                            },
                            onToggleLike = {
                                onAction(WatchLaterAction.OnLikeClick(post.id!!))
                            },
                            onUserClick = {
                                onAction(WatchLaterAction.OnUserClick(post.userId))
                            },
                            onComment = {
                                 onAction(WatchLaterAction.OnCommentClick(post.id!!))
                            },
                            onShare = {},
                        )
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
            WatchLaterScreen(
                state = WatchLaterState(
                    savedPosts = MockData.mockPosts
                ),
                onAction = {}
            )
        }
    }
}