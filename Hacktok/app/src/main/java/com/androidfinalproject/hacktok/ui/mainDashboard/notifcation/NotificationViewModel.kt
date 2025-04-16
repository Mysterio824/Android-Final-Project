package com.androidfinalproject.hacktok.ui.mainDashboard.notifcation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.repository.NotificationRepository
import com.androidfinalproject.hacktok.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(NotificationState())
    val state: StateFlow<NotificationState> = _state.asStateFlow()

    init {
        loadNotifications()
    }

    fun onAction(action: NotificationAction) {
        when (action) {
            is NotificationAction.OnMarkAsRead -> markNotificationAsRead(action.notificationId)
            is NotificationAction.OnDeleteNotification -> deleteNotification(action.notificationId)
            is NotificationAction.OnRefresh -> loadNotifications(true)
            else -> {}
        }
    }

    private fun loadNotifications(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null) }
                val notifications = MockData.getMockNotifications(10)
                _state.update { it.copy(notifications = notifications, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Failed to load notifications", isLoading = false) }
            }
        }
    }

    private fun markNotificationAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                notificationRepository.markAsRead(notificationId)
                // Update the local state to reflect the change
                _state.update { state ->
                    val updatedNotifications = state.notifications.map { notification ->
                        if (notification.id == notificationId) {
                            notification.copy(isRead = true)
                        } else {
                            notification
                        }
                    }
                    state.copy(notifications = updatedNotifications)
                }
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }

    private fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            try {
                notificationRepository.deleteNotification(notificationId)
                // Remove from local state
                _state.update { state ->
                    val updatedNotifications = state.notifications.filter { it.id != notificationId }
                    state.copy(notifications = updatedNotifications)
                }
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }
}