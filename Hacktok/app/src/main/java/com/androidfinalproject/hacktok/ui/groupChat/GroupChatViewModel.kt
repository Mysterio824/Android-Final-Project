package com.androidfinalproject.hacktok.ui.groupChat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.Group
import com.androidfinalproject.hacktok.model.Message
import com.androidfinalproject.hacktok.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class GroupChatViewModel : ViewModel() {
    private val _state = MutableStateFlow(GroupChatState())
    val state = _state.asStateFlow()

    fun onAction(action: GroupChatAction) {
        when (action) {
            is GroupChatAction.SendMessage -> sendMessage(action.message)
            is GroupChatAction.DeleteMessage -> deleteMessage(action.messageId)
            is GroupChatAction.LoadInitialMessages -> loadInitialMessages()
            is GroupChatAction.ToggleMute -> toggleMute()
            is GroupChatAction.RenameGroup -> renameGroup(action.newName)
            is GroupChatAction.FindInChat -> findInChat()
            is GroupChatAction.LeaveGroup -> leaveGroup()
            is GroupChatAction.NavigateToManageGroup -> TODO()
            GroupChatAction.NavigateBack -> TODO()
        }
    }

    fun sendMessage(content: String) {
        if (content.isBlank()) return

        val newMessage = Message(
            id = UUID.randomUUID().toString(),
            senderId = _state.value.currentUser.id ?: "",
            content = content,
            createdAt = Date()
        )

        viewModelScope.launch {
            // In real app, would send message to server here

            // Update state with new message
            _state.update { currentState ->
                currentState.copy(
                    messages = currentState.messages + newMessage
                )
            }
        }
    }

    fun deleteMessage(messageId: String?) {
        viewModelScope.launch {
            // In real app, would delete message on server here

            // Update state by removing the message
            _state.update { currentState ->
                currentState.copy(
                    messages = currentState.messages.filter { it.id != messageId }
                )
            }
        }
    }

    // Function to load initial messages
    fun loadInitialMessages() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                // In real app, would load messages from server here
                // For now, create some sample messages
                val demoMessages = createDemoMessages()

                _state.update { currentState ->
                    currentState.copy(
                        messages = demoMessages,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        error = "Không thể tải tin nhắn: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun createDemoMessages(): List<Message> {
        val user1 = _state.value.currentUser.id ?: ""
        val user2 = "user2" // Member from the group
        val user3 = "user3" // Another member from the group

        return listOf(
            Message(
                id = UUID.randomUUID().toString(),
                senderId = user2,
                content = "Chào cả nhóm!",
                createdAt = Date(System.currentTimeMillis() - 3600000)
            ),
            Message(
                id = UUID.randomUUID().toString(),
                senderId = user1,
                content = "Chào mọi người, dự án của chúng ta tiến triển thế nào rồi?",
                createdAt = Date(System.currentTimeMillis() - 3500000)
            ),
            Message(
                id = UUID.randomUUID().toString(),
                senderId = user3,
                content = "Mình đã hoàn thành phần UI, đang chờ API.",
                createdAt = Date(System.currentTimeMillis() - 3400000)
            ),
            Message(
                id = UUID.randomUUID().toString(),
                senderId = user2,
                content = "Mình sẽ gửi API doc vào ngày mai nhé!",
                createdAt = Date(System.currentTimeMillis() - 3300000)
            )
        )
    }

    private fun toggleMute() {
        _state.update { currentState ->
            currentState.copy(
                isGroupMuted = !currentState.isGroupMuted
            )
        }
        // In real app, would save this state to database
    }

    private fun renameGroup(newName: String) {
        if (newName.isBlank()) return

        _state.update { currentState ->
            currentState.copy(
                group = currentState.group.copy(groupName = newName)
            )
        }
        // In real app, would update group name on server
    }

    private fun findInChat() {
        // Search functionality in chat
        println("Finding in group chat: ${_state.value.group.groupName}")
        // In real app, would show search UI
    }

    private fun leaveGroup() {
        // Leave group functionality
        println("Leaving group: ${_state.value.group.groupName}")
        // In real app, would remove user from group on server and navigate back
    }
}