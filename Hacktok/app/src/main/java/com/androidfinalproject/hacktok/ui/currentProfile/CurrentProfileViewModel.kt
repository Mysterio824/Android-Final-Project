package com.androidfinalproject.hacktok.ui.currentProfile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.repository.PostRepository
import com.androidfinalproject.hacktok.repository.UserRepository
import com.androidfinalproject.hacktok.service.LikeService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrentProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val likeService: LikeService
) : ViewModel() {
    private val _state = MutableStateFlow<CurrentProfileState>(CurrentProfileState.Loading)
    val state = _state.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            _state.value = CurrentProfileState.Loading
            try {
                val user = userRepository.getCurrentUser()
                if (user != null) {
                    val userId = user.id ?: return@launch
                    val posts = postRepository.getPostsByUser(userId)
                        .sortedByDescending { it.createdAt }

                    val referencePosts = mutableMapOf<String, Post>()
                    val referenceUsers = mutableMapOf<String, User>()

                    posts.forEach { post ->
                        post.refPostId?.let { refId ->
                            val refPost = postRepository.getPost(refId)
                            if (refPost != null) {
                                referencePosts[refId] = refPost
                                val refUser = userRepository.getUserById(refPost.userId)
                                if (refUser != null) {
                                    referenceUsers[refPost.userId] = refUser
                                }
                            }
                        }
                    }
                    _state.value = CurrentProfileState.Success(
                        user = user,
                        posts = posts,
                        friendCount = user.friends.size,
                        referencePosts = referencePosts,
                        referenceUsers = referenceUsers
                    )
                } else {
                    _state.value = CurrentProfileState.Error("User not found")
                }
            } catch (e: Exception) {
                _state.value = CurrentProfileState.Error("Failed to load user: ${e.message}")
            }
        }
    }

    private fun loadLikesUser(targetId: String) {
        viewModelScope.launch {
            try {
                val current = _state.value
                val likeUsers = likeService.getPostLike(targetId)
                if (current is CurrentProfileState.Success) {
                    _state.value = current.copy(
                        listLikeUser = likeUsers
                    )
                }
            } catch(e: Exception){
                Log.d("CurrentProfileViewModel", e.message.toString())
            }
        }
    }

    private fun editPost(postId: String?, newContent: String) {
        viewModelScope.launch {
            try {
                if (postId != null) {
                    val updates = mapOf("content" to newContent)
                    postRepository.updatePost(postId, updates)
                    loadCurrentUser() // Reload to refresh the posts
                }
            } catch (e: Exception) {
                _state.value = CurrentProfileState.Error("Failed to edit post: ${e.message}")
            }
        }
    }

    private fun deletePost(postId: String?) {
        viewModelScope.launch {
            try {
                if (postId != null) {
                    postRepository.deletePost(postId)
                    Log.d("CurrentProfileViewModel", "Post deleted successfully")
                    loadCurrentUser() // Reload to refresh the posts
                } else {
                    _state.value = CurrentProfileState.Error("Cannot delete post: Invalid post ID")
                }
            } catch (e: Exception) {
                _state.value = CurrentProfileState.Error("Failed to delete post: ${e.message}")
            }
        }
    }

    fun onAction(action: CurrentProfileAction) {
        when (action) {
            is CurrentProfileAction.RetryLoading -> {
                loadCurrentUser()
            }
            is CurrentProfileAction.OnDeletePost -> {
                deletePost(action.postId)
            }
            is CurrentProfileAction.ShowShareDialog -> {
                val current = _state.value
                if (current is CurrentProfileState.Success) {
                    _state.value = current.copy(showShareDialog = true)
                }
            }
            is CurrentProfileAction.UpdatePrivacy -> {
                val current = _state.value
                if (current is CurrentProfileState.Success) {
                    _state.value = current.copy(sharePrivacy = action.privacy)
                }
            }
            is CurrentProfileAction.UpdateSharePost -> {
                val current = _state.value
                if (current is CurrentProfileState.Success) {
                    _state.value = current.copy(
                        postToShare = action.post,
                        showShareDialog = true
                    )
                }
            }
            is CurrentProfileAction.UpdateShareCaption -> {
                val current = _state.value
                if (current is CurrentProfileState.Success) {
                    _state.value = current.copy(shareCaption = action.caption)
                }
            }
            is CurrentProfileAction.DismissShareDialog -> {
                val current = _state.value
                if (current is CurrentProfileState.Success) {
                    _state.value = current.copy(
                        showShareDialog = false,
                        shareCaption = "",
                        postToShare = null
                    )
                }
            }
            is CurrentProfileAction.OnSharePost -> {
                val current = _state.value
                if (current is CurrentProfileState.Success) {
                    viewModelScope.launch {
                        val post = Post(
                            content = action.caption,
                            userId = current.user.id ?: return@launch,
                            refPostId = action.post.id ?: return@launch,
                            privacy = action.privacy.name,
                        )
                        try {
                            postRepository.addPost(post)
                            loadCurrentUser() // Reload after share
                        } catch (e: Exception) {
                            _state.value = CurrentProfileState.Error("Failed to share post: ${e.message}")
                        }
                    }
                }
            }

            is CurrentProfileAction.OnLike -> {
                viewModelScope.launch {
                    val updatedPost = if (action.isLike) {
                        likeService.likePost(action.postId, action.emoji)
                    } else {
                        likeService.unlikePost(action.postId)
                    }

                    if (updatedPost == null) return@launch

                    val posts: List<Post> = when (val state = _state.value) {
                        is CurrentProfileState.Success -> state.posts
                        else -> return@launch
                    }

                    val newList = posts.map { post ->
                        if (post.id == updatedPost.id) updatedPost else post
                    }

                    _state.update { current ->
                        if (current is CurrentProfileState.Success) {
                            current.copy(posts = newList)
                        } else current
                    }
                }
            }

            is CurrentProfileAction.OnLikesShowClick -> loadLikesUser(action.targetId)

            is CurrentProfileAction.Refresh  -> loadCurrentUser()

            else -> {

            } // Handle other actions
        }
    }

    fun List<Post>.replacePost(updatedPost: Post): List<Post> {
        return map { if (it.id == updatedPost.id) updatedPost else it }
    }

}