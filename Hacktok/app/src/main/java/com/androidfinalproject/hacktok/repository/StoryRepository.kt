package com.androidfinalproject.hacktok.repository

import com.androidfinalproject.hacktok.model.Story
import com.androidfinalproject.hacktok.ui.newPost.PRIVACY
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface StoryRepository {
    suspend fun addStory(story: Story): String
    suspend fun getStory(storyId: String): Story?
    suspend fun getStoriesByUser(userId: String): List<Story>
    suspend fun getActiveStories(excludeExpired: Boolean = true): Flow<List<Story>>
    suspend fun getStoriesByPrivacy(privacy: PRIVACY): List<Story>
    suspend fun updateStory(storyId: String, updates: Map<String, Any>)
    suspend fun deleteStory(storyId: String)
    suspend fun addViewer(storyId: String, viewerId: String)
    suspend fun observeUserStories(userId: String): Flow<List<Story>>
    suspend fun observeActiveStories(userId: String? = null): Flow<Result<List<Story>>>
}