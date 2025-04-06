package com.androidfinalproject.hacktok.ui.adminManage.userManagement

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.ui.adminManage.userManagement.component.UserItem

@Composable
fun UserManagementTab(
    state: UserManagementState,
    onAction: (UserManagementAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                onAction(UserManagementAction.FilterUsers(it))
            },
            label = { Text("Search Users") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp)) // Consistent spacing

        if (state.filteredUsers.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No users found",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn {
                items(state.filteredUsers) { user ->
                    UserItem(
                        user = user,
                        onUpdateRole = {
                            userId, role
                                -> onAction(UserManagementAction.UpdateUserRole(userId, role))
                        },
                        onDelete = {userId -> onAction(UserManagementAction.DeleteUser(userId)) }
                    )
                }
            }
        }
    }
}