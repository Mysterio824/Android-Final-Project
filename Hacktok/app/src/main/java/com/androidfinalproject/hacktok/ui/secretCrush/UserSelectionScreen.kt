package com.androidfinalproject.hacktok.ui.secretcrush

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSelectionScreen(
    users: List<User>,
    isLoading: Boolean,
    onUserSelected: (User) -> Unit,
    onBackPressed: () -> Unit
) {
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val filteredUsers = if (searchQuery.text.isEmpty()) {
        users
    } else {
        users.filter {
            it.username?.contains(searchQuery.text, ignoreCase = true) ?: false ||
                    it.fullName?.contains(searchQuery.text, ignoreCase = true) ?: false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Secret Crush") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search users") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                singleLine = true
            )

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredUsers) { user ->
                        UserListItem(user = user, onUserClick = { onUserSelected(user) })
                    }

                    if (filteredUsers.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No users found",
                                    style = MaterialTheme.typography.bodyLarge
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
fun UserListItem(
    user: User,
    onUserClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onUserClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User avatar
            AsyncImage(
                model = user.profileImage,
                contentDescription = "User avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // User info
            Column {
                Text(
                    text = user.fullName ?: "User",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "@${user.username ?: ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserSelectionScreenPreview() {
    MainAppTheme {
        UserSelectionScreen(
            users = MockData.mockUsers,
            isLoading = false,
            onUserSelected = {},
            onBackPressed = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserSelectionScreenLoadingPreview() {
    MainAppTheme {
        UserSelectionScreen(
            users = emptyList(),
            isLoading = true,
            onUserSelected = {},
            onBackPressed = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserSelectionScreenEmptyPreview() {
    MainAppTheme {
        UserSelectionScreen(
            users = emptyList(),
            isLoading = false,
            onUserSelected = {},
            onBackPressed = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserListItemPreview() {
    MainAppTheme {
        UserListItem(
            user = MockData.mockUsers[0],
            onUserClick = {}
        )
    }
}