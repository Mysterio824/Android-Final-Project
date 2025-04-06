package com.androidfinalproject.hacktok.ui.mainDashboard.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.ui.mainDashboard.home.component.WhatsNewBar
import com.androidfinalproject.hacktok.ui.post.component.PostContent

@Composable
fun HomeScreen(
    state: HomeScreenState,
    onAction: (HomeScreenAction) -> Unit
) {
    WhatsNewBar(
        query = state.query,
        onQueryChange = { text -> onAction(HomeScreenAction.UpdateStatusText(text)) },
        upload = { onAction(HomeScreenAction.UploadPost) }
    )

    if (state.isLoading) {
        CircularProgressIndicator(modifier = Modifier.fillMaxSize())
    } else {
        LazyColumn {
            items(state.posts) { post ->
                PostContent(
                    post = post,
                    onLikeClick = {
                        onAction(HomeScreenAction.LikePost(post.id.toString()))
                    },
                    onCommentClick = {
                        onAction(HomeScreenAction.OnPostClick(post.id.toString()))
                    },
                    onShareClick = {
                        onAction(HomeScreenAction.SharePost(post.id.toString()))
                    },
                    onUserClick = {
                        onAction(HomeScreenAction.OnUserClick(post.userId))
                    }
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}