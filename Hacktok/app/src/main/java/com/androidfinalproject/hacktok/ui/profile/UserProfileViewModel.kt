package com.androidfinalproject.hacktok.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.RelationInfo
import com.androidfinalproject.hacktok.model.enums.RelationshipStatus
import com.androidfinalproject.hacktok.service.AuthService
import com.androidfinalproject.hacktok.service.RelationshipService
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val relationshipService: RelationshipService,
    private val authService: AuthService
) : ViewModel() {
    private val TAG = "UserProfileViewModel"
    private val _state = MutableStateFlow(UserProfileState())
    val state: StateFlow<UserProfileState> = _state.asStateFlow()
    private val firestore = try {
        FirebaseFirestore.getInstance()
    } catch (e: Exception) {
        Log.e(TAG, "Failed to initialize Firestore", e)
        null
    }

    fun onAction(action: UserProfileAction) {
        val profileUserId = state.value.user?.id ?: run {
            Log.e(TAG, "User ID is null, cannot perform action: $action")
            return
        }
        
        viewModelScope.launch {
            try {
                 val success = when (action) {
                    UserProfileAction.SendFriendRequest -> relationshipService.sendFriendRequest(profileUserId)
                    UserProfileAction.CancelFriendRequest -> relationshipService.cancelFriendRequest(profileUserId)
                    UserProfileAction.Unfriend -> relationshipService.cancelFriendRequest(profileUserId)
                    UserProfileAction.AcceptFriendRequest -> relationshipService.acceptFriendRequest(profileUserId)
                    UserProfileAction.DeclineFriendRequest -> relationshipService.declineFriendRequest(profileUserId)
                    UserProfileAction.BlockUser -> relationshipService.blockUser(profileUserId)
                    UserProfileAction.UnblockUser -> relationshipService.unblockUser(profileUserId)
                    
                    is UserProfileAction.LikePost -> { likePost(action.postId); true }
                    UserProfileAction.RefreshProfile -> { loadProfile(); true }
                    else -> true
                }

                if (!success && action !is UserProfileAction.LikePost && action !is UserProfileAction.RefreshProfile) {
                    _state.update { it.copy(error = "Action failed: ${action::class.simpleName}") }
                } else {
                    if (action !is UserProfileAction.LikePost && action !is UserProfileAction.RefreshProfile) {
                         kotlinx.coroutines.delay(200)
                         loadRelationshipInfo(profileUserId)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error performing action $action: ${e.message}", e)
                _state.update { it.copy(error = "Error performing action: ${e.message}") }
            }
        }
    }

    fun loadUserProfile(userId: String?) {
        if (userId == null) {
            Log.e(TAG, "loadUserProfile called with null userId")
            _state.update { it.copy(isLoading = false, error = "User ID is null", userIdBeingLoaded = null) }
            return
        }

        if ((state.value.user?.id == userId && !state.value.isLoading && state.value.error == null) || 
            (state.value.isLoading && state.value.userIdBeingLoaded == userId)) {
             Log.d(TAG, "Profile for $userId already loaded or loading. Skipping.")
             return
        }

        Log.d(TAG, "Loading user profile for userId: $userId")
        _state.update { it.copy(isLoading = true, error = null, userIdBeingLoaded = userId) }

        viewModelScope.launch {
            var loadedUser: User? = null
            var loadedPosts: List<Post> = emptyList()
            var loadError: String? = null
            var currentUserId: String? = null
            var relationshipInfo: RelationInfo? = null

            try {
                currentUserId = authService.getCurrentUserId()
                
                coroutineScope { 
                    val userAndPostsDeferred = async { fetchUserAndPosts(userId) }
                    val relationshipDeferred = async { 
                        if (currentUserId != null && currentUserId != userId) {
                             fetchRelationshipInfo(currentUserId, userId)
                        } else {
                             null
                        }
                    }

                    val userAndPostsResult = userAndPostsDeferred.await()
                    loadedUser = userAndPostsResult.first
                    loadedPosts = userAndPostsResult.second
                    loadError = if (loadedUser == null && userAndPostsResult.second.isEmpty()) "User not found or failed to load posts" else null
                    
                    relationshipInfo = relationshipDeferred.await()
                }

                 if (loadedUser != null) {
                     Log.d(TAG, "Updating state with User: ${loadedUser?.id}, Posts: ${loadedPosts.size}, Relation: ${relationshipInfo?.status}")
                     _state.update {
                         it.copy(
                            user = loadedUser,
                            posts = loadedPosts,
                            relationshipInfo = relationshipInfo,
                            currentUserId = currentUserId,
                            isLoading = false,
                            error = null, 
                            userIdBeingLoaded = null
                        )
                    }
                } else {
                     Log.e(TAG, "Final loadedUser is null. Error: ${loadError ?: "Unknown error"}")
                     _state.update { 
                         it.copy(
                             isLoading = false, 
                             error = loadError ?: "Failed to load profile", 
                             userIdBeingLoaded = null
                        ) 
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error loading profile coroutineScope: ${e.message}", e)
                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = "Failed to load profile: ${e.message}",
                        userIdBeingLoaded = null
                    )
                }
            }
        }
    }
    
    private suspend fun loadRelationshipInfo(profileUserId: String) {
         val currentUserId = authService.getCurrentUserId()
         if (currentUserId != null && currentUserId != profileUserId) {
             try {
                 val relationships = relationshipService.getMyRelationships()
                 val specificRelation = relationships[profileUserId]
                 _state.update { it.copy(relationshipInfo = specificRelation) } 
             } catch (e: Exception) {
                  Log.e(TAG, "Error reloading relationship info: ${e.message}", e)
                  _state.update { it.copy(relationshipInfo = null, error = "Failed to update relationship status") }
             }
         } else {
              _state.update { it.copy(relationshipInfo = null) }
         }
    }

    private suspend fun fetchUserAndPosts(userId: String): Pair<User?, List<Post>> {
        return try {
            if (firestore == null) throw IllegalStateException("Firestore not initialized")

            val userDoc = firestore.collection("users").document(userId).get().await()
            if (!userDoc.exists()) {
                 Log.e(TAG, "User document doesn't exist in Firestore for userId: $userId")
                 return Pair(null, emptyList())
            }
            var user = userDoc.toObject(User::class.java)
            if (user == null) {
                 Log.e(TAG, "Failed to parse user data from Firestore for userId: $userId")
                 return Pair(null, emptyList())
            }
            user = user.copy(id = userDoc.id)

            val postsSnapshot = firestore.collection("posts")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            val posts = postsSnapshot.documents.mapNotNull { doc ->
                try {
                    val post = doc.toObject(Post::class.java)
                    post?.copy(user = user)
                } catch (e: Exception) {
                    Log.e(TAG, "Error parsing post document: ${e.message}")
                    null
                }
            }
            Pair(user, posts)
        } catch (e: Exception) {
            Log.e(TAG, "Error in fetchUserAndPosts for $userId: ${e.message}", e)
            Pair(null, emptyList())
        }
    }
    
    private suspend fun fetchRelationshipInfo(currentUserId: String, profileUserId: String): RelationInfo? {
         return try {
             val relationships = relationshipService.getMyRelationships()
             relationships[profileUserId]
         } catch (e: Exception) {
             Log.e(TAG, "Error fetching relationship info between $currentUserId and $profileUserId: ${e.message}")
             null
         }
     }

    private fun loadProfile() {
        Log.d(TAG, "Refreshing profile")
        state.value.user?.id?.let { loadUserProfile(it) }
    }
    
    private fun likePost(postId: String) {
        Log.d(TAG, "LikePost action called for postId: $postId (Not Implemented)")
    }
}