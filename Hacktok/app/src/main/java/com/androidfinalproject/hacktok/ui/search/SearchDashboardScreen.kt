package com.androidfinalproject.hacktok.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.ui.search.component.PostItem
import com.androidfinalproject.hacktok.ui.search.component.UserItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchDashboardScreen(
    viewModel: SearchViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState = viewModel.uiState
    val tabs = listOf("Accounts", "Tags", "Posts")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Thanh tìm kiếm
        SearchBar(
            query = uiState.searchQuery,
            onQueryChange = { viewModel.onAction(SearchAction.UpdateQuery(it)) },
            onSearch = { /* Handle search if needed */ },
            active = false,
            onActiveChange = { /* No active state for now */ },
            placeholder = { Text("Search") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
        ) {}

        // Tab Row
        TabRow(
            selectedTabIndex = uiState.selectedTabIndex,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = uiState.selectedTabIndex == index,
                    onClick = { viewModel.onAction(SearchAction.ChangeTab(index)) },
                    text = { Text(title, style = MaterialTheme.typography.bodySmall) }
                )
            }
        }

        // Nội dung chính
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) {
            when (uiState.selectedTabIndex) {
                0 -> items(uiState.filteredUsers) { user ->
                    UserItem(user = user)
                }
                1, 2, 3 -> items(uiState.filteredPosts) { post ->
                    PostItem(post = post)
                }
            }
        }
    }
}