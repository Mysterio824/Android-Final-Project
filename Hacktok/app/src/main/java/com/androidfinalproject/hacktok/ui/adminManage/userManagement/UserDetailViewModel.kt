package com.androidfinalproject.hacktok.ui.adminManage.userManagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.model.UserRole
import com.androidfinalproject.hacktok.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UserDetailState {
    object Loading : UserDetailState()
    data class Success(val user: User) : UserDetailState()
    data class Error(val message: String) : UserDetailState()
}

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _userState = MutableStateFlow<UserDetailState>(UserDetailState.Loading)
    val userState: StateFlow<UserDetailState> = _userState.asStateFlow()
    
    fun loadUserDetails(userId: String) {
        viewModelScope.launch {
            _userState.value = UserDetailState.Loading
            try {
                val user = userRepository.getUserById(userId)
                if (user != null) {
                    _userState.value = UserDetailState.Success(user)
                } else {
                    _userState.value = UserDetailState.Error("User not found")
                }
            } catch (e: Exception) {
                _userState.value = UserDetailState.Error(e.message ?: "Failed to load user details")
            }
        }
    }
    
    suspend fun updateUserRole(userId: String, newRole: UserRole) {
        try {
            userRepository.updateUser(userId, mapOf("role" to newRole.name))
            // Refresh user details after update
            loadUserDetails(userId)
        } catch (e: Exception) {
            _userState.value = UserDetailState.Error(e.message ?: "Failed to update user role")
        }
    }
    
    suspend fun deleteUser(userId: String) {
        try {
            userRepository.deleteUser(userId)
            // No need to refresh since we'll be navigating away
        } catch (e: Exception) {
            _userState.value = UserDetailState.Error(e.message ?: "Failed to delete user")
        }
    }
} 