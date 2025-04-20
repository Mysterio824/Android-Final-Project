package com.androidfinalproject.hacktok.ui.mainDashboard.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.repository.PostRepository
import com.androidfinalproject.hacktok.service.AuthService
import com.androidfinalproject.hacktok.service.ReportService
import com.androidfinalproject.hacktok.model.enums.ReportCause
import com.androidfinalproject.hacktok.model.enums.ReportType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.androidfinalproject.hacktok.repository.PostShareRepository
import com.androidfinalproject.hacktok.service.LikeService
import com.androidfinalproject.hacktok.repository.UserRepository
import com.androidfinalproject.hacktok.service.RelationshipService

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val authService: AuthService,
    private val postRepository: PostRepository,
    private val reportService: ReportService,
    private val relationshipService: RelationshipService,
    private val postShareRepository: PostShareRepository,
    private val likeService: LikeService,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(HomeScreenState())
    val state: StateFlow<HomeScreenState> = _state.asStateFlow()
    private lateinit var friendList: List<String>

    init {
        loadPosts()
    }

    fun onAction(action: HomeScreenAction) {
        when (action) {
            is HomeScreenAction.OnSharePost -> {
                viewModelScope.launch {
                    try {
                        postShareRepository.sharePost(
                            postId = action.post.id ?: return@launch,
                            caption = action.caption,
                            privacy = action.privacy.name,
                        )
                    } catch (e: Exception) {
                        Log.d("ERROR", e.toString())
                    }
                }
            }
            is HomeScreenAction.LoadMorePosts -> loadMorePosts()
            is HomeScreenAction.UpdateSharePrivacy -> _state.update { it.copy(sharePrivacy = action.privacy) }
            is HomeScreenAction.UpdateShareCaption -> _state.update { it.copy(shareCaption = action.caption) }
            is HomeScreenAction.UpdateSharePost -> _state.update { it.copy(sharePost = action.post, showShareDialog = true) }
            is HomeScreenAction.DismissShareDialog -> _state.update { it.copy(showShareDialog = false) }
            is HomeScreenAction.LikePost -> likePost(action.postId)
            is HomeScreenAction.UnLikePost -> unLikePost(action.postId)
            is HomeScreenAction.SharePost -> sharePost(action.postId)
            is HomeScreenAction.SubmitReport -> submitReport(
                reportedItemId = action.reportedItemId,
                reportType = action.reportType,
                reportCause = action.reportCause
            )
            else -> {}
        }
    }

    private suspend fun loadAuthorNames(posts: List<Post>): Map<String, String> {
        val distinctUserIds = posts.map { it.userId }.distinct()
        val users = distinctUserIds.mapNotNull { userRepository.getUserById(it) }

        return posts.associate { post ->
            val name = users.find { it.id == post.userId }?.fullName ?: "Unknown"
            post.id!! to name
        }
    }

    private fun loadPosts() {
        viewModelScope.launch {
            val currentUser = authService.getCurrentUser()
            val userId = currentUser?.id ?: ""
            val friendMap = relationshipService.getFriends(userId)
            friendList = friendMap.keys.toList() // List of friend userIds
            Log.d("CURRENT_USER", userId)
            Log.d("FRIEND_LIST", friendList.toString())

            _state.update{
                it.copy(
                    user = authService.getCurrentUser(),
                    isLoading = true,
                    error = null
                )
            }

            try {
                postRepository.resetPagination()

                val posts = postRepository.getNextPosts(userId = userId, friendList = friendList)

                val postAuthorMap = loadAuthorNames(posts)
                _state.update {
                    it.copy(
                        posts = posts,
                        postAuthorNames = postAuthorMap,
                        isLoading = false,
                        isPaginating =  false,
                        hasMorePosts = posts.size == 10
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load content: ${e.message}"
                    )
                }
                launch { clearErrorAfterDelay() }
            }
        }
    }

    private fun loadMorePosts() {
        viewModelScope.launch {
            _state.update { it.copy(isPaginating = true) }
            val newPosts = postRepository.getNextPosts(userId = state.value.user?.id ?: "", friendList = friendList)
            val newAuthorMap = loadAuthorNames(newPosts)
            _state.update {
                val allPosts = (it.posts + newPosts).distinctBy { post -> post.id }
                val allAuthors = it.postAuthorNames + newAuthorMap
                it.copy(
                    posts = allPosts,
                    postAuthorNames = allAuthors,
                    isPaginating = false,
                    hasMorePosts = newPosts.size == 10
                )
            }
        }
    }

    private fun likePost(postId: String) {
        viewModelScope.launch {
            _state.update { currentState ->
                val updatedPost = likeService.likePost(postId) ?: return@launch

                val newList = currentState.posts.map { post ->
                    if (post.id == updatedPost.id) updatedPost.copy(
                        user = post.user,
                    ) else post
                }

                currentState.copy(posts = newList)
            }
        }
    }

    private fun unLikePost(postId: String) {
        viewModelScope.launch {
            _state.update { currentState ->
                val updatedPost = likeService.unlikePost(postId) ?: return@launch

                val newList = currentState.posts.map { post ->
                    if (post.id == updatedPost.id) updatedPost.copy(
                        user = post.user,
                    ) else post
                }

                currentState.copy(posts = newList)
            }
        }
    }

    private fun sharePost(postId: String) {
        // Placeholder logic for sharing a post
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
}