package com.androidfinalproject.hacktok.ui.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.ui.search.component.*
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchDashboardScreen(
    state: SearchUiState,
    onAction: (SearchAction) -> Unit,
) {
    val tabs = listOf("Accounts", "Tags", "Places", "Posts")
    
    // Load search history when the screen is first displayed
    androidx.compose.runtime.LaunchedEffect(Unit) {
        onAction(SearchAction.LoadSearchHistory)
    }
    
    BackHandler {
        onAction(SearchAction.OnNavigateBack)
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar with Search
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onAction(SearchAction.OnNavigateBack) },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))

            SearchBar(
                query = state.searchQuery,
                onQueryChange = { onAction(SearchAction.UpdateQuery(it)) },
                onSearch = { /* Handle search if needed */ },
                active = false,
                onActiveChange = { /* No active state for now */ },
                placeholder = { Text("Search", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp)),
                colors = SearchBarDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    dividerColor = Color.Transparent
                )
            ) {}
        }

        // Display search history when query is empty
        if (state.searchQuery.isEmpty()) {
            SearchHistorySection(
                searchHistory = state.searchHistory,
                onHistoryItemClick = { query -> onAction(SearchAction.OnHistoryItemClick(query)) },
                onClearHistory = { onAction(SearchAction.ClearSearchHistory) }
            )
        } else {
            // Tab Row with custom indicator
            TabRow(
                selectedTabIndex = state.selectedTabIndex,
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.background
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = state.selectedTabIndex == index,
                        onClick = { onAction(SearchAction.ChangeTab(index)) },
                        text = { 
                            Text(
                                title, 
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (state.selectedTabIndex == index) 
                                        FontWeight.Bold else FontWeight.Normal
                                )
                            ) 
                        },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Content
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    state.isLoading -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    state.error != null -> {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = state.error,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            when (state.selectedTabIndex) {
                                0 -> items(state.filteredUsers) { user ->
                                    UserSearchItem(
                                        user = user,
                                        onClick = { onAction(SearchAction.OnUserClick(user)) }
                                    )
                                }
                                1, 2, 3 -> items(state.filteredPosts) { post ->
                                    PostSearchItem(
                                        post = post,
                                        onClick = { onAction(SearchAction.OnPostClick(post)) },
                                        user = state.users.find { it.id == post.userId }!!
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}



private fun Modifier.tabIndicatorOffset(
    currentTabPosition: TabPosition
): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(constraints)
    val currentTabWidth = currentTabPosition.width.roundToPx()
    val currentTabLeft = currentTabPosition.left.roundToPx()
    layout(currentTabWidth, placeable.height) {
        placeable.placeRelative(
            x = currentTabLeft,
            y = 0
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ScreenPreview() {
    MainAppTheme {
        Box{
            SearchDashboardScreen(
                state = SearchUiState(
                    selectedTabIndex = 2
                ),
                onAction = {}
            )
        }
    }
}