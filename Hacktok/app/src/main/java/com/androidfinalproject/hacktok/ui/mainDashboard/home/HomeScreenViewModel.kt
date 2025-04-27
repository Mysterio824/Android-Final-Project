package com.androidfinalproject.hacktok.ui.mainDashboard.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.Ad
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.service.AdService
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
import com.androidfinalproject.hacktok.service.LikeService
import com.androidfinalproject.hacktok.repository.UserRepository
import com.androidfinalproject.hacktok.service.RelationshipService
import com.androidfinalproject.hacktok.service.StoryService
import kotlinx.coroutines.flow.firstOrNull
import java.util.Date

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val authService: AuthService,
    private val postRepository: PostRepository,
    private val reportService: ReportService,
    private val relationshipService: RelationshipService,
    private val likeService: LikeService,
    private val userRepository: UserRepository,
    private val storyService: StoryService,
    private val adService: AdService
) : ViewModel() {
    private val _state = MutableStateFlow(HomeScreenState())
    val state: StateFlow<HomeScreenState> = _state.asStateFlow()
    private lateinit var friendList: List<String>

    init {
        _state.update { HomeScreenState() }

        viewModelScope.launch {
            reloadPosts()   // resets pagination and loads the first page
            loadStories()   // loads current user's and following's stories
            loadRandomAd()  // loads a random eligible ad
        }
    }

    private suspend fun loadRandomAd() {
        try {
            Log.d("HomeScreenViewModel", "Starting to load random ad")
            val currentUser = authService.getCurrentUser()
            if (currentUser == null) {
                Log.e("HomeScreenViewModel", "Failed to load ad: Current user is null")
                return
            }
            Log.d("HomeScreenViewModel", "Current user ID: ${currentUser.id}")

            // Get a random eligible ad
            val randomAd = adService.getRandomEligibleAd(currentUser.id!!)
            Log.d("HomeScreenViewModel", "Random ad result: ${randomAd?.id ?: "null"}")
            
            if (randomAd != null) {
                // Increment impressions
                Log.d("HomeScreenViewModel", "Incrementing impressions for ad: ${randomAd.id}")
                adService.incrementImpressions(randomAd.id!!)
                
                _state.update { it.copy(currentAd = randomAd) }
                Log.d("HomeScreenViewModel", "Successfully updated state with ad: ${randomAd.id}")
            } else {
                Log.d("HomeScreenViewModel", "No eligible ads found for user: ${currentUser.id}")
            }
        } catch (e: Exception) {
            Log.e("HomeScreenViewModel", "Error loading random ad", e)
            Log.e("HomeScreenViewModel", "Error message: ${e.message}")
            Log.e("HomeScreenViewModel", "Error stack trace: ${e.stackTraceToString()}")
        }
    }

    fun onAction(action: HomeScreenAction) {
        when (action) {
            is HomeScreenAction.OnAdClick -> {
                viewModelScope.launch {
                    state.value.currentAd?.let { ad ->
                        adService.incrementClicks(ad.id!!)
                    }
                }
            }
            is HomeScreenAction.OnSharePost -> {
                viewModelScope.launch {
                    val referencePost = postRepository.getPost(action.post.id ?: return@launch)
                    val post = Post(
                        content = action.caption,
                        userId = state.value.user?.id ?: "",
                        reference = referencePost,
                        privacy = action.privacy.name,
                        user = state.value.user
                    )
                    try {
                        postRepository.addPost(post)
                    } catch (e: Exception) {
                        Log.d("ERROR", e.toString())
                    }
                }
            }
            is HomeScreenAction.LoadMorePosts -> loadMorePosts()
            is HomeScreenAction.LoadMoreStories -> loadStories()
            is HomeScreenAction.UpdateSharePrivacy -> _state.update { it.copy(sharePrivacy = action.privacy) }
            is HomeScreenAction.UpdateShareCaption -> _state.update { it.copy(shareCaption = action.caption) }
            is HomeScreenAction.UpdateSharePost -> _state.update { it.copy(sharePost = action.post, showShareDialog = true) }
            is HomeScreenAction.DismissShareDialog -> _state.update { it.copy(showShareDialog = false) }
            is HomeScreenAction.LikePost -> likePost(action.postId)
            is HomeScreenAction.Refresh -> {
                Log.d("HomeScreenViewModel", "Refreshing home screen content")
                viewModelScope.launch {
                    reloadPosts()   // resets pagination and loads the first page
                    loadStories()   // loads current user's and following's stories
                    loadRandomAd()  // loads a random eligible ad
                }
            }
            is HomeScreenAction.OnLikesShowClick -> loadLikesUser(action.targetId)
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

    fun reloadPosts() {
        postRepository.resetPagination()
        loadPosts()
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

    private fun loadPosts() {
        viewModelScope.launch {
            val currentUser = authService.getCurrentUser()
            val userId = currentUser?.id ?: ""
            val friendMap = relationshipService.getFriends(userId)
            friendList = friendMap.keys.toList()

            _state.update {
                it.copy(
                    user = currentUser,
                    isLoading = true,
                    error = null
                )
            }

            try {
                val posts = postRepository.getNextPosts(
                    userId = userId,
                    friendList = friendList,
                    limit = 10L
                )

                val postAuthorMap = loadAuthorNames(posts)

                _state.update {
                    it.copy(
                        posts = posts,
                        postAuthorNames = postAuthorMap,
                        isLoading = false,
                        isPaginating = false,
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
        if (state.value.isPaginating || !state.value.hasMorePosts) return

        viewModelScope.launch {
            _state.update { it.copy(isPaginating = true) }

            val newPosts = postRepository.getNextPosts(
                userId = state.value.user?.id ?: "",
                friendList = friendList,
                limit = 10L
            )

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
    private fun loadStories() {
        viewModelScope.launch {
            _state.update { it.copy(isStoryLoading = true) }

            try {
                val currentUser = authService.getCurrentUser()
                val currentUserId = currentUser?.id

                if (currentUserId == null) {
                    _state.update { it.copy(isStoryLoading = false) }
                    return@launch
                }

                // 1. Lấy story người dùng hiện tại
                val myStories = storyService.getStoriesByUser(currentUserId)

                // 2. Lấy story từ following
                val followingStoriesResult = storyService.getStoriesFromFollowing()
                val followingStories = if (followingStoriesResult.isSuccess) {
                    followingStoriesResult.getOrNull() ?: emptyList()
                } else {
                    emptyList()
                }

                val allStories = myStories?.plus(followingStories)

                _state.update {
                    it.copy(
                        stories = allStories!!,
                        isStoryLoading = false
                    )
                }

            } catch (e: Exception) {
                Log.e("Story", "Failed to load stories", e)
                _state.update {
                    it.copy(
                        isStoryLoading = false
                    )
                }
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