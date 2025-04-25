package com.androidfinalproject.hacktok.ui.adminManage.userManagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.Report
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.model.enums.UserRole
import com.androidfinalproject.hacktok.repository.ReportRepository
import com.androidfinalproject.hacktok.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

sealed class UserDetailState {
    data object Loading : UserDetailState()
    data class Success(val user: User, val reports: List<Report> = emptyList(), val showBanDialog: Boolean = false) : UserDetailState()
    data class Error(val message: String) : UserDetailState()
}

@HiltViewModel
class UserDetailViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val reportRepository: ReportRepository,
) : ViewModel() {
    
    private val _userState = MutableStateFlow<UserDetailState>(UserDetailState.Loading)
    val userState: StateFlow<UserDetailState> = _userState.asStateFlow()
    
    fun loadUserDetails(userId: String) {
        viewModelScope.launch {
            _userState.value = UserDetailState.Loading
            try {
                val user = userRepository.getUserById(userId)
                val reports = reportRepository.getReportsForUser(userId)
                if (user != null) {
                    _userState.value = UserDetailState.Success(user, reports)
                } else {
                    _userState.value = UserDetailState.Error("User not found")
                }
            } catch (e: Exception) {
                _userState.value = UserDetailState.Error(e.message ?: "Failed to load user details")
            }
        }
    }

    fun showBanDialog() {
        val currentState = _userState.value
        if (currentState is UserDetailState.Success) {
            _userState.value = currentState.copy(showBanDialog = true)
        }
    }

    fun dismissBanDialog() {
        val currentState = _userState.value
        if (currentState is UserDetailState.Success) {
            _userState.value = currentState.copy(showBanDialog = false)
        }
    }

    fun banUser(
        userId: String,
        isPermanent: Boolean,
        durationDays: Int?,
        reason: String
    ) {
        viewModelScope.launch {
            try {
                val duration = if (isPermanent) {
                    Long.MAX_VALUE
                } else {
                    durationDays?.toLong() ?: 0L
                }

                // Step 1: Ban the user
                userRepository.banUser(userId, reason, duration)

                // Step 2: Resolve all pending reports targeting this user
                val reports = reportRepository.getReportsForUser(userId)
                reports.filter { it.status == "pending" }.forEach { report ->
                    report.id?.let { reportId ->
                        reportRepository.updateReport(
                            reportId,
                            mapOf(
                                "status" to "resolved",
                                "resolutionNote" to "target user is banned",
                                "resolvedAt" to Date()
                            )
                        )
                    }
                }

                // Step 3: Reload user detail (to get updated report list)
                loadUserDetails(userId)

                // Step 4: Dismiss dialog
                dismissBanDialog()

            } catch (e: Exception) {
                _userState.value = UserDetailState.Error(e.message ?: "Failed to ban user")
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