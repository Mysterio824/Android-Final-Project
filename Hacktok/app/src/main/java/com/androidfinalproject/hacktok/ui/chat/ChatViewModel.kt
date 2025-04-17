package com.androidfinalproject.hacktok.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.Message
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.repository.AuthRepository
import com.androidfinalproject.hacktok.repository.ChatRepository
import com.androidfinalproject.hacktok.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private var otherUserId: String? = null
    
    private val _state = MutableStateFlow(ChatState())
    val state = _state.asStateFlow()

    fun setUserId(userId: String) {
        otherUserId = userId
        loadChat()
    }

    private fun loadChat() {
        val userId = otherUserId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            try {
                val firebaseUser = authRepository.getCurrentUser()
                if (firebaseUser == null) {
                    _state.update { it.copy(error = "Not authenticated", isLoading = false) }
                    return@launch
                }

                // Get or create chat
                val chatId = chatRepository.getOrCreateChat(firebaseUser.uid, userId)
                
                // Load other user's data
                val otherUser = userRepository.getUserById(userId)
                if (otherUser == null) {
                    _state.update { it.copy(error = "User not found", isLoading = false) }
                    return@launch
                }

                // Convert FirebaseUser to our User model using the companion object method
                val currentUser = User.fromFirebaseUser(firebaseUser)

                // Update state with user info
                _state.update { it.copy(
                    currentUser = currentUser,
                    otherUser = otherUser,
                    isLoading = false
                ) }

                // Load messages
                chatRepository.getChatMessagesFlow(chatId).collect { messages ->
                    _state.update { it.copy(messages = messages) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = e.message ?: "An error occurred",
                    isLoading = false
                ) }
            }
        }
    }

    fun onAction(action: ChatAction) {
        when (action) {
            is ChatAction.SendMessage -> sendMessage(action.message)
            is ChatAction.DeleteMessage -> deleteMessage(action.messageId)
            is ChatAction.LoadInitialMessages -> loadChat()
            is ChatAction.ToggleMute -> toggleMute()
            is ChatAction.CreateGroup -> createGroup()
            is ChatAction.FindInChat -> findInChat()
            is ChatAction.DeleteChat -> deleteChat()
            is ChatAction.BlockUser -> blockUser()
            is ChatAction.NavigateToManageUser -> {} // Handled by navigation
            ChatAction.NavigateBack -> {} // Handled by navigation
        }
    }

    private fun sendMessage(content: String) {
        if (content.isBlank()) return

        viewModelScope.launch {
            try {
                val currentUser = authRepository.getCurrentUser() ?: return@launch
                val chatId = chatRepository.getOrCreateChat(currentUser.uid, otherUserId!!)

                val message = Message(
                    senderId = currentUser.uid,
                    content = content,
                    createdAt = Date()
                )

                chatRepository.sendMessage(chatId, message)
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to send message: ${e.message}") }
            }
        }
    }

    private fun deleteMessage(messageId: String?) {
        if (messageId == null) return

        viewModelScope.launch {
            try {
                val currentUser = authRepository.getCurrentUser() ?: return@launch
                val chatId = chatRepository.getOrCreateChat(currentUser.uid, otherUserId!!)
                chatRepository.deleteMessage(chatId, messageId)
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to delete message: ${e.message}") }
            }
        }
    }

    private fun deleteChat() {
        viewModelScope.launch {
            try {
                val currentUser = authRepository.getCurrentUser() ?: return@launch
                val chatId = chatRepository.getOrCreateChat(currentUser.uid, otherUserId!!)
                chatRepository.deleteChat(chatId)
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to delete chat: ${e.message}") }
            }
        }
    }

    private fun toggleMute() {
        _state.update { it.copy(isUserMuted = !it.isUserMuted) }
        // TODO: Implement mute functionality in repository
    }

    private fun blockUser() {
        // TODO: Implement block user functionality
    }

    private fun createGroup() {
        // TODO: Implement group creation
    }

    private fun findInChat() {
        // TODO: Implement chat search
    }
}

