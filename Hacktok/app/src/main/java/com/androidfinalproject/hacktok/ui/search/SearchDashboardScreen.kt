package com.androidfinalproject.hacktok.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.ui.search.component.PostItem
import com.androidfinalproject.hacktok.ui.search.component.UserItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchDashboardScreen(
    users: List<User> = emptyList(),
    posts: List<Post> = emptyList()
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Accounts", "Tags", "Places", "Posts")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Thanh tìm kiếm giống Instagram
        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
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

        // Tab Row giống Instagram với chữ nhỏ hơn
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp) // Giảm padding để tiết kiệm không gian
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title, style = MaterialTheme.typography.bodySmall) } // Sử dụng bodySmall
                )
            }
        }

        // Nội dung chính
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) {
            when (selectedTabIndex) {
                0 -> items(users.filter { it.username.contains(searchQuery, ignoreCase = true) }) { user ->
                    UserItem(user = user)
                }
                1 -> items(posts.filter { it.content.contains(searchQuery, ignoreCase = true) && it.content.contains("#") }) { post ->
                    PostItem(post = post)
                }
                2 -> items(posts.filter { it.content.contains(searchQuery, ignoreCase = true) && it.content.contains("place") }) { post ->
                    PostItem(post = post)
                }
                3 -> items(posts.filter { it.content.contains(searchQuery, ignoreCase = true) }) { post ->
                    PostItem(post = post)
                }
            }
        }
    }
}