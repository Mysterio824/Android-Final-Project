package com.androidfinalproject.hacktok.ui.mainDashboard.watchLater

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.SavedPost
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.model.enums.ReportCause
import com.androidfinalproject.hacktok.model.enums.ReportType
import com.androidfinalproject.hacktok.repository.PostRepository
import com.androidfinalproject.hacktok.repository.SavedPostRepository
import com.androidfinalproject.hacktok.repository.UserRepository
import com.androidfinalproject.hacktok.service.AuthService
import com.androidfinalproject.hacktok.service.LikeService
import com.androidfinalproject.hacktok.service.ReportService
import com.androidfinalproject.hacktok.ui.newPost.PRIVACY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchLaterViewModel @Inject constructor(
    private val authService: AuthService,
    private val reportService: ReportService,
    private val likeService: LikeService,
    private val postRepository: PostRepository,
    private val savedPostRepository: SavedPostRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(WatchLaterState())
    val state: StateFlow<WatchLaterState> = _state.asStateFlow()

    init {
        loadWatchLaterPosts()
    }

    fun onAction(action: WatchLaterAction) {
        when (action) {
            is WatchLaterAction.OnPostClick -> loadPost(action.postId)
            is WatchLaterAction.OnUserClick -> loadUserProfile(action.userId)
            is WatchLaterAction.OnLikeClick -> likePost(action.postId, action.emoji)
            is WatchLaterAction.OnUnLikeClick -> unLikePost(action.postId)
            is WatchLaterAction.OnLikesShowClick -> loadLikesUser(action.targetId)
            is WatchLaterAction.OnImageClick -> loadImage(action.imageUrl)
            is WatchLaterAction.OnSavePost -> savePost(action.postId)
            is WatchLaterAction.OnDeleteSavedPost -> deleteSavedPost(action.postId)
            is WatchLaterAction.OnPostEditClick -> editPost(action.postId)
            is WatchLaterAction.OnDeletePost -> deletePost(action.postId)
            is WatchLaterAction.SubmitReport -> submitReport(action.reportedItemId, action.reportType, action.reportCause)
            is WatchLaterAction.UpdateSharePost -> _state.update { it.copy(sharePost = action.post, showShareDialog = true) }
            is WatchLaterAction.UpdateSharePrivacy -> _state.update { it.copy(sharePrivacy = action.privacy) }
            is WatchLaterAction.UpdateShareCaption -> _state.update { it.copy(shareCaption = action.caption) }
            is WatchLaterAction.OnSharePost -> sharePost(action.post, action.caption, action.privacy)
            is WatchLaterAction.DismissShareDialog -> _state.update { it.copy(showShareDialog = false) }
            is WatchLaterAction.RemovePost -> removePost(action.postId)
            is WatchLaterAction.Refresh -> loadWatchLaterPosts()
        }
    }

    private fun deletePost(postId: String) {
        viewModelScope.launch {
            try {
                postRepository.deletePost(postId)
            } catch(e: Exception) {
                _state.update {
                    Log.e("WatchlaterViewmodel", e.message.toString())
                    it.copy(error = e.message)
                }
            }
        }
    }

    private fun loadWatchLaterPosts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val currentUser = authService.getCurrentUser()
                if (currentUser != null) {
                    val userId = currentUser.id ?: return@launch
                    
                    // Load saved posts
                    val savedPosts = savedPostRepository.getSavedPostsByUser(userId)
                        .map { it.postId }

                    // Load actual post objects for each saved post
                    val posts = savedPosts.mapNotNull { postId ->
                        try {
                            postRepository.getPost(postId)
                        } catch (e: Exception) {
                            // If post is not found, remove it from saved posts
                            savedPostRepository.deleteSavedPost(postId)
                            null
                        }
                    }.sortedByDescending { it.createdAt }

                    val referencePosts = mutableMapOf<String, Post>()
                    val referenceUsers = mutableMapOf<String, User>()
                    val postUsers = mutableMapOf<String, User>()

                    posts.forEach { post ->
                        // Load the author of the post
                        val author = userRepository.getUserById(post.userId)
                        if (author != null && post.id != null) {
                            postUsers[post.id] = author
                        }

                        // Load referenced posts and their authors
                        post.refPostId?.let { refId ->
                            try {
                                val refPost = postRepository.getPost(refId)
                                if (refPost != null) {
                                    referencePosts[refId] = refPost
                                    val refUser = userRepository.getUserById(refPost.userId)
                                    if (refUser != null) {
                                        referenceUsers[refPost.userId] = refUser
                                    }
                                }
                            } catch (e: Exception) {
                                // Skip if referenced post is not found
                            }
                        }
                    }

                    _state.update {
                        it.copy(
                            posts = posts,
                            user = currentUser,
                            currentUserId = userId,
                            referencePosts = referencePosts,
                            referenceUsers = referenceUsers,
                            savedPosts = savedPosts,
                            postUsers = postUsers,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to load posts: ${e.message}", isLoading = false) }
            }
        }
    }

    private fun savePost(postId: String) {
        viewModelScope.launch {
            try {
                val currentUser = state.value.user
                if (currentUser != null) {
                    val userId = currentUser.id ?: return@launch
                    val savedPost = SavedPost(
                        userId = userId,
                        postId = postId
                    )
                    savedPostRepository.savePost(savedPost)
                    // Update the state with the new saved post
                    _state.update { it.copy(
                        savedPosts = it.savedPosts + postId
                    ) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to save post: ${e.message}") }
            }
        }
    }

    private fun deleteSavedPost(postId: String) {
        viewModelScope.launch {
            try {
                val currentUser = state.value.user
                if (currentUser != null) {
                    val userId = currentUser.id ?: return@launch
                    savedPostRepository.deleteSavedPost(postId)
                    // Update the state by removing the unsaved post
                    _state.update { it.copy(
                        savedPosts = it.savedPosts.filter { it != postId }
                    ) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to delete saved post: ${e.message}") }
            }
        }
    }

    private fun removePost(postId: String) {
        viewModelScope.launch {
            try {
                // Remove from saved posts
                val currentUser = state.value.user
                if (currentUser != null) {
                    val userId = currentUser.id ?: return@launch
                    savedPostRepository.deleteSavedPost(postId)
                    
                    // Update state
                    _state.update { currentState ->
                        currentState.copy(
                            posts = currentState.posts.filter { it.id != postId },
                            savedPosts = currentState.savedPosts.filter { it != postId }
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to remove post: ${e.message}") }
            }
        }
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

    private fun likePost(postId: String, emoji: String) {
        viewModelScope.launch {
            val updatedPost = likeService.likePost(postId, emoji) ?: return@launch

            _state.update { currentState ->
                val updatedPosts = currentState.posts.map { post ->
                    if (post.id == updatedPost.id) updatedPost else post
                }
                currentState.copy(posts = updatedPosts)
            }
        }
    }

    private fun unLikePost(postId: String) {
        viewModelScope.launch {
            val updatedPost = likeService.unlikePost(postId) ?: return@launch

            _state.update { currentState ->
                val updatedPosts = currentState.posts.map { post ->
                    if (post.id == updatedPost.id) updatedPost else post
                }
                currentState.copy(posts = updatedPosts)
            }
        }
    }

    private fun submitReport(reportedItemId: String, reportType: ReportType, reportCause: ReportCause) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, userMessage = null) }
            try {
                reportService.submitReport(
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
    }

    private suspend fun clearMessageAfterDelay() {
        delay(1000)
        _state.update { it.copy(userMessage = null) }
    }

    private suspend fun clearErrorAfterDelay() {
        delay(3000)
        _state.update { it.copy(error = null) }
    }

    private fun editPost(postId: String) {
        viewModelScope.launch {
            try {
                val post = postRepository.getPost(postId)
                if (post != null) {
                    // Navigate to edit post screen
                    // This will be handled by the navigation component
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to load post for editing: ${e.message}") }
            }
        }
    }

    private fun sharePost(post: Post, caption: String, privacy: PRIVACY) {
        viewModelScope.launch {
            try {
                val newPost = Post(
                    content = caption,
                    userId = state.value.user?.id ?: return@launch,
                    refPostId = post.id,
                    privacy = privacy.name
                )
                postRepository.addPost(newPost)
                _state.update { it.copy(userMessage = "Post shared successfully") }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to share post: ${e.message}") }
            }
        }
    }

    private fun loadPost(postId: String) {
        viewModelScope.launch {
            try {
                val post = postRepository.getPost(postId)
                if (post != null) {
                    // Navigate to post detail screen
                    // This will be handled by the navigation component
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to load post: ${e.message}") }
            }
        }
    }

    private fun loadUserProfile(userId: String) {
        viewModelScope.launch {
            try {
                val user = userRepository.getUserById(userId)
                if (user != null) {
                    // Navigate to user profile screen
                    // This will be handled by the navigation component
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to load user profile: ${e.message}") }
            }
        }
    }

    private fun loadImage(imageUrl: String) {
        // This function is typically handled by the UI layer
        // The image loading is usually done by the Image composable or a custom image loader
        // No need to implement anything here as it's just a UI action
    }
}