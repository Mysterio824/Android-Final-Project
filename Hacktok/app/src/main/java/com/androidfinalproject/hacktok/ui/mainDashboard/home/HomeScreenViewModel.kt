package com.androidfinalproject.hacktok.ui.mainDashboard.home

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
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
import android.app.Application
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.utils.MessageEncryptionUtil
import com.androidfinalproject.hacktok.service.ApiService
import com.androidfinalproject.hacktok.model.SavedPost
import com.androidfinalproject.hacktok.repository.SavedPostRepository

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val authService: AuthService,
    private val postRepository: PostRepository,
    private val reportService: ReportService,
    private val relationshipService: RelationshipService,
    private val likeService: LikeService,
    private val userRepository: UserRepository,
    private val storyService: StoryService,
    private val adService: AdService,
    private val apiService: ApiService,
    private val savedPostRepository: SavedPostRepository,
    application: Application
) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(HomeScreenState())
    val state: StateFlow<HomeScreenState> = _state.asStateFlow()
    private lateinit var friendList: List<String>

    init {
        _state.update { HomeScreenState() }

        viewModelScope.launch {
            reloadPosts()   // resets pagination and loads the first page
            loadStories()   // loads current user's and following's stories
            loadRandomAd()  // loads a random eligible ad
            initializeEncryption()
            loadSavedPosts() // load saved posts
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
                        if (ad.url.isNotEmpty()) {
                            try {
                                val url = ad.url.trim()
                                // Validate URL format
                                val formattedUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
                                    "https://$url"
                                } else {
                                    url
                                }
                                
                                // Create a simple intent to open in default browser
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(formattedUrl))
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                
                                // Start the activity
                                getApplication<Application>().startActivity(intent)
                            } catch (e: Exception) {
                                Log.e("HomeScreenViewModel", "Error opening URL: ${e.message}")
                            }
                        }
                    }
                }
            }
            is HomeScreenAction.OnAdInterested -> {
                viewModelScope.launch {
                    val currentUser = state.value.user
                    val currentAd = state.value.currentAd
                    if (currentUser != null && currentAd != null) {
                        val updatedAd = adService.addInterestedUser(currentAd.id!!, currentUser.id!!)
                        if (updatedAd != null) {
                            _state.update { it.copy(currentAd = updatedAd) }
                        }
                    }
                }
            }
            is HomeScreenAction.OnAdUninterested -> {
                viewModelScope.launch {
                    val currentUser = state.value.user
                    val currentAd = state.value.currentAd
                    if (currentUser != null && currentAd != null) {
                        val updatedAd = adService.removeInterestedUser(currentAd.id!!, currentUser.id!!)
                        if (updatedAd != null) {
                            _state.update { it.copy(currentAd = updatedAd) }
                        }
                    }
                }
            }
            is HomeScreenAction.OnSharePost -> {
                viewModelScope.launch {
                    val post = Post(
                        content = action.caption,
                        userId = state.value.user?.id ?: "",
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
            is HomeScreenAction.DeletePost -> deletePost(action.postId)
            is HomeScreenAction.LoadMorePosts -> loadMorePosts()
            is HomeScreenAction.LoadMoreStories -> loadStories()
            is HomeScreenAction.UpdateSharePrivacy -> _state.update { it.copy(sharePrivacy = action.privacy) }
            is HomeScreenAction.UpdateShareCaption -> _state.update { it.copy(shareCaption = action.caption) }
            is HomeScreenAction.UpdateSharePost -> _state.update { it.copy(sharePost = action.post, showShareDialog = true) }
            is HomeScreenAction.DismissShareDialog -> _state.update { it.copy(showShareDialog = false) }
            is HomeScreenAction.LikePost -> likePost(action.postId, action.emoji)
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
            is HomeScreenAction.SubmitReport -> submitReport(
                reportedItemId = action.reportedItemId,
                reportType = action.reportType,
                reportCause = action.reportCause
            )
            is HomeScreenAction.OnSavePost -> savePost(action.postId)
            is HomeScreenAction.OnDeleteSavedPost -> deleteSavedPost(action.postId)
            else -> {}
        }
    }

    private fun loadSavedPosts() {
        viewModelScope.launch {
            try {
                val currentUser = state.value.user
                if (currentUser != null) {
                    val userId = currentUser.id ?: return@launch
                    val savedPosts = savedPostRepository.getSavedPostsByUser(userId)
                        .map { it.postId }
                    _state.update { it.copy(savedPosts = savedPosts) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to load saved posts: ${e.message}") }
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

    private fun deletePost(postId: String) {
        viewModelScope.launch {
            try {
                postRepository.deletePost(postId)
            } catch(e: Exception) {
                _state.update {
                    Log.e("HomeScreenViewmodel", e.message.toString())
                    it.copy(error = e.message)
                }
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

    private suspend fun loadAuthorNames(posts: List<Post>): Map<String, String> {
        val distinctUserIds = posts.map { it.userId }.distinct()
        val users = distinctUserIds.mapNotNull { userRepository.getUserById(it) }

        return posts.associate { post ->
            val name = users.find { it.id == post.userId }?.fullName ?: "Unknown"
            post.id!! to name
        }
    }

    private fun reloadPosts() {
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

                // ✅ Build postId -> fullName map
                val postAuthorNames = loadAuthorNames(posts)

                // ✅ New: postId -> User map
                val postUsers = mutableMapOf<String, User>()

                // ✅ Reference posts and users
                val referencePosts = mutableMapOf<String, Post>()
                val referenceUsers = mutableMapOf<String, User>()

                posts.forEach { post ->
                    // Load the User of the post
                    val author = userRepository.getUserById(post.userId)
                    if (author != null && post.id != null) {
                        postUsers[post.id] = author
                    }

                    // If it's a shared (reference) post
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

                _state.update {
                    it.copy(
                        posts = posts,
                        postAuthorNames = postAuthorNames,
                        postUsers = postUsers,                 // ✅ New line added here
                        referencePosts = referencePosts,
                        referenceUsers = referenceUsers,
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

            try {
                val newPosts = postRepository.getNextPosts(
                    userId = state.value.user?.id ?: "",
                    friendList = friendList,
                    limit = 10L
                )

                val newAuthorMap = loadAuthorNames(newPosts)
                val newPostUsers = mutableMapOf<String, User>()

                // 🛠️ Fetch each post's author
                for (post in newPosts) {
                    val author = userRepository.getUserById(post.userId)
                    if (author != null && post.id != null) {
                        newPostUsers[post.id] = author
                    }
                }

                _state.update {
                    val allPosts = (it.posts + newPosts).distinctBy { post -> post.id }
                    val allAuthors = it.postAuthorNames + newAuthorMap
                    val allPostUsers = it.postUsers + newPostUsers

                    it.copy(
                        posts = allPosts,
                        postAuthorNames = allAuthors,
                        postUsers = allPostUsers,          // ✅ Updated here
                        isPaginating = false,
                        hasMorePosts = newPosts.size == 10
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isPaginating = false,
                        error = "Failed to load more posts: ${e.message}"
                    )
                }
            }
        }
    }

    private fun likePost(postId: String, emoji: String) {
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

    private fun initializeEncryption() {
        viewModelScope.launch {
            try {
                // Khởi tạo MessageEncryptionUtil với context
                MessageEncryptionUtil.initialize(getApplication())

                // Lấy key từ server
                val key = apiService.getEncryptionKey()
                MessageEncryptionUtil.initializeKey(key)
            } catch (e: Exception) {
                Log.e("HomeScreenViewModel", "Failed to initialize encryption", e)
            }
        }
    }
}