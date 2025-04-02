package com.androidfinalproject.hacktok.ui.adminManage.userManagement

import androidx.lifecycle.ViewModel
import com.androidfinalproject.hacktok.model.MockData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UserManagementViewModel : ViewModel() {
    private val _state = MutableStateFlow(UserManagementState())
    val state: StateFlow<UserManagementState> = _state.asStateFlow()

    init {
        _state.update {
            it.copy(
                users = MockData.mockUsers,
                filteredUsers = MockData.mockUsers,
                availableRoles = MockData.mockUserRoles
            )
        }
    }

    fun onAction(action: UserManagementAction) {
        when (action) {
            is UserManagementAction.UpdateUserRole -> {
                _state.update { currentState ->
                    currentState.copy(
                        users = currentState.users.map { user ->
                            if (user.id == action.userId) user.copy(role = action.newRole) else user
                        },
                        filteredUsers = currentState.filteredUsers.map { user ->
                            if (user.id == action.userId) user.copy(role = action.newRole) else user
                        }
                    )
                }
            }
            is UserManagementAction.DeleteUser -> {
                _state.update { currentState ->
                    currentState.copy(
                        users = currentState.users.filter { it.id != action.userId },
                        filteredUsers = currentState.filteredUsers.filter { it.id != action.userId }
                    )
                }
            }
            is UserManagementAction.FilterUsers -> {
                _state.update { currentState ->
                    currentState.copy(
                        filteredUsers = if (action.query.isBlank()) {
                            currentState.users
                        } else {
                            currentState.users.filter { user ->
                                user.username.contains(action.query, ignoreCase = true) ||
                                        user.email.contains(action.query, ignoreCase = true) ||
                                        user.fullName?.contains(action.query, ignoreCase = true) == true
                            }
                        }
                    )
                }
            }
        }
    }
}