package com.androidfinalproject.hacktok.ui.adminManage.userManagement

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.androidfinalproject.hacktok.R
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

    Column(modifier = modifier
        .fillMaxSize()
        .padding(16.dp)) {
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
                val successState = state as UserManagementUiState.Success
                UserList(
                    users = successState.users,
                    reportCounts = successState.reportCounts,
                    onUserClick = onUserClick,
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
    reportCounts: Map<String, Int>,
    onUserClick: (String) -> Unit,
    onUpdateRole: (String, UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(users) { user ->
            val reportCount = reportCounts[user.id] ?: 0 // fallback to 0 if no reports

            UserListItem(
                user = user,
                reportCount = reportCount,
                onClick = { user.id?.let { onUserClick(it) } },
                onUpdateRole = { role -> user.id?.let { onUpdateRole(it, role) } }
            )
        }
    }
}

@Composable
fun UserListItem(
    user: User,
    reportCount: Int,
    onClick: () -> Unit,
    onUpdateRole: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = user.profileImage,
                    contentDescription = "Profile Image",
                    placeholder = painterResource(R.drawable.placeholder_image),
                    error = painterResource(R.drawable.placeholder_image),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = user.username ?: "No username",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = listOfNotNull(user.fullName, user.email).joinToString(" • "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!user.isCurrentlyBanned()) {
                    AccountStatusBadge(reportCount)
                } else {
                    Text(
                        text = "⚠️ This user is currently banned",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }


                Button(onClick = {
                    val newRole = if (user.role == UserRole.USER) UserRole.ADMIN else UserRole.USER
                    onUpdateRole(newRole)
                }) {
                    Text("Make ${if (user.role == UserRole.USER) "Admin" else "User"}")
                }
            }
        }
    }
}

@Composable
fun AccountStatusBadge(reportCount: Int) {
    val (label, color, icon) = when {
        reportCount == 0 -> Triple("Good", MaterialTheme.colorScheme.primary, "✅")
        reportCount in 1..2 -> Triple("Warning", MaterialTheme.colorScheme.tertiary, "⚠️")
        else -> Triple("Danger", MaterialTheme.colorScheme.error, "🚨")
    }

    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(50),
        tonalElevation = 0.dp,
        modifier = Modifier.padding(end = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = color)
        }
    }
}