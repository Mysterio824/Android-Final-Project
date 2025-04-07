package com.androidfinalproject.hacktok.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.MockData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UserProfileViewModel(userId : String) : ViewModel() {
    private val _state = MutableStateFlow(UserProfileState())
    val state: StateFlow<UserProfileState> = _state.asStateFlow()

    init {
        loadUserProfile(userId)
    }

    fun onAction(action: UserProfileAction) {
        when (action) {
            is UserProfileAction.AddFriend -> addFriend(state.value.user!!.id)
            is UserProfileAction.Unfriend -> unfriend(state.value.user!!.id)
            is UserProfileAction.BlockUser -> blockUser(state.value.user!!.id)
            is UserProfileAction.RefreshProfile -> loadProfile()
            else -> {}
        }
    }

    fun loadUserProfile(userId: String?) {
        viewModelScope.launch {
            // Set loading state
            _state.update { currentState ->
                currentState.copy(isLoading = true, error = null)
            }

            try {
                // This would typically be an API call
                // Mock implementation for now
                val mockUser = MockData.mockUsers.first()

                val mockPosts = MockData.mockPosts

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

    private fun addFriend(userId: String?) {
        viewModelScope.launch {
            // Implement friend addition logic
            _state.update { currentState ->
                currentState.copy(isFriend = true)
            }
        }
    }

    private fun unfriend(userId: String?) {
        viewModelScope.launch {
            // Implement unfriend logic
            _state.update { currentState ->
                currentState.copy(isFriend = false)
            }
        }
    }

    private fun blockUser(userId: String?) {
        viewModelScope.launch {
            // Implement block user logic
            _state.update { currentState ->
                currentState.copy(isBlocked = true, isFriend = false)
            }
        }
    }
}