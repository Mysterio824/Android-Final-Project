package com.androidfinalproject.hacktok.service

import com.androidfinalproject.hacktok.model.Media
import com.androidfinalproject.hacktok.model.Story
import com.androidfinalproject.hacktok.ui.newPost.PRIVACY
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface StoryService {
    suspend fun createStory(
        media: Media,
        privacy: PRIVACY = PRIVACY.PUBLIC,
        expiresAt: Date? = null
    ): Result<Story>

    suspend fun getStory(storyId: String): Result<Story>

    suspend fun deleteStory(storyId: String): Result<Unit>

    suspend fun viewStory(storyId: String): Result<Unit>

    suspend fun observeUserStories(userId: String): Flow<Result<List<Story>>>

    suspend fun observeActiveStories(
        forCurrentUserOnly: Boolean = false,
        filterByPrivacy: Boolean = true
    ): Flow<Result<List<Story>>>

    suspend fun getUnviewedStoriesCount(): Result<Int>

    suspend fun getStoriesFromFollowing(): Result<List<Story>>
}