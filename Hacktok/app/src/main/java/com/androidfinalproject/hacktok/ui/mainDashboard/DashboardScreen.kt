package com.androidfinalproject.hacktok.ui.mainDashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.androidfinalproject.hacktok.ui.mainDashboard.component.BottomNavigationBar
import com.androidfinalproject.hacktok.ui.mainDashboard.component.DashboardTopBar
import com.androidfinalproject.hacktok.ui.mainDashboard.component.WhatsNewBar
import com.androidfinalproject.hacktok.ui.post.component.PostContent
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = viewModel()) {
    val state = viewModel.state.collectAsState().value
    var currentScreen by remember { mutableStateOf("Search") }

    MainAppTheme {
        Scaffold(
            topBar = { DashboardTopBar { /* Xử lý WhatsNew click */ } },
            bottomBar = { BottomNavigationBar(currentScreen) { currentScreen = it } }
        ) { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                WhatsNewBar(
                    onSearch = { query -> /* Xử lý tìm kiếm */ },
                    onPickImage = { /* Xử lý chọn ảnh từ thư viện */ },
                    onTakePhoto = { /* Xử lý chụp ảnh từ camera */ },
                    onVoiceInput = { /* Xử lý nhập giọng nói */ },
                    onLocation = { /* Xử lý chia sẻ vị trí */ }
                )

                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.fillMaxSize())
                } else {
                    LazyColumn {
                        items(state.posts) { post ->
                            PostContent(
                                post = post,
                                onLikeClick = { /* Xử lý like */ },
                                onCommentClick = { /* Xử lý comment */ },
                                onShareClick = { /* Xử lý share */ },
                                onUserClick = { /* Xử lý click vào user */ }
                            )
                        }
                    }
                }
            }
        }
    }
}
