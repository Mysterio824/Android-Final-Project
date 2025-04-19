package com.androidfinalproject.hacktok.ui.newStory

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidfinalproject.hacktok.model.Media
import com.androidfinalproject.hacktok.model.Story
import com.androidfinalproject.hacktok.ui.newPost.PRIVACY
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class NewStoryViewModel : ViewModel() {
    private val _state = MutableStateFlow(NewStoryState())
    val state: StateFlow<NewStoryState> = _state

    // Current user's ID - replace with actual user data
    private val currentUserId = "user1"
    private val currentUserName = "user1"

    // Create a global list to store stories
    companion object {
        val globalStories = mutableListOf<Story>()
    }

    fun onAction(action: NewStoryAction) {
        when (action) {
            is NewStoryAction.GoToImageEditor -> {
                _state.update { currentState ->
                    currentState.copy(
                        selectedImageUri = action.imageUri,
                        storyType = "image"
                    )
                }
            }
            is NewStoryAction.NewTextStory -> {
                _state.update { currentState ->
                    currentState.copy(
                        storyType = "text"
                    )
                }
            }
            is NewStoryAction.UpdatePrivacy -> {
                _state.update { currentState ->
                    currentState.copy(privacySetting = action.privacy)
                }
            }
            is NewStoryAction.UpdateText -> {
                _state.update { currentState ->
                    currentState.copy(storyText = action.text)
                }
            }
            is NewStoryAction.CreateImageStory -> {
                createImageStory(action.imageUri, action.privacy)
            }
            is NewStoryAction.CreateTextStory -> {
                createTextStory(action.text, action.privacy)
            }
            else -> {}
        }
    }

    private fun createImageStory(imageUri: Uri?, privacy: PRIVACY) {
        if (imageUri == null) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val now = Date()
                val expiresAt = Date(now.time + 24 * 60 * 60 * 1000) // 24 hours later

                val story = Story(
                    id = UUID.randomUUID().toString(),
                    userId = currentUserId,
                    userName = currentUserName,
                    userAvatar = null,
                    media = Media(
                        type = "image",
                        url = imageUri.toString(),
                        thumbnailUrl = imageUri.toString() // For real app, generate thumbnail
                    ),
                    createdAt = now,
                    expiresAt = expiresAt,
                    viewerIds = emptyList(),
                    privacy = privacy
                )

                // In a real app, upload story to server
                // For this example, add to global list
                globalStories.add(story)

                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        isStoryCreated = true,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to create story"
                    )
                }
            }
        }
    }

    private fun createTextStory(text: String, privacy: PRIVACY) {
        if (text.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val now = Date()
                val expiresAt = Date(now.time + 24 * 60 * 60 * 1000) // 24 hours later

                val story = Story(
                    id = UUID.randomUUID().toString(),
                    userId = currentUserId,
                    userName = currentUserName,
                    userAvatar = null,
                    media = Media(
                        type = "text",
                        url = text, // For text stories, store text content
                        thumbnailUrl = "" // No thumbnail for text
                    ),
                    createdAt = now,
                    expiresAt = expiresAt,
                    viewerIds = emptyList(),
                    privacy = privacy
                )

                // In a real app, upload story to server
                globalStories.add(story)

                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        isStoryCreated = true,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _state.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to create story"
                    )
                }
            }
        }
    }

    fun resetState() {
        _state.update { NewStoryState() }
    }
}