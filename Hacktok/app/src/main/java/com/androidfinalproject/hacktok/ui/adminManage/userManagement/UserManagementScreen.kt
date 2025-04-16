package com.androidfinalproject.hacktok.ui.adminManage.userManagement

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.model.enums.UserRole

@Composable
fun UserManagementScreen(
    viewModel: UserManagementViewModel,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.userManagementState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        TextField(
            value = searchQuery,
            onValueChange = { viewModel.searchUsers(it) },
            label = { Text("Search Users") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (state) {
            is UserManagementUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally)
                )
            }
            is UserManagementUiState.Success -> {
                UserList(
                    users = (state as UserManagementUiState.Success).users,
                    onUserClick = onUserClick,
                    onDeleteUser = { viewModel.deleteUser(it) },
                    onUpdateRole = { userId, role -> viewModel.updateUserRole(userId, role) }
                )
            }
            is UserManagementUiState.Error -> {
                Text(
                    text = (state as UserManagementUiState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun UserList(
    users: List<User>,
    onUserClick: (String) -> Unit,
    onDeleteUser: (String) -> Unit,
    onUpdateRole: (String, UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(users) { user ->
            UserListItem(
                user = user,
                onClick = { user.id?.let { onUserClick(it) } },
                onDelete = { user.id?.let { onDeleteUser(it) } },
                onUpdateRole = { role -> user.id?.let { onUpdateRole(it, role) } }
            )
        }
    }
}

@Composable
private fun UserListItem(
    user: User,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onUpdateRole: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = user.username ?: "No username", style = MaterialTheme.typography.titleMedium)
            Text(text = user.email, style = MaterialTheme.typography.bodyMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onDelete) {
                    Text("Delete")
                }
                Button(onClick = { onUpdateRole(UserRole.ADMIN) }) {
                    Text("Make Admin")
                }
            }
        }
    }
} 