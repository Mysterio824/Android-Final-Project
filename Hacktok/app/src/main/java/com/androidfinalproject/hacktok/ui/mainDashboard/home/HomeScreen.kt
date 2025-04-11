package com.androidfinalproject.hacktok.ui.mainDashboard.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.ui.mainDashboard.home.component.*
import com.androidfinalproject.hacktok.ui.post.component.PostContent
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

@Composable
fun HomeScreen(
    state: HomeScreenState,
    onAction: (HomeScreenAction) -> Unit
) {
    val facebookBlue = Color(0xFF1877F2)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F2F5))
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = facebookBlue
                )
            }
            state.error != null -> {
                Text(
                    text = state.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            state.posts.isEmpty() -> {
                Text(
                    text = "No posts available",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item {
                        WhatsNewBar(
                            profilePicUrl = state.user?.profileImage ?: "",
                            onNewPostCLick = { onAction(HomeScreenAction.OnCreatePost) }
                        )
                    }

                    item {
                        StoriesSection(onCreateStory = { onAction(HomeScreenAction.OnCreateStory) })
                    }

                    items(state.posts) { post ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RectangleShape,
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            PostContent(
                                post = post,
                                onPostClick = { onAction(HomeScreenAction.OnPostClick(post.id!!)) },
                                onToggleLike = { onAction(HomeScreenAction.LikePost(post.id!!)) },
                                onUserClick = { onAction(HomeScreenAction.OnUserClick(post.userId)) },
                                onComment = { onAction(HomeScreenAction.OnPostClick(post.id!!)) },
                                onShare = { onAction(HomeScreenAction.SharePost(post.id!!)) },
                                onOptionsClick = {}
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
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