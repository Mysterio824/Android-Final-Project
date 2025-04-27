package com.androidfinalproject.hacktok.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.RelationInfo
import com.androidfinalproject.hacktok.model.enums.ReportCause
import com.androidfinalproject.hacktok.model.enums.ReportType
import com.androidfinalproject.hacktok.repository.PostRepository
import com.androidfinalproject.hacktok.repository.UserRepository
import com.androidfinalproject.hacktok.service.AuthService
import com.androidfinalproject.hacktok.service.LikeService
import com.androidfinalproject.hacktok.service.RelationshipService
import com.androidfinalproject.hacktok.service.ReportService
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val relationshipService: RelationshipService,
    private val postRepository: PostRepository,
    private val authService: AuthService,
    private val reportService: ReportService,
    private val likeService: LikeService,
    private val userRepository: UserRepository
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

    private val _profileUserId = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val relationshipInfoFlow: StateFlow<RelationInfo?> = _profileUserId.flatMapLatest { profileId ->
        if (profileId == null) {
            flowOf(null) // No profile loaded, no relationship info
        } else {
            relationshipService.observeMyRelationships()
                .map { relationshipsMap ->
                    relationshipsMap[profileId] // Extract info for the specific profile user
                }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        viewModelScope.launch {
            relationshipInfoFlow.collect { relationInfo ->
                _state.update { it.copy(relationshipInfo = relationInfo) }
            }
        }
    }

    fun onAction(action: UserProfileAction) {
        val profileUserId = state.value.user?.id ?: run {
            Log.e(TAG, "User ID is null, cannot perform action: $action")
            return
        }

        when (action) {
            is UserProfileAction.DismissShareDialog -> _state.update { it.copy(showShareDialog = false) }
            is UserProfileAction.UpdateShareCaption -> _state.update { it.copy(shareCaption = action.caption) }
            is UserProfileAction.UpdateSharePrivacy -> _state.update { it.copy(sharePrivacy = action.privacy) }
            is UserProfileAction.UpdateSharePost -> _state.update { it.copy(sharePost = action.post, showShareDialog = true) }
            is UserProfileAction.OnSharePost -> {
                viewModelScope.launch {
                    val post = Post(
                        content = action.caption,
                        userId = state.value.currentUser?.id ?: return@launch,
                        refPostId = action.post.id,
                        privacy = action.privacy.name,
                    )
                    try {
                        postRepository.addPost(post)
                    } catch (e: Exception) {
                        Log.d("ERROR", e.toString())
                    }
                }
            }
            is UserProfileAction.LikePost -> likePost(action.postId, action.emoji)
            is UserProfileAction.UnlikePost -> unLikePost(action.postId)
            is UserProfileAction.OnLikesShowClick -> loadLikesUser(action.targetId)
            is UserProfileAction.OnSavePost -> savePost(action.postId)
            else -> {}
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
                    is UserProfileAction.SubmitReport
                        -> submitReport(
                            action.reportedItemId,
                            action.reportType,
                            action.reportCause
                        )

                    is UserProfileAction.LikePost -> { likePost(action.postId, action.emoji); true }
                    UserProfileAction.RefreshProfile -> { loadProfile(); true }
                    else -> true
                }

                if (!success && action !is UserProfileAction.LikePost && action !is UserProfileAction.RefreshProfile) {
                    _state.update { it.copy(error = "Action failed: ${action::class.simpleName}") }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error performing action $action: ${e.message}", e)
                _state.update { it.copy(error = "Error performing action: ${e.message}") }
            }
        }
    }

    private fun savePost(postId: String) {
        TODO("Not yet implemented")
    }

    private fun loadLikesUser(targetId: String) {
        viewModelScope.launch {
            try {
                val likeUsers = likeService.getPostLike(targetId)
                _state.update{
                    it.copy(listLikeUser = likeUsers)
                }
            } catch(e: Exception){
                Log.d("PostDetailViewModel", e.message.toString())
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

        // Update the profile user ID being observed
        _profileUserId.value = userId

        viewModelScope.launch {
             val loadedUser: User?
             val loadedPosts: List<Post>
             val loadError: String?

             try {
                 // Fetch user and posts, relationship is handled by the flow
                 val userAndPostsResult = fetchUserAndPosts(userId)
                 loadedUser = userAndPostsResult.first
                 loadedPosts = userAndPostsResult.second.sortedByDescending { it.createdAt }
                 loadError = if (loadedUser == null /* && userAndPostsResult.second.isEmpty() */) "User not found" else null // Simplified error check
                 val relationship = relationshipService.getFriends(userId)
                 if (loadedUser != null) {
                     val refPostIds = loadedPosts.mapNotNull { it.refPostId }.toSet()

                     val refPosts = mutableMapOf<String, Post>()
                     val refUsers = mutableMapOf<String, User>()
                     val postUsers = mutableMapOf<String, User>()

                     for (post in loadedPosts) {
                         // Map the author of the post
                         if (post.id != null) {
                             postUsers[post.id] = loadedUser // Because these posts are all written by loadedUser
                         }

                         // Map the referenced posts
                         post.refPostId?.let { refId ->
                             val refPost = postRepository.getPost(refId)
                             if (refPost != null) {
                                 refPosts[refPost.id!!] = refPost
                                 val refUser = userRepository.getUserById(refPost.userId)
                                 if (refUser != null) {
                                     refUsers[refUser.id!!] = refUser
                                 }
                             }
                         }
                     }
                     Log.d(TAG, "Updating state with User: ${loadedUser.id}, Posts: ${loadedPosts.size}")
                     _state.update {
                         it.copy(
                            user = loadedUser,
                            posts = loadedPosts,
                             postUsers = postUsers,
                             isLoading = false,
                            error = null,
                            userIdBeingLoaded = null,
                            numberOfFriends = relationship.size,
                            currentUser = authService.getCurrentUser(),
                             referencePosts = refPosts,
                             referenceUsers = refUsers,
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
                    doc.toObject(Post::class.java)
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
    
    private fun loadProfile() {
        Log.d(TAG, "Refreshing profile")
        state.value.user?.id?.let { loadUserProfile(it) }
    }
    
    private fun likePost(postId: String, emoji: String) {
        Log.d(TAG, "LikePost action called for postId: $postId (Not Implemented)")
        viewModelScope.launch {
            _state.update { currentState ->
                val updatedPost = likeService.likePost(postId, emoji) ?: return@launch

                val newList = currentState.posts.map { post ->
                    if (post.id == updatedPost.id) updatedPost else post
                }

                currentState.copy(posts = newList)
            }
        }
    }

    private fun unLikePost(postId: String) {
        Log.d(TAG, "UnLikePost action called for postId: $postId (Not Implemented)")
        viewModelScope.launch {
            _state.update { currentState ->
                val updatedPost = likeService.unlikePost(postId) ?: return@launch

                val newList = currentState.posts.map { post ->
                    if (post.id == updatedPost.id) updatedPost else post
                }

                currentState.copy(posts = newList)
            }
        }
    }

    private fun submitReport(reportedItemId: String, reportType: ReportType, reportCause: ReportCause) : Boolean {
        var result = false
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, userMessage = null) }
            try {
                result = reportService.submitReport(
                    reportedItemId = reportedItemId,
                    reportType = reportType,
                    reportCause = reportCause
                )
                _state.update { it.copy(isLoading = false, userMessage = "Report submitted successfully.") }
                launch { clearMessageAfterDelay() }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to submit report: ${e.message}"
                    )
                }
                launch { clearErrorAfterDelay() }
            }
        }
        return result
    }

    private suspend fun clearMessageAfterDelay() {
        delay(1000)
        _state.update { it.copy(userMessage = null) }
    }

    private suspend fun clearErrorAfterDelay() {
        delay(3000)
        _state.update { it.copy(error = null) }
    }
}