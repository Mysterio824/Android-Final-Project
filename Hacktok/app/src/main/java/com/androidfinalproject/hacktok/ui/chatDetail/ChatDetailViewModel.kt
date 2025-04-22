package com.androidfinalproject.hacktok.ui.chatDetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.Group
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.model.enums.RelationshipStatus
import com.androidfinalproject.hacktok.repository.ChatRepository
import com.androidfinalproject.hacktok.repository.UserRepository
import com.androidfinalproject.hacktok.service.RelationshipService
import com.androidfinalproject.hacktok.ui.messageDashboard.MessageDashboardAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val relationshipService: RelationshipService,
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private var otherUserId: String? = null
    private val _state = MutableStateFlow(ChatDetailState())
    val state: StateFlow<ChatDetailState> = _state.asStateFlow()

    private fun loadData(){
        val userId = otherUserId ?: return
        viewModelScope.launch {

            // Lấy thông tin từ savedStateHandle để biết đây là chat nhóm hay cá nhân
            val isGroup = false
            val currentUser = userRepository.getCurrentUser()
                ?: return@launch

            val chatId = chatRepository.getOrCreateChat(currentUser.id!!, userId)

            val otherUser = userRepository.getUserById(userId)
                ?: run{
                    _state.update { it.copy(error = "User not found", isLoading = false) }
                    return@launch
                }

            if (isGroup) {
                // Khởi tạo nhóm (demo)
                val group = Group(
                    id = "group1",
                    groupName = "Nhóm chat",
                    description = "Mô tả nhóm chat",
                    creatorId = "user1",
                    members = listOf("user1", "user2", "user3"),
                    admins = listOf("user1"),
                    isPublic = true,
                    createdAt = java.util.Date(),
                    coverImage = null
                )

                val membersList = listOf(
                    User(id = "user1", username = "User One", email = "user1@example.com"),
                    User(id = "user2", username = "User Two", email = "user2@example.com"),
                    User(id = "user3", username = "User Three", email = "user3@example.com")
                )

                _state.update { currentState ->
                    currentState.copy(
                        currentUser = currentUser,
                        group = group,
                        membersList = membersList,
                        isGroup = true
                    )
                }
            } else {

                _state.update { currentState ->
                    currentState.copy(
                        chatId = chatId,
                        currentUser = currentUser,
                        otherUser = otherUser,
                        isGroup = false
                    )
                }
            }
        }

    }

    fun setUserId(userId: String) {
        otherUserId = userId
        loadData()
    }

    fun onAction(action: ChatDetailAction) {
        when (action) {
            is ChatDetailAction.ToggleMute -> toggleMute()
            is ChatDetailAction.RenameGroup -> renameGroup(action.newName)
            is ChatDetailAction.FindInChat -> findInChat()
            is ChatDetailAction.LeaveGroup -> leaveGroup()
            is ChatDetailAction.CreateGroup -> createGroup()
            is ChatDetailAction.AddMember -> addMember()
            is ChatDetailAction.DeleteChat -> deleteChat()
            is ChatDetailAction.BlockUser -> blockUser()
            is ChatDetailAction.UnBlockUser -> unblockUser()
            else -> {}
        }
    }

    private fun toggleMute() {
        viewModelScope.launch {
            val value = state.value
            val res = chatRepository.setMuteState(value.chatId, value.currentUser.id!!, !value.isUserMuted)
            if(res){
                _state.update { currentState ->
                    currentState.copy(
                        isUserMuted = !currentState.isUserMuted
                    )
                }
            }
        }
    }

    private fun renameGroup(newName: String) {
        if (newName.isNotBlank()) {
            _state.update { currentState ->
                currentState.copy(
                    group = currentState.group.copy(
                        groupName = newName
                    )
                )
            }
            // In a real app, you would call a repository to update the group name on the server
        }
    }

    private fun findInChat() {
        // Implement search functionality
        // This would typically open a search UI component
    }

    private fun leaveGroup() {
        // Implement logic to leave the group
        // This would typically call a repository to remove the user from the group
        viewModelScope.launch {
            try {
                // Call repository to leave group
                // Then navigate back (this would be done in the UI layer)
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        error = "Failed to leave group: ${e.message}"
                    )
                }
            }
        }
    }

    private fun createGroup() {
        // Implement logic to create a new group
        // Trong trường hợp chat cá nhân, tạo nhóm mới với người dùng hiện tại và người dùng khác
    }

    private fun addMember() {
        // Implement logic to add a member to the group
    }

    private fun deleteChat() {
        viewModelScope.launch {
            chatRepository.deleteChat(state.value.chatId)
        }
    }

    private fun blockUser() {
        viewModelScope.launch {
            val res = relationshipService.blockUser(state.value.otherUser!!.id!!)
            if (!res) return@launch

            _state.update { it.copy(relation = it.relation.copy(status = RelationshipStatus.BLOCKING)) }
        }
    }

    private fun unblockUser() {
        viewModelScope.launch {
            val res = relationshipService.unblockUser(state.value.otherUser!!.id!!)
            if (!res) return@launch

            _state.update { it.copy(relation = it.relation.copy(status = RelationshipStatus.NONE)) }
        }
    }
}