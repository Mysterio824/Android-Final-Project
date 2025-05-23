package com.androidfinalproject.hacktok.ui.storydetail

import com.androidfinalproject.hacktok.model.Message
import com.androidfinalproject.hacktok.model.Story
import com.androidfinalproject.hacktok.model.User

data class StoryDetailState(
    val currentUser: User = User(),
    val story: Story? = null,
    val currentStoryIndex: Int = 0,
    val totalStories: Int = 0,
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isStoryPaused: Boolean = false,
    val storyProgress: Float = 0f,
    val reportSuccessMessage: String? = null,
    val successMessage: String? = null
)