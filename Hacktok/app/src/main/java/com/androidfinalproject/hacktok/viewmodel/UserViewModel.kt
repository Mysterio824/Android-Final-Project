package com.androidfinalproject.hacktok.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UserProfileState {
    object Loading : UserProfileState()
    data class Success(val user: User) : UserProfileState()
    data class Error(val message: String) : UserProfileState()
}

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userProfileState = MutableStateFlow<UserProfileState>(UserProfileState.Loading)
    val userProfileState: StateFlow<UserProfileState> = _userProfileState.asStateFlow()

    private val _isFollowing = MutableStateFlow(false)
    val isFollowing: StateFlow<Boolean> = _isFollowing.asStateFlow()

    private val _followersCount = MutableStateFlow(0)
    val followersCount: StateFlow<Int> = _followersCount.asStateFlow()

    private val _followingCount = MutableStateFlow(0)
    val followingCount: StateFlow<Int> = _followingCount.asStateFlow()

    private val _videosCount = MutableStateFlow(0)
    val videosCount: StateFlow<Int> = _videosCount.asStateFlow()

    init {
        loadCurrentUser()
    }

    fun loadCurrentUser() {
        viewModelScope.launch {
            _userProfileState.value = UserProfileState.Loading
            try {
                val user = userRepository.getCurrentUser()
                if (user != null) {
                    _userProfileState.value = UserProfileState.Success(user)
                    if (user.id != null) {
                        loadUserStats(user.id)
                    }
                } else {
                    _userProfileState.value = UserProfileState.Error("User not found")
                }
            } catch (e: Exception) {
                _userProfileState.value = UserProfileState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            _userProfileState.value = UserProfileState.Loading
            try {
                val user = userRepository.getUserById(userId)
                if (user != null) {
                    _userProfileState.value = UserProfileState.Success(user)
                    loadUserStats(userId)
                } else {
                    _userProfileState.value = UserProfileState.Error("User not found")
                }
            } catch (e: Exception) {
                _userProfileState.value = UserProfileState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    private fun loadUserStats(userId: String) {
        viewModelScope.launch {
            try {
                _followersCount.value = userRepository.getFollowersCount()
                _followingCount.value = userRepository.getFollowingCount()
                _videosCount.value = userRepository.getVideosCount()
                _isFollowing.value = userRepository.isFollowingUser(userId)
            } catch (e: Exception) {
                // Handle error silently or update UI accordingly
            }
        }
    }

    fun updateProfile(user: User) {
        viewModelScope.launch {
            try {
                val success = userRepository.updateUserProfile(user)
                if (success) {
                    _userProfileState.value = UserProfileState.Success(user)
                } else {
                    _userProfileState.value = UserProfileState.Error("Failed to update profile")
                }
            } catch (e: Exception) {
                _userProfileState.value = UserProfileState.Error(e.message ?: "Failed to update profile")
            }
        }
    }

    fun updateBio(bio: String) {
        viewModelScope.launch {
            try {
                val success = userRepository.updateUserBio(bio)
                if (success) {
                    // Reload user profile to get updated data
                    loadCurrentUser()
                } else {
                    _userProfileState.value = UserProfileState.Error("Failed to update bio")
                }
            } catch (e: Exception) {
                _userProfileState.value = UserProfileState.Error(e.message ?: "Failed to update bio")
            }
        }
    }

    fun updateProfileImage(imageUrl: String) {
        viewModelScope.launch {
            try {
                val success = userRepository.updateUserProfileImage(imageUrl)
                if (success) {
                    // Reload user profile to get updated data
                    loadCurrentUser()
                } else {
                    _userProfileState.value = UserProfileState.Error("Failed to update profile image")
                }
            } catch (e: Exception) {
                _userProfileState.value = UserProfileState.Error(e.message ?: "Failed to update profile image")
            }
        }
    }

    fun followUser(userId: String) {
        viewModelScope.launch {
            try {
                val success = userRepository.followUser(userId)
                if (success) {
                    _isFollowing.value = true
                    loadUserStats(userId)
                }
            } catch (e: Exception) {
                // Handle error silently or update UI accordingly
            }
        }
    }

    fun unfollowUser(userId: String) {
        viewModelScope.launch {
            try {
                val success = userRepository.unfollowUser(userId)
                if (success) {
                    _isFollowing.value = false
                    loadUserStats(userId)
                }
            } catch (e: Exception) {
                // Handle error silently or update UI accordingly
            }
        }
    }
} 