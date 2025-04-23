package com.androidfinalproject.hacktok.ui.mainDashboard.notifcation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.service.CommentService
import com.androidfinalproject.hacktok.service.NotificationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationService: NotificationService,
    private val commentService: CommentService
) : ViewModel() {
    private val _state = MutableStateFlow(NotificationState())
    val state: StateFlow<NotificationState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true, error = null) }
                notificationService.observeMyNotifications().collect{notifications ->
                    _state.update { it.copy(notifications = notifications, isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Failed to load notifications", isLoading = false) }
            }
        }
    }

    fun onAction(action: NotificationAction) {
        when (action) {
            is NotificationAction.OnMarkAsRead -> markNotificationAsRead(action.notificationId)
            is NotificationAction.OnCommentClick -> onCommentClick(action.commentId)
            is NotificationAction.OnDeleteNotification -> deleteNotification(action.notificationId)
            else -> {}
        }
    }

    private fun onCommentClick(commentId: String) {
        viewModelScope.launch {
            val comment = commentService.getComment(commentId)
                ?: return@launch
            _state.update {
                it.copy(
                    commentId = commentId,
                    postId = comment.postId,
                    navigateComment = true
                )
            }
        }
    }

    private fun markNotificationAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                val success = notificationService.markNotificationAsRead(notificationId)
                if (success) {
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
                } else {
                    // Handle failure (e.g., show a snackbar)
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to mark notification as read") }
            }
        }
    }

    private fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            try {
                val success = notificationService.deleteNotification(notificationId)
                if (success) {
                    _state.update { state ->
                        val updatedNotifications = state.notifications.filter { it.id != notificationId }
                        state.copy(notifications = updatedNotifications)
                    }
                } else {
                    // Handle failure
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to delete notification") }
            }
        }
    }
}