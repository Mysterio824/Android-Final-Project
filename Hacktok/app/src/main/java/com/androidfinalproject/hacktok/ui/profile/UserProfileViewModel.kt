package com.androidfinalproject.hacktok.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.bson.types.ObjectId

class UserProfileViewModel : ViewModel() {
    private val _state = MutableStateFlow(UserProfileState())
    val state: StateFlow<UserProfileState> = _state.asStateFlow()

    fun onAction(action: UserProfileAction) {
        when (action) {
            is UserProfileAction.AddFriend -> addFriend(action.userId)
            is UserProfileAction.Unfriend -> unfriend(action.userId)
            is UserProfileAction.BlockUser -> blockUser(action.userId)
            is UserProfileAction.RefreshProfile -> loadProfile()
            else -> {}
        }
    }

    fun loadUserProfile(userId: ObjectId?) {
        viewModelScope.launch {
            // Set loading state
            _state.update { currentState ->
                currentState.copy(isLoading = true, error = null)
            }

            try {
                // This would typically be an API call
                // Mock implementation for now
                val mockUser = User(
                    id = userId,
                    username = "example_user",
                    email = "user@example.com"
                )

                val mockPosts = listOf(
                    Post(
                        id = ObjectId(),
                        user = mockUser,
                        likeCount = 2,
                        content = "This is my first post!"
                    ),
                    Post(
                        id = ObjectId(),
                        user = mockUser,
                        likeCount = 4,
                        content = "Another day, another post!"
                    )
                )

                // Check if user is a friend (would come from repository)
                val isFriend = true
                val isBlocked = false

                // Update state with loaded data
                _state.update { currentState ->
                    currentState.copy(
                        user = mockUser,
                        posts = mockPosts,
                        isFriend = isFriend,
                        isBlocked = isBlocked,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                // Update state with error
                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = "Failed to load profile: ${e.message}"
                    )
                }
            }
        }
    }

    private fun loadProfile() {
        // Reload the current profile
        state.value.user?.id?.let { loadUserProfile(it) }
    }

    private fun addFriend(userId: ObjectId?) {
        viewModelScope.launch {
            // Implement friend addition logic
            _state.update { currentState ->
                currentState.copy(isFriend = true)
            }
        }
    }

    private fun unfriend(userId: ObjectId?) {
        viewModelScope.launch {
            // Implement unfriend logic
            _state.update { currentState ->
                currentState.copy(isFriend = false)
            }
        }
    }

    private fun blockUser(userId: ObjectId?) {
        viewModelScope.launch {
            // Implement block user logic
            _state.update { currentState ->
                currentState.copy(isBlocked = true, isFriend = false)
            }
        }
    }
}