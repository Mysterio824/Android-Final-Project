package com.androidfinalproject.hacktok.ui.adminManage.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.model.UserRole

@Composable
fun UserManagementTab(
    users: List<User>,
    onUpdateRole: (String, UserRole) -> Unit,
    onDelete: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        items(users) { user ->
            UserItem(
                user = user,
                onUpdateRole = { newRole -> onUpdateRole(user.id ?: "", newRole) },
                onDelete = { onDelete(user.id ?: "") }
            )
        }
    }
}

@Composable
fun UserItem(
    user: User,
    onUpdateRole: (UserRole) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Username: ${user.username}", fontWeight = FontWeight.Bold)
                Text(text = "Email: ${user.email}")
                Text(text = "Role: ${user.role}")
            }

            var expanded by remember { mutableStateOf(false) }
            Box {
                Button(onClick = { expanded = true }) {
                    Text("Update Role")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    UserRole.values().forEach { role ->
                        DropdownMenuItem(
                            onClick = {
                                onUpdateRole(role)
                                expanded = false
                            }
                        ) {
                            Text(role.toString())
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete User", tint = Color.Red)
            }
        }
    }
}