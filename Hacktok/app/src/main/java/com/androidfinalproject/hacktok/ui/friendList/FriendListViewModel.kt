package com.androidfinalproject.hacktok.ui.friendList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.model.RelationInfo
import com.androidfinalproject.hacktok.model.RelationshipStatus
import com.androidfinalproject.hacktok.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.bson.types.ObjectId
import java.time.Instant

class FriendListViewModel(private val currentUserId: String) : ViewModel() {
    private val _state = MutableStateFlow(FriendListState())
    val state: StateFlow<FriendListState> = _state.asStateFlow()

    init {
        loadFriends(currentUserId)
    }

    fun onAction(action: FriendListAction) {
        when (action) {
            is FriendListAction.SearchQueryChanged -> updateSearchQuery(action.query)
            is FriendListAction.SendFriendRequest -> sendRequest(action.userId, action.isSend)
            is FriendListAction.OnAcceptFriendRequest -> addFriend(action.userId, action.isAccepted)
            is FriendListAction.OnBlockFriend -> blockFriend(action.userId)
            is FriendListAction.OnUnBlockFriend -> unBlockFriend(action.userId)
            else -> {}
        }
    }

    private fun loadFriends(userId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val mockUsers = MockData.mockUsers

                val updatedRelations = mockUsers.mapIndexed { index, user ->
                    val status = when (index) {
                        0 -> RelationshipStatus.FRIENDS
                        1 -> RelationshipStatus.BLOCKED
                        2 -> RelationshipStatus.BLOCKING
                        else -> RelationshipStatus.NONE
                    }

                    user.id!! to RelationInfo(
                        id = "${minOf(userId, user.id)}_${maxOf(userId, user.id)}",
                        status = status,
                        lastActionByMe = status == RelationshipStatus.BLOCKING, // assume "me" did the blocking
                        updatedAt = Instant.now()
                    )
                }.toMap()

                _state.update {
                    it.copy(
                        users = mockUsers,
                        filteredUsers = mockUsers,
                        isLoading = false,
                        relations = updatedRelations
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(isLoading = false, error = "Failed to load friends: ${e.message}")
                }
            }
        }
    }


    private fun updateSearchQuery(query: String) {
        _state.update { current ->
            val filtered = if (query.isBlank()) current.users
            else current.users.filter {
                it.username.contains(query, ignoreCase = true) ||
                        it.email.contains(query, ignoreCase = true)
            }
            current.copy(searchQuery = query, filteredUsers = filtered)
        }
    }

    private fun sendRequest(userId: String, isSend: Boolean) {
        // isSend == true means send, false means cancel outgoing
        _state.update { current ->
            val info = current.relations[userId] ?: return@update current
            val newStatus = if (isSend) RelationshipStatus.PENDING_OUTGOING
            else RelationshipStatus.NONE
            current.copy(
                relations = current.relations + (userId to info.copy(
                    status         = newStatus,
                    lastActionByMe = true,
                    updatedAt      = Instant.now()
                ))
            )
        }
    }

    private fun addFriend(userId: String, isAccepted: Boolean) {
        // isAccepted == true means accept incoming, false means decline
        _state.update { current ->
            val info = current.relations[userId] ?: return@update current
            val newStatus = if (isAccepted) RelationshipStatus.FRIENDS
            else RelationshipStatus.NONE
            current.copy(
                relations = current.relations + (userId to info.copy(
                    status         = newStatus,
                    lastActionByMe = isAccepted,
                    updatedAt      = Instant.now()
                ))
            )
        }
    }

    private fun blockFriend(userId: String) {
        _state.update { current ->
            val info = current.relations[userId] ?: return@update current
            current.copy(
                relations = current.relations + (userId to info.copy(
                    status         = RelationshipStatus.BLOCKING,
                    lastActionByMe = true,
                    updatedAt      = Instant.now()
                ))
            )
        }
    }

    private fun unBlockFriend(userId: String) {
        _state.update { current ->
            val info = current.relations[userId] ?: return@update current
            current.copy(
                relations = current.relations + (userId to info.copy(
                    status         = RelationshipStatus.NONE,
                    lastActionByMe = true,
                    updatedAt      = Instant.now()
                ))
            )
        }
    }
}
