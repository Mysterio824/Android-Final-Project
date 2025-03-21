package com.androidfinalproject.hacktok.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.ui.friendList.FriendListAction
import com.androidfinalproject.hacktok.ui.search.component.PostItem
import com.androidfinalproject.hacktok.ui.search.component.UserItem


@Composable
fun SearchDashboardScreenRoot(
    viewModel: SearchViewModel,
    onUserClick: (User) -> Unit,
    onPostClick: (Post) -> Unit,
    onGoBack: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    SearchDashboardScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is SearchAction.OnPostClick -> onPostClick(action.post)
                is SearchAction.OnUserClick -> onUserClick(action.user)
                else -> viewModel.onAction(action)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchDashboardScreen(
    state: SearchUiState,
    onAction: (SearchAction) -> Unit,
) {
    val tabs = listOf("Accounts", "Tags", "Posts")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Thanh tìm kiếm
        SearchBar(
            query = state.searchQuery,
            onQueryChange = { onAction(SearchAction.UpdateQuery(it)) },
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
            selectedTabIndex = state.selectedTabIndex,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = state.selectedTabIndex == index,
                    onClick = { onAction(SearchAction.ChangeTab(index)) },
                    text = { Text(title, style = MaterialTheme.typography.bodySmall) }
                )
            }
        }

        if (state.isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        } else if (state.error != null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = state.error,
                    color = Color.Red
                )
            }
        } else {

            // Nội dung chính
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
            ) {
                when (state.selectedTabIndex) {
                    0 -> items(state.filteredUsers) { user ->
                        UserItem(
                            user = user,
                            onClick = {
                                onAction(SearchAction.OnUserClick(user))
                            },
                        )
                    }

                    1, 2, 3 -> items(state.filteredPosts) { post ->
                        PostItem(
                            post = post,
                            onClick = {
                                onAction(SearchAction.OnPostClick(post))
                            },
                        )
                    }
                }
            }
        }
    }
}