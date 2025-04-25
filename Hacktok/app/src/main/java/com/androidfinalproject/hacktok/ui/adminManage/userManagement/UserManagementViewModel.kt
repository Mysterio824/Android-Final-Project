package com.androidfinalproject.hacktok.ui.adminManage.userManagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.model.enums.UserRole
import com.androidfinalproject.hacktok.repository.ReportRepository
import com.androidfinalproject.hacktok.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UserManagementUiState {
    object Loading : UserManagementUiState()
    data class Success(
        val users: List<User>,
        val reportCounts: Map<String, Int> = emptyMap()
    ) : UserManagementUiState()
    data class Error(val message: String) : UserManagementUiState()
}

@HiltViewModel
class UserManagementViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val reportRepository: ReportRepository,
) : ViewModel() {

    private val _userManagementState = MutableStateFlow<UserManagementUiState>(UserManagementUiState.Loading)
    val userManagementState: StateFlow<UserManagementUiState> = _userManagementState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var allUsers = listOf<User>()
    private var reportCounts = mutableMapOf<String, Int>()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _userManagementState.value = UserManagementUiState.Loading
            try {
                // Implement loading all users from repository
                // For now, using a placeholder
                allUsers = userRepository.getAllUsers()

                reportCounts.clear() // Reset before updating

                allUsers.forEach { user ->
                    val userId = user.id
                    if (userId != null) {
                        val reports = reportRepository.getReportsForUser(userId)
                        reportCounts[userId] = reports.size
                    }
                }

                _userManagementState.value = UserManagementUiState.Success(
                    users = allUsers,
                    reportCounts = reportCounts.toMap()
                )
            } catch (e: Exception) {
                _userManagementState.value = UserManagementUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun searchUsers(query: String) {
        _searchQuery.value = query
        val filteredUsers = allUsers.filter { user ->
            user.username?.contains(query, ignoreCase = true) == true ||
            user.email.contains(query, ignoreCase = true) ||
            user.fullName?.contains(query, ignoreCase = true) == true
        }
        _userManagementState.value = UserManagementUiState.Success(filteredUsers)
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            try {
                userRepository.deleteUser(userId)
                // Refresh the user list after deletion
                loadUsers()
            } catch (e: Exception) {
                _userManagementState.value = UserManagementUiState.Error(e.message ?: "Failed to delete user")
            }
        }
    }

    fun updateUserRole(userId: String, newRole: UserRole) {
        viewModelScope.launch {
            try {
                userRepository.updateUser(userId, mapOf("role" to newRole.name))
                // Refresh the user list after role update
                loadUsers()
            } catch (e: Exception) {
                _userManagementState.value = UserManagementUiState.Error(e.message ?: "Failed to update user role")
            }
        }
    }
}