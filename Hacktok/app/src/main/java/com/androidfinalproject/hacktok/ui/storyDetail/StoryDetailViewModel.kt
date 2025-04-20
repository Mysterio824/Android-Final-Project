package com.androidfinalproject.hacktok.ui.storydetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.Message
import com.androidfinalproject.hacktok.model.Story
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.model.UserSnapshot
import com.androidfinalproject.hacktok.repository.ChatRepository
import com.androidfinalproject.hacktok.repository.UserRepository
import com.androidfinalproject.hacktok.service.StoryService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class StoryDetailViewModel @Inject constructor(
    private val storyService: StoryService,
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository

) : ViewModel() {
    private val _state = MutableStateFlow(StoryDetailState())
    val state = _state.asStateFlow()

    private var storyProgressJob: Job? = null
    private val storyDuration = 5000L // 5 seconds per story

    private var storiesCache = listOf<Story>()
    private var currentUserId: String? = null

    init {
        viewModelScope.launch {
            // Initialize current user
            try {
                val user = userRepository.getCurrentUser()
                _state.update { it.copy(currentUser = user ?: User()) }
            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to load user: ${e.message}") }
            }
        }
    }

    fun onAction(action: StoryDetailAction) {
        when (action) {
            is StoryDetailAction.SendMessage -> sendMessage(action.message)
            is StoryDetailAction.CloseStory -> {} // Handled by the parent
            is StoryDetailAction.LoadStoryDetails -> loadStoryDetails(action.userId)
            is StoryDetailAction.NextStory -> moveToNextStory()
            is StoryDetailAction.PreviousStory -> moveToPreviousStory()
            is StoryDetailAction.PauseStory -> pauseStory()
            is StoryDetailAction.ResumeStory -> resumeStory()
            is StoryDetailAction.ReportStory -> reportStory()
            is StoryDetailAction.NavigateToUserProfile -> {} // Handled by the parent
            is StoryDetailAction.DeleteStory -> deleteStory()
            is StoryDetailAction.ViewStory -> viewStory(action.storyId)
            else -> {}
        }
    }

    fun loadStoryDetails(userId: String? = null) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                // If we have a specific user ID, get their stories
                if (userId != null) {
                    storyService.observeUserStories(userId).collectLatest { result ->
                        if (result.isSuccess) {
                            val stories = result.getOrNull() ?: emptyList()
                            storiesCache = stories.filter { it.expiresAt.after(Date()) }

                            _state.update { currentState ->
                                currentState.copy(
                                    story = storiesCache.getOrNull(currentState.currentStoryIndex),
                                    totalStories = storiesCache.size,
                                    isLoading = false
                                )
                            }

                            // View the current story
                            state.value.story?.let { it.id?.let { it1 -> viewStory(it1) } }

                            // Start the timer for story progress
                            startStoryProgressTimer()
                        } else {
                            _state.update {
                                it.copy(
                                    error = "Failed to load stories: ${result.exceptionOrNull()?.message}",
                                    isLoading = false
                                )
                            }
                        }
                    }
                } else {
                    // Get active stories from user or for everyone
                    storyService.observeActiveStories(
                        forCurrentUserOnly = false,
                        filterByPrivacy = true
                    ).collectLatest { result ->
                        if (result.isSuccess) {
                            val stories = result.getOrNull() ?: emptyList()
                            storiesCache = stories

                            _state.update { currentState ->
                                currentState.copy(
                                    story = storiesCache.getOrNull(currentState.currentStoryIndex),
                                    totalStories = storiesCache.size,
                                    isLoading = false
                                )
                            }

                            // View the current story
                            state.value.story?.let { it.id?.let { it1 -> viewStory(it1) } }

                            // Start the timer for story progress
                            startStoryProgressTimer()
                        } else {
                            _state.update {
                                it.copy(
                                    error = "Failed to load stories: ${result.exceptionOrNull()?.message}",
                                    isLoading = false
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        error = "Could not load stories: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun deleteStory() {
        _state.value.story?.let { currentStory ->
            viewModelScope.launch {
                try {
                    val result = currentStory.id?.let { storyService.deleteStory(it) }

                    if (result != null) {
                        if (result.isSuccess) {
                            // If there are more stories, move to next one, otherwise close
                            if (_state.value.totalStories > 1) {
                                moveToNextStory()
                            } else {
                                _state.update { it.copy(story = null) }
                            }
                        } else {
                            _state.update {
                                it.copy(error = "Failed to delete story: ${result.exceptionOrNull()?.message}")
                            }
                        }
                    }
                } catch (e: Exception) {
                    _state.update { it.copy(error = "Failed to delete story: ${e.message}") }
                }
            }
        }
    }

    private fun viewStory(storyId: String?) {
        viewModelScope.launch {
            try {
                val result = storyService.viewStory(storyId.toString())

                if (result.isFailure) {
                    // Log error but don't update UI - non-critical operation
                    println("Failed to mark story as viewed: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                println("Error viewing story: ${e.message}")
            }
        }
    }

    private fun sendMessage(content: String) {
        if (content.isBlank()) return
        val currentStory = _state.value.story ?: return

        viewModelScope.launch {
            try {
                val senderId = currentUserId ?: return@launch
                val receiverId = currentStory.userId ?: return@launch

                // 1. Get or create chat
                val chatId = chatRepository.getOrCreateChat(senderId, receiverId)

                // 2. Create message
                val message = Message(
                    senderId = senderId,
                    content = content,
                    createdAt = Date(),
                    isRead = false,
                    isDeleted = false
                )

                // 3. Send
                chatRepository.sendMessage(chatId, message)

                // 4. Optional: show local message
                _state.update { currentState ->
                    currentState.copy(messages = currentState.messages + message)
                }

            } catch (e: Exception) {
                _state.update { it.copy(error = "Failed to send message: ${e.message}") }
            }
        }
    }

    private fun moveToNextStory() {
        _state.update { currentState ->
            if (currentState.currentStoryIndex < currentState.totalStories - 1) {
                val newIndex = currentState.currentStoryIndex + 1
                currentState.copy(
                    currentStoryIndex = newIndex,
                    story = storiesCache.getOrNull(newIndex),
                    storyProgress = 0f
                )
            } else {
                currentState
            }
        }

        // View the new story
        state.value.story?.let { viewStory(it.id) }

        // Restart the timer
        restartStoryProgressTimer()
    }

    private fun moveToPreviousStory() {
        _state.update { currentState ->
            if (currentState.currentStoryIndex > 0) {
                val newIndex = currentState.currentStoryIndex - 1
                currentState.copy(
                    currentStoryIndex = newIndex,
                    story = storiesCache.getOrNull(newIndex),
                    storyProgress = 0f
                )
            } else {
                currentState
            }
        }

        // View the new story
        state.value.story?.let { viewStory(it.id) }

        // Restart the timer
        restartStoryProgressTimer()
    }

    private fun startStoryProgressTimer() {
        storyProgressJob?.cancel()
        storyProgressJob = viewModelScope.launch {
            val updateInterval = 100L // Update progress every 100ms
            val totalUpdates = storyDuration / updateInterval

            repeat(totalUpdates.toInt()) { step ->
                delay(updateInterval)
                val progress = (step + 1) / totalUpdates.toFloat()
                _state.update { it.copy(storyProgress = progress) }

                // Move to next story when progress completes
                if (progress >= 1f) {
                    moveToNextStory()
                }
            }
        }
    }

    private fun restartStoryProgressTimer() {
        storyProgressJob?.cancel()
        startStoryProgressTimer()
    }

    private fun pauseStory() {
        storyProgressJob?.cancel()
        _state.update { it.copy(isStoryPaused = true) }
    }

    private fun resumeStory() {
        _state.update { it.copy(isStoryPaused = false) }
        val currentProgress = _state.value.storyProgress
        val remainingTime = storyDuration * (1 - currentProgress)

        storyProgressJob?.cancel()
        storyProgressJob = viewModelScope.launch {
            val updateInterval = 100L
            val totalRemainingUpdates = remainingTime / updateInterval
            var currentStep = (totalRemainingUpdates * currentProgress).toInt()

            while (currentStep < totalRemainingUpdates) {
                delay(updateInterval)
                currentStep++
                val progress = currentStep / totalRemainingUpdates.toFloat()
                _state.update { it.copy(storyProgress = progress) }

                if (progress >= 1f) {
                    moveToNextStory()
                }
            }
        }
    }

    private fun reportStory() {
        // In a real app, implement reporting functionality
        _state.value.story?.let { currentStory ->
            // Show success message
            _state.update { it.copy(reportSuccessMessage = "Story reported successfully") }

            // Clear message after a short delay
            viewModelScope.launch {
                delay(3000)
                _state.update { it.copy(reportSuccessMessage = null) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        storyProgressJob?.cancel()
    }
}