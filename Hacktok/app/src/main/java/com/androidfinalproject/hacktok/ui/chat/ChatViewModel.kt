package com.androidfinalproject.hacktok.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class ChatViewModel : ViewModel() {
    private val _state = MutableStateFlow(ChatState())
    val state = _state.asStateFlow()

    fun  onAction(action: ChatAction){
        when(action) {
            is ChatAction.SendMessage -> sendMessage(action.message)
            is ChatAction.DeleteMessage -> deleteMessage(action.messageId)
            is ChatAction.LoadInitialMessages -> loadInitialMessages()
            is ChatAction.ToggleMute -> toggleMute()
            is ChatAction.CreateGroup -> createGroup()
            is ChatAction.FindInChat -> findInChat()
            is ChatAction.DeleteChat -> deleteChat()
            is ChatAction.BlockUser -> blockUser()
            is ChatAction.NavigateToManageUser -> TODO()
            ChatAction.NavigateBack-> TODO()
        }
    }

    fun sendMessage(content: String) {
        if (content.isBlank()) return

        val newMessage = Message(
            id = UUID.randomUUID().toString(),
            senderId = _state.value.currentUser.username,
            content = content,
            createdAt = Date()
        )

        viewModelScope.launch {
            // Trong thực tế, sẽ gửi tin nhắn lên server tại đây

            // Cập nhật state với tin nhắn mới
            _state.update { currentState ->
                currentState.copy(
                    messages = currentState.messages + newMessage
                )
            }
        }
    }

    fun deleteMessage(messageId: String?) {
        viewModelScope.launch {
            // Trong thực tế, sẽ xóa tin nhắn trên server tại đây

            // Cập nhật state bằng cách loại bỏ tin nhắn
            _state.update { currentState ->
                currentState.copy(
                    messages = currentState.messages.filter { it.id != messageId }
                )
            }
        }
    }

    // Hàm mô phỏng việc tải dữ liệu ban đầu
    fun loadInitialMessages() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                // Trong thực tế, sẽ tải tin nhắn từ server tại đây
                // Hiện tại, tạo một số tin nhắn mẫu
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
        val user1 = _state.value.currentUser.username
        val user2 = _state.value.otherUser.username

        return listOf(
            Message(
                id = UUID.randomUUID().toString(),
                senderId = user2,
                content = "Chào bạn, bạn khỏe không?",
                createdAt = Date(System.currentTimeMillis() - 3600000)
            ),
            Message(
                id = UUID.randomUUID().toString(),
                senderId = user1,
                content = "Mình khỏe, còn bạn thì sao?",
                createdAt = Date(System.currentTimeMillis() - 3500000)
            ),
            Message(
                id = UUID.randomUUID().toString(),
                senderId = user2,
                content = "Mình cũng khỏe. Hôm nay bạn đã làm gì?",
                createdAt = Date(System.currentTimeMillis() - 3400000)
            ),
            Message(
                id = UUID.randomUUID().toString(),
                senderId = user1,
                content = "Mình đang code một ứng dụng Android. Còn bạn?",
                createdAt = Date(System.currentTimeMillis() - 3300000)
            )
        )
    }

    private fun toggleMute() {
        _state.update { currentState ->
            currentState.copy(
                isUserMuted = !currentState.isUserMuted
            )
        }
        // Trong thực tế, cần lưu trạng thái này vào database
    }

    private fun createGroup() {
        // Thực hiện logic tạo nhóm
        // Hiện tại chỉ in log vì chưa có database
        println("Creating group with user: ${_state.value.otherUser.username}")
        // Trong thực tế, sẽ chuyển đến màn hình tạo nhóm
    }

    private fun findInChat() {
        // Chức năng tìm kiếm trong chat
        println("Finding in chat with user: ${_state.value.otherUser.username}")
        // Trong thực tế, sẽ hiển thị UI tìm kiếm
    }

    private fun deleteChat() {
        viewModelScope.launch {
            // Xóa tất cả tin nhắn
            _state.update { currentState ->
                currentState.copy(
                    messages = emptyList()
                )
            }
            // Trong thực tế, sẽ xóa chat trên server
        }
    }

    private fun blockUser() {
        // Thực hiện chức năng block user
        println("Blocking user: ${_state.value.otherUser.username}")
        // Trong thực tế, sẽ cập nhật trạng thái block trong database
    }
}
