package com.androidfinalproject.hacktok.ui.mainDashboard

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
import java.util.Date


class DashboardViewModel : ViewModel() {
    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        loadPosts()
    }

    fun onAction(action: DashboardAction) {
        when (action) {
            is DashboardAction.LoadPosts -> loadPosts()
            is DashboardAction.LikePost -> likePost(action.postId)
            is DashboardAction.CommentPost -> commentOnPost(action.postId, action.comment)
            is DashboardAction.SharePost -> sharePost(action.postId)
            else -> {}
        }
    }

    private fun loadPosts() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val mockPosts = listOf(
                    Post(ObjectId(), "Hello world!", likeCount = 2, user = User(ObjectId(),"Kien","Kien@gmail.com")),
                    Post(ObjectId(), " world!", likeCount = 2, user = User(ObjectId(),"A","A@gmail.com")),
                    Post(ObjectId(), "Hello !",likeCount = 2, user = User(ObjectId(),"B","B@gmail.com")),
                    Post(ObjectId(), "Heworld!", likeCount = 2, user = User(ObjectId(),"C","C@gmail.com")),
                    Post(ObjectId(), "áđâsdá!", likeCount = 2, user = User(ObjectId(),"D","D@gmail.com")),
                )

                _state.update {
                    it.copy(
                        posts = mockPosts,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load posts: ${e.message}"
                    )
                }
            }
        }
    }

    private fun likePost(postId: String) {
        _state.update { currentState ->
            val updatedPosts = currentState.posts.map {
                if (it.id.toString() == postId) it.copy(likes = it.likes + 1) else it
            }
            currentState.copy(posts = updatedPosts)
        }
    }

    private fun commentOnPost(postId: String, comment: String) {
        _state.update { currentState ->
            val updatedPosts = currentState.posts.map {
                if (it.id.toString() == postId) {
                    val newComments = it.comments;
                    newComments.add(comment)
                    it.copy(comments = newComments);
                }else it
            }
            currentState.copy(posts = updatedPosts)
        }
    }

    private fun sharePost(postId: String) {
        // Placeholder logic for sharing a post
    }
}
