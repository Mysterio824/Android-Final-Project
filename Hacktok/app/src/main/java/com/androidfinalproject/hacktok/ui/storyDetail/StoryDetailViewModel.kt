package com.androidfinalproject.hacktok.ui.storydetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.Media
import com.androidfinalproject.hacktok.model.Message
import com.androidfinalproject.hacktok.model.Story
import com.androidfinalproject.hacktok.ui.newStory.NewStoryViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class StoryDetailViewModel : ViewModel() {
    private val _state = MutableStateFlow(StoryDetailState())
    val state = _state.asStateFlow()

    private var storyProgressJob: Job? = null
    private val storyDuration = 5000L // 5 seconds per story

    fun onAction(action: StoryDetailAction) {
        when (action) {
            is StoryDetailAction.SendMessage -> sendMessage(action.message)
            is StoryDetailAction.CloseStory -> {} // Handled by the parent
            is StoryDetailAction.LoadStoryDetails -> loadStoryDetails()
            is StoryDetailAction.NextStory -> moveToNextStory()
            is StoryDetailAction.PreviousStory -> moveToPreviousStory()
            is StoryDetailAction.PauseStory -> pauseStory()
            is StoryDetailAction.ResumeStory -> resumeStory()
            is StoryDetailAction.ReportStory -> reportStory()
            is StoryDetailAction.NavigateToUserProfile -> {} // Handled by the parent
            is StoryDetailAction.DeleteStory -> deleteStory()
            is StoryDetailAction.ViewStory -> viewStory()
        }
    }

    fun loadStoryDetails() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                // Get all stories including ones created by the user
                val allStories = NewStoryViewModel.globalStories + createMockStories()
                val activeStories = allStories.filter { story ->
                    story.expiresAt.after(Date()) // Only show non-expired stories
                }

                _state.update { currentState ->
                    currentState.copy(
                        story = activeStories.getOrNull(currentState.currentStoryIndex),
                        totalStories = activeStories.size,
                        isLoading = false
                    )
                }

                startStoryProgressTimer()
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        error = "Could not load story: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun deleteStory() {
        _state.value.story?.let { currentStory ->
            if (currentStory.userId == _state.value.currentUser.id) {
                viewModelScope.launch {
                    try {
                        // Remove from global stories
                        NewStoryViewModel.globalStories.removeIf { it.id == currentStory.id }

                        // If there are more stories, move to next one, otherwise close
                        if (_state.value.totalStories > 1) {
                            moveToNextStory()
                        } else {
                            _state.update { it.copy(story = null) }
                        }
                    } catch (e: Exception) {
                        _state.update { it.copy(error = "Failed to delete story: ${e.message}") }
                    }
                }
            }
        }
    }

    private fun viewStory() {
        _state.value.story?.let { currentStory ->
            val currentUserId = _state.value.currentUser.id ?: return
            if (!currentStory.viewerIds.contains(currentUserId)) {
                viewModelScope.launch {
                    // In a real app, update story view count on server
                    val updatedViewerIds = currentStory.viewerIds + currentUserId
                    val updatedStory = currentStory.copy(viewerIds = updatedViewerIds)

                    // Update in global stories list
                    val index = NewStoryViewModel.globalStories.indexOfFirst { it.id == currentStory.id }
                    if (index != -1) {
                        NewStoryViewModel.globalStories[index] = updatedStory
                    }

                    _state.update { it.copy(story = updatedStory) }
                }
            }
        }
    }

    private fun createMockStories(): List<Story> {
        val now = Date()
        val expiresAt = Date(now.time + 24 * 60 * 60 * 1000) // 24 hours later

        return listOf(
            Story(
                id = "1",
                userId = "user2",
                userName = "user2",
                userAvatar = null,
                media = Media(
                    type = "image",
                    url = "https://example.com/story1.jpg",
                    thumbnailUrl = "https://example.com/story1_thumb.jpg"
                ),
                createdAt = Date(System.currentTimeMillis() - 3600000),
                expiresAt = expiresAt,
                viewerIds = listOf("user3", "user4")
            ),
            Story(
                id = "2",
                userId = "user2",
                userName = "user2",
                userAvatar = null,
                media = Media(
                    type = "image",
                    url = "https://example.com/story2.jpg",
                    thumbnailUrl = "https://example.com/story2_thumb.jpg"
                ),
                createdAt = Date(System.currentTimeMillis() - 1800000),
                expiresAt = expiresAt,
                viewerIds = listOf("user1", "user3")
            ),
            Story(
                id = "3",
                userId = "user3",
                userName = "user3",
                userAvatar = "https://example.com/avatar3.jpg",
                media = Media(
                    type = "video",
                    url = "https://example.com/story3.mp4",
                    thumbnailUrl = "https://example.com/story3_thumb.jpg"
                ),
                createdAt = Date(System.currentTimeMillis() - 900000),
                expiresAt = expiresAt,
                viewerIds = listOf("user1", "user2")
            )
        )
    }

    private fun sendMessage(content: String) {
        if (content.isBlank()) return

        val newMessage = Message(
            id = UUID.randomUUID().toString(),
            senderId = _state.value.currentUser.id ?: "unknown",
            content = content,
            createdAt = Date(),
            isRead = false,
            media = null,
            isDeleted = false,
            replyTo = null
        )

        viewModelScope.launch {
            // In a real app, send message to server

            // Update state with new message
            _state.update { currentState ->
                currentState.copy(
                    messages = currentState.messages + newMessage
                )
            }
        }
    }

    private fun moveToNextStory() {
        _state.update { currentState ->
            if (currentState.currentStoryIndex < currentState.totalStories - 1) {
                val newIndex = currentState.currentStoryIndex + 1
                val stories = NewStoryViewModel.globalStories + createMockStories()
                currentState.copy(
                    currentStoryIndex = newIndex,
                    story = stories.getOrNull(newIndex),
                    storyProgress = 0f
                )
            } else {
                currentState
            }
        }

        restartStoryProgressTimer()
    }

    private fun moveToPreviousStory() {
        _state.update { currentState ->
            if (currentState.currentStoryIndex > 0) {
                val newIndex = currentState.currentStoryIndex - 1
                val stories = NewStoryViewModel.globalStories + createMockStories()
                currentState.copy(
                    currentStoryIndex = newIndex,
                    story = stories.getOrNull(newIndex),
                    storyProgress = 0f
                )
            } else {
                currentState
            }
        }

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
        // In a real app, report story to server
        println("Reporting story: ${_state.value.story?.id}")
    }

    override fun onCleared() {
        super.onCleared()
        storyProgressJob?.cancel()
    }
}