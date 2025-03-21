package com.androidfinalproject.hacktok.ui.mainDashboard;

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.androidfinalproject.hacktok.ui.mainDashboard.*

@Composable
fun DashboardScreen(viewModel: DashboardViewModel = viewModel()) {
    val state = viewModel.state.collectAsState().value

    Scaffold(
        topBar = { DashboardTopBar { /* Xử lý search click */ } }
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(it)) {
            SearchBar { query -> /* Xử lý tìm kiếm */ }
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.fillMaxSize())
            } else {
                LazyColumn {
                    items(state.posts) { post ->
                        PostItem(post)
                    }
                }
            }
        }
    }
}