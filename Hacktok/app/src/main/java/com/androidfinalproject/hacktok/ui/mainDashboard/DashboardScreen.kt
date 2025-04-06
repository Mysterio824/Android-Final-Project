package com.androidfinalproject.hacktok.ui.mainDashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.androidfinalproject.hacktok.ui.mainDashboard.component.BottomNavigationBar
import com.androidfinalproject.hacktok.ui.mainDashboard.component.DashboardTopBar
import com.androidfinalproject.hacktok.ui.mainDashboard.component.WhatsNewBar
import com.androidfinalproject.hacktok.ui.post.component.PostContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp


@Composable
fun DashboardScreen(
    state: DashboardState,
    onAction: (DashboardAction) -> Unit
) {
    var currentScreen by remember { mutableStateOf("Search") }

    Scaffold(
        topBar = { DashboardTopBar { /* Xử lý WhatsNew click */ } },
        bottomBar = { BottomNavigationBar(currentScreen) { currentScreen = it } }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            WhatsNewBar(
                query = state.query,
                onQueryChange = { text -> onAction(DashboardAction.UpdateStatusText(text)) },
                upload = { onAction(DashboardAction.UploadPost) }
            )


            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.fillMaxSize())
            } else {
                LazyColumn {
                    items(state.posts) { post ->
                        PostContent(
                            post = post,
                            onLikeClick = {
                                onAction(DashboardAction.LikePost(post.id.toString()))
                            },
                            onCommentClick = {
                                onAction(DashboardAction.PostClick(post.id.toString()))
                            },
                            onShareClick = {
                                onAction(DashboardAction.SharePost(post.id.toString()))
                            },
                            onUserClick = {
                                onAction(DashboardAction.UserClick(post.userId.toString()))
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
    }

}
