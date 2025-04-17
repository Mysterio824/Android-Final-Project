package com.androidfinalproject.hacktok.ui.messageDashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.Chat
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.repository.AuthRepository
import com.androidfinalproject.hacktok.repository.ChatRepository
import com.androidfinalproject.hacktok.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.mutableStateOf

@HiltViewModel
class MessageDashboardViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(MessageDashboardState())
    val state = _state.asStateFlow()

    init {
        loadChats()
    }

    private fun loadChats() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            try {
                val currentUser = authRepository.getCurrentUser()
                if (currentUser == null) {
                    _state.update { it.copy(error = "Not authenticated", isLoading = false) }
                    return@launch
                }

                // Get all chats for the current user
                val chats = chatRepository.getUserChats(currentUser.uid)
                
                // Get user details for each chat
                val users = chats.mapNotNull { chat ->
                    // Find the ID of the other participant
                    val otherUserId = chat.participants.firstOrNull { it != currentUser.uid }
                    // Fetch user details only if otherUserId is found
                    otherUserId?.let { userRepository.getUserById(it) }
                }

                // Deduplicate the user list based on user ID
                val distinctUsers = users.distinctBy { it.id }

                _state.update { it.copy(
                    userList = distinctUsers, // Use the distinct list
                    isLoading = false
                ) }
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = e.message ?: "An error occurred",
                    isLoading = false
                ) }
            }
        }
    }

    fun onAction(action: MessageDashboardAction) {
        when (action) {
            is MessageDashboardAction.SearchQueryChanged -> {
                _state.update { it.copy(searchQuery = mutableStateOf(action.query)) }
            }
            is MessageDashboardAction.Refresh -> {
                loadChats()
            }
            else -> {} // Other actions are handled by the screen
        }
    }
}