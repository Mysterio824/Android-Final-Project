package com.androidfinalproject.hacktok.ui.messageDashboard

import android.util.Log
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
import androidx.lifecycle.ViewModelStore
import com.androidfinalproject.hacktok.model.enums.RelationshipStatus
import com.androidfinalproject.hacktok.service.RelationshipService

@HiltViewModel
class MessageDashboardViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val relationshipService: RelationshipService
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

                _state.update { it.copy(currentUserId = currentUser.uid) }

                val chats = chatRepository.getUserChats(currentUser.uid)

                val chatItems = chats.mapNotNull { chat ->
                    val otherUserId = chat.participants.firstOrNull { it != currentUser.uid } ?: return@mapNotNull null
                    val user = userRepository.getUserById(otherUserId) ?: return@mapNotNull null
                    val relationInfo = relationshipService.getRelationship(otherUserId)
                    ChatItem(user, chat, relationInfo)
                }

                _state.update {
                    it.copy(
                        chatList = chatItems,
                        filterChatList = chatItems,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = e.message ?: "An error occurred",
                        isLoading = false
                    )
                }
            }
        }
    }


    fun onAction(action: MessageDashboardAction) {
        when (action) {
            is MessageDashboardAction.SearchQueryChanged -> onSearch(action.query)
            is MessageDashboardAction.Refresh -> {
                loadChats()
            }
            is MessageDashboardAction.MuteChat -> muteChat(action.chatId)
            is MessageDashboardAction.DeleteChat -> deleteChat(action.chatId)
            is MessageDashboardAction.BlockChat -> blockChat(action.userId)
            is MessageDashboardAction.UnBlockChat -> unblockChat(action.userId)
            else -> {}
        }
    }

    private fun blockChat(userId: String) {
        viewModelScope.launch {
            val success = relationshipService.blockUser(userId)
            if (!success) return@launch

            _state.update { currentState ->
                val updatedChatList = currentState.chatList.map {
                    if (it.user.id == userId) {
                        it.copy(
                            relationInfo = it.relationInfo.copy(status = RelationshipStatus.BLOCKING)
                        )
                    } else it
                }
                val updatedFilterChatList = currentState.filterChatList.map {
                    if (it.user.id == userId) {
                        it.copy(
                            relationInfo = it.relationInfo.copy(status = RelationshipStatus.NONE)
                        )
                    } else it
                }
                currentState.copy(chatList = updatedChatList, filterChatList = updatedFilterChatList)
            }
        }
    }

    private fun unblockChat(userId: String) {
        viewModelScope.launch {
            val success = relationshipService.unblockUser(userId)
            if (!success) return@launch

            _state.update { currentState ->
                val updatedChatList = currentState.chatList.map {
                    if (it.user.id == userId) {
                        it.copy(
                            relationInfo = it.relationInfo.copy(status = RelationshipStatus.NONE)
                        )
                    } else it
                }
                val updatedFilterChatList = currentState.filterChatList.map {
                    if (it.user.id == userId) {
                        it.copy(
                            relationInfo = it.relationInfo.copy(status = RelationshipStatus.NONE)
                        )
                    } else it
                }
                currentState.copy(chatList = updatedChatList, filterChatList = updatedFilterChatList)
            }
        }
    }

    private fun deleteChat(chatId: String) {
        viewModelScope.launch {
            chatRepository.deleteChat(chatId)

            _state.update { currentState ->
                val updatedChatList = currentState.chatList.filter { it.chat.id != chatId }
                val updatedFilterChatList = currentState.filterChatList.filter { it.chat.id != chatId }
                currentState.copy(chatList = updatedChatList, filterChatList = updatedFilterChatList)
            }
        }
    }

    private fun muteChat(chatId: String) {
        TODO("Not yet implemented")
    }

    private fun onSearch(query: String) {
        viewModelScope.launch {
            _state.update { it.copy(searchQuery = mutableStateOf(query)) }

            _state.update { currentState ->
                val updatedChatList = currentState.chatList.filter { it.user.username?.contains(query) ?: false }
                currentState.copy(filterChatList = updatedChatList)
            }
        }
    }
}