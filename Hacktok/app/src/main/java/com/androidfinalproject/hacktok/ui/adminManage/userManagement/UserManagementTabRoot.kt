package com.androidfinalproject.hacktok.ui.adminManage.userManagement

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.androidfinalproject.hacktok.model.enums.UserRole

@Composable
fun UserManagementTabRoot(
    viewModel: UserManagementViewModel
) {
    val uiState by viewModel.userManagementState.collectAsStateWithLifecycle()

    when (uiState) {
        is UserManagementUiState.Loading -> {
            // Show loading state
        }
        is UserManagementUiState.Success -> {
            val users = (uiState as UserManagementUiState.Success).users
            UserManagementTab(
                state = UserManagementState(
                    users = users,
                    filteredUsers = users,
                    availableRoles = listOf(UserRole.USER, UserRole.MODERATOR, UserRole.ADMIN, UserRole.SUPER_ADMIN)
                ),
                onAction = { action ->
                    when (action) {
                        is UserManagementAction.FilterUsers -> {
                            viewModel.searchUsers(action.query)
                        }
                        is UserManagementAction.DeleteUser -> {
                            viewModel.deleteUser(action.userId)
                        }
                        is UserManagementAction.UpdateUserRole -> {
                            viewModel.updateUserRole(action.userId, action.newRole)
                        }
                    }
                }
            )
        }
        is UserManagementUiState.Error -> {
            // Show error state
        }
    }
}