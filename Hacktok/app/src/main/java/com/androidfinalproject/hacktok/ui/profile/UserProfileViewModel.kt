package com.androidfinalproject.hacktok.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.model.Post
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserProfileViewModel(userId: String) : ViewModel() {
    private val TAG = "UserProfileViewModel"
    private val _state = MutableStateFlow(UserProfileState())
    val state: StateFlow<UserProfileState> = _state.asStateFlow()
    private val firestore = try {
        FirebaseFirestore.getInstance()
    } catch (e: Exception) {
        Log.e(TAG, "Failed to initialize Firestore", e)
        null
    }

    init {
        Log.d(TAG, "Initializing with userId: $userId")
        loadUserProfile(userId)
    }

    fun onAction(action: UserProfileAction) {
        when (action) {
            is UserProfileAction.AddFriend -> addFriend(state.value.user!!.id)
            is UserProfileAction.Unfriend -> unfriend(state.value.user!!.id)
            is UserProfileAction.BlockUser -> blockUser(state.value.user!!.id)
            is UserProfileAction.RefreshProfile -> loadProfile()
            is UserProfileAction.LikePost -> likePost(action.postId)
            else -> {}
        }
    }

    fun loadUserProfile(userId: String?) {
        if (userId == null) {
            Log.e(TAG, "loadUserProfile called with null userId")
            _state.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    error = "User ID is null"
                )
            }
            return
        }

        Log.d(TAG, "Loading user profile for userId: $userId")
        viewModelScope.launch {
            // Set loading state
            _state.update { currentState ->
                currentState.copy(isLoading = true, error = null)
            }

            try {
                if (firestore == null) {
                    Log.d(TAG, "Firestore is null, using mock data")
                    // Fallback to mock data if Firebase is not available
                    val mockUser = MockData.mockUsers.firstOrNull { it.id == userId }
                    if (mockUser == null) {
                        Log.e(TAG, "User not found in mock data for userId: $userId")
                        _state.update { currentState ->
                            currentState.copy(
                                isLoading = false,
                                error = "User not found"
                            )
                        }
                        return@launch
                    }

                    val mockPosts = MockData.mockPosts.filter { it.userId == userId }
                    Log.d(TAG, "Found ${mockPosts.size} mock posts for user")
                    
                    // Ensure post.user is set to avoid NPE in PostContent
                    val processedPosts = mockPosts.map { post ->
                        post.copy(user = mockUser)
                    }
                    
                    Log.d(TAG, "Updating state with mock user: ${mockUser.username}")
                    _state.update { currentState ->
                        currentState.copy(
                            user = mockUser,
                            posts = processedPosts,
                            isLoading = false
                        )
                    }
                    return@launch
                }

                // Fetch user data from Firestore
                Log.d(TAG, "Fetching user data from Firestore for userId: $userId")
                val userDoc = firestore.collection("users").document(userId).get().await()
                if (!userDoc.exists()) {
                    Log.e(TAG, "User document doesn't exist in Firestore for userId: $userId")
                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            error = "User not found"
                        )
                    }
                    return@launch
                }

                val user = userDoc.toObject(User::class.java)
                if (user == null) {
                    Log.e(TAG, "Failed to parse user data from Firestore for userId: $userId")
                    _state.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            error = "Failed to parse user data"
                        )
                    }
                    return@launch
                }

                // Fetch user's posts
                Log.d(TAG, "Fetching posts for userId: $userId")
                val postsSnapshot = firestore.collection("posts")
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()

                val posts = postsSnapshot.documents.mapNotNull { doc ->
                    try {
                        val post = doc.toObject(Post::class.java)
                        // Ensure post.user is set to avoid NPE in PostContent
                        post?.copy(user = user)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing post document: ${e.message}")
                        null
                    }
                }
                Log.d(TAG, "Found ${posts.size} posts for user")

                // Update state with loaded data
                Log.d(TAG, "Updating state with user: ${user.username}")
                _state.update { currentState ->
                    currentState.copy(
                        user = user,
                        posts = posts,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading profile: ${e.message}", e)
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
        Log.d(TAG, "Refreshing profile")
        state.value.user?.id?.let { loadUserProfile(it) }
    }

    private fun addFriend(userId: String?) {
        viewModelScope.launch {
            try {
                // Implement friend addition logic with Firestore
                _state.update { currentState ->
                    currentState.copy(isFriend = true)
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(error = "Failed to add friend: ${e.message}")
                }
            }
        }
    }

    private fun unfriend(userId: String?) {
        viewModelScope.launch {
            try {
                // Implement unfriend logic with Firestore
                _state.update { currentState ->
                    currentState.copy(isFriend = false)
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(error = "Failed to unfriend: ${e.message}")
                }
            }
        }
    }

    private fun blockUser(userId: String?) {
        viewModelScope.launch {
            try {
                // Implement block user logic with Firestore
                _state.update { currentState ->
                    currentState.copy(isBlocked = true, isFriend = false)
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(error = "Failed to block user: ${e.message}")
                }
            }
        }
    }

    fun likePost(postId: String) {
        // TODO: Implement like post functionality with Firestore
    }
}