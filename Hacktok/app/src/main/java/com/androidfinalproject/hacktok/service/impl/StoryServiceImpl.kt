package com.androidfinalproject.hacktok.service.impl

import android.util.Log
import com.androidfinalproject.hacktok.model.Media
import com.androidfinalproject.hacktok.model.Story
import com.androidfinalproject.hacktok.model.UserSnapshot
//import com.androidfinalproject.hacktok.model.enums.NotificationType
import com.androidfinalproject.hacktok.repository.StoryRepository
import com.androidfinalproject.hacktok.repository.UserRepository
//import com.androidfinalproject.hacktok.service.FcmService
import com.androidfinalproject.hacktok.service.StoryService
import com.androidfinalproject.hacktok.ui.newPost.PRIVACY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoryServiceImpl @Inject constructor(
    private val storyRepository: StoryRepository,
    private val userRepository: UserRepository,
//    private val fcmService: FcmService
) : StoryService {
    private val TAG = "StoryServiceImpl"
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override suspend fun createStory(
        media: Media,
        privacy: PRIVACY,
        expiresAt: Date?
    ): Result<Story> {
        return try {
            val user = userRepository.getCurrentUser()
                ?: return Result.failure(IllegalStateException("User not logged in"))

            // Set expiration date (default: 24 hours from now)
            val expiration = expiresAt ?: Calendar.getInstance().apply {
                add(Calendar.HOUR, 24)
            }.time

            val story = Story(
                userId = user.id!!,
                userName = user.username ?: "",
                userAvatar = user.profileImage,
                media = media,
                createdAt = Date(),
                expiresAt = expiration,
                viewerIds = emptyList(),
                privacy = privacy
            )

            val storyId = storyRepository.addStory(story)
            val createdStory = storyRepository.getStory(storyId)
                ?: return Result.failure(IllegalStateException("Failed to create story"))

            // Notify followers about new story
//            serviceScope.launch {
//                notifyFollowersAboutNewStory(user.id)
//            }

            Result.success(createdStory)
        } catch (e: Exception) {
            Log.e(TAG, "Error creating story", e)
            Result.failure(e)
        }
    }

    override suspend fun getStory(storyId: String): Result<Story> {
        return try {
            val story = storyRepository.getStory(storyId)
                ?: return Result.failure(IllegalStateException("Story not found"))

            Result.success(story)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting story", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteStory(storyId: String): Result<Unit> {
        return try {
            val story = storyRepository.getStory(storyId)
                ?: return Result.failure(IllegalStateException("Story not found"))

            val currentUser = userRepository.getCurrentUser()
                ?: return Result.failure(IllegalStateException("User not logged in"))

            // Ensure only story owner can delete
            if (story.userId != currentUser.id) {
                return Result.failure(IllegalStateException("Only the story owner can delete this story"))
            }

            storyRepository.deleteStory(storyId)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting story", e)
            Result.failure(e)
        }
    }

    override suspend fun viewStory(storyId: String): Result<Unit> {
        return try {
            val currentUser = userRepository.getCurrentUser()
                ?: return Result.failure(IllegalStateException("User not logged in"))

            val story = storyRepository.getStory(storyId)
                ?: return Result.failure(IllegalStateException("Story not found"))

            // Only add viewer if not already viewed
            if (!story.viewerIds.contains(currentUser.id)) {
                storyRepository.addViewer(storyId, currentUser.id!!)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error viewing story", e)
            Result.failure(e)
        }
    }

    override suspend fun observeUserStories(userId: String): Flow<Result<List<Story>>> {
        return try {
            storyRepository.observeUserStories(userId)
                .map { Result.success(it) }
                .catch { e ->
                    Log.e(TAG, "Error observing user stories", e)
                    emit(Result.failure(e))
                }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getStoriesByUser(userId: String): List<Story> {
        return storyRepository.getStoriesByUser(userId)
            .filter { it.expiresAt.after(Date()) }
    }


    override suspend fun observeActiveStories(
        forCurrentUserOnly: Boolean,
        filterByPrivacy: Boolean
    ): Flow<Result<List<Story>>> {
        return try {
            val currentUser = userRepository.getCurrentUser()

            // If we need to filter for current user only and we have a user
            val userId = if (forCurrentUserOnly && currentUser != null) {
                currentUser.id
            } else {
                null
            }

            storyRepository.observeActiveStories(userId)
                .map { result ->
                    result.map { stories ->
                        if (filterByPrivacy && currentUser != null) {
                            // Filter stories based on privacy settings
                            stories.filter { story ->
                                when (story.privacy) {
                                    PRIVACY.PUBLIC -> true
                                    PRIVACY.PRIVATE -> story.userId == currentUser.id
                                    PRIVACY.FRIENDS -> {
                                        // Logic to check if user is a friend
                                        // For simplicity, assume friends based on userId
                                        currentUser.friends?.contains(story.userId) ?: false ||
                                                story.userId == currentUser.id
                                    }
                                    else -> false
                                }
                            }
                        } else {
                            stories
                        }
                    }
                }
                .catch { e ->
                    Log.e(TAG, "Error observing active stories", e)
                    emit(Result.failure(e))
                }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getUnviewedStoriesCount(): Result<Int> {
        return try {
            val currentUser = userRepository.getCurrentUser()
                ?: return Result.failure(IllegalStateException("User not logged in"))

            var unviewedCount = 0

            storyRepository.getActiveStories()
                .catch { emit(emptyList()) }
                .collect { stories ->
                    // Count stories that current user hasn't viewed yet
                    unviewedCount = stories.count { story ->
                        !story.viewerIds.contains(currentUser.id) &&
                                story.userId != currentUser.id &&  // Don't count own stories
                                (story.privacy == PRIVACY.PUBLIC || // Public stories
                                        (story.privacy == PRIVACY.FRIENDS &&
                                                (currentUser.friends?.contains(story.userId) ?: false)) // Friend stories if user is a friend
                                        )
                    }
                }

            Result.success(unviewedCount)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting unviewed stories count", e)
            Result.failure(e)
        }
    }


    override suspend fun getStoriesFromFollowing(): Result<List<Story>> {
        return try {
            val currentUser = userRepository.getCurrentUser()
                ?: return Result.failure(IllegalStateException("User not logged in"))

            // Get users that current user is following
            val followingIds = currentUser.following ?: emptyList()

            if (followingIds.isEmpty()) {
                return Result.success(emptyList())
            }

            // Get active stories from followed users
            val stories = mutableListOf<Story>()

            for (userId in followingIds) {
                val userStories = storyRepository.getStoriesByUser(userId)
                    .filter { it.expiresAt.after(Date()) } // Only active stories
                    .filter { story ->
                        // Filter based on privacy settings
                        story.privacy == PRIVACY.PUBLIC ||
                                (story.privacy == PRIVACY.FRIENDS &&
                                        (currentUser.friends?.contains(story.userId) ?: false))
                    }

                stories.addAll(userStories)
            }

            // Sort by created time (newest first)
            val sortedStories = stories.sortedByDescending { it.createdAt }

            Result.success(sortedStories)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting stories from following", e)
            Result.failure(e)
        }
    }

//    private suspend fun notifyFollowersAboutNewStory(userId: String) {
//        try {
//            // Get user's followers
//            val user = userRepository.getUserById(userId)
//            val followers = user?.followers ?: return
//
//            // Notify each follower about new story
//            for (followerId in followers) {
//                fcmService.sendInteractionNotification(
//                    recipientUserId = followerId,
//                    senderUserId = userId,
//                    notificationType = NotificationType.NEW_STORY,
//                    itemId = userId // Using userId as the item ID for stories
//                )
//            }
//        } catch (e: Exception) {
//            Log.e(TAG, "Error notifying followers about new story", e)
//        }
//    }
}