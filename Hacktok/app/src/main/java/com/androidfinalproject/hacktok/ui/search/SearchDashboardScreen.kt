package com.androidfinalproject.hacktok.ui.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchDashboardScreen(
    state: SearchUiState,
    onAction: (SearchAction) -> Unit,
) {
    val tabs = listOf("Accounts", "Tags", "Places", "Posts")
    
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
                    imageVector = Icons.Default.ArrowBack,
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

        // Tab Row with custom indicator
        TabRow(
            selectedTabIndex = state.selectedTabIndex,
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.background,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[state.selectedTabIndex])
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    height = 3.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
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
                                    onClick = { onAction(SearchAction.OnPostClick(post)) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserSearchItem(
    user: User,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = user.profileImage,
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column {
            Text(
                text = user.username ?: "Unknown User",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = user.fullName ?: "",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun PostSearchItem(
    post: Post,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            AsyncImage(
                model = post.user?.profileImage,
                contentDescription = "User Profile",
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = post.user?.username ?: "Unknown User",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
        
        AsyncImage(
            model = post.imageLink,
            contentDescription = "Post Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        
        Text(
            text = post.content,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp)
        )
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