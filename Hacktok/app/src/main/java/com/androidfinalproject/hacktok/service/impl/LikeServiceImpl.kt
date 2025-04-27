package com.androidfinalproject.hacktok.service.impl

import com.androidfinalproject.hacktok.model.FullReaction
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.Reaction
import com.androidfinalproject.hacktok.model.enums.NotificationType
import com.androidfinalproject.hacktok.repository.CommentRepository
import com.androidfinalproject.hacktok.repository.PostRepository
import com.androidfinalproject.hacktok.repository.UserRepository
import com.androidfinalproject.hacktok.service.LikeService
import com.androidfinalproject.hacktok.service.NotificationService
import javax.inject.Inject

class LikeServiceImpl @Inject constructor(
    private val commentRepository: CommentRepository,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val notificationService: NotificationService
) : LikeService{
    override suspend fun likePost(postId: String, emoji: String): Post? {
        try {
            val post = postRepository.getPost(postId)
                ?: return null

            val user = userRepository.getCurrentUser()
                ?: return null
            val userId = user.id ?: return null

            val updatedLikedUserIds = post.likedUserIds.toMutableList()
            if (!updatedLikedUserIds.any { it.userId == userId && it.emoji == emoji }) {
                updatedLikedUserIds.removeAll { it.userId == userId }
                updatedLikedUserIds.add(Reaction(userId, emoji))

                val updates = mapOf("likedUserIds" to updatedLikedUserIds)

                postRepository.updatePost(postId, updates)

                notificationService.createNotification(
                    recipientUserId = post.userId,
                    type = NotificationType.POST_LIKE,
                    senderId = userId,
                    relatedItemId = post.id,
                )

                return post.copy(
                    likedUserIds = updatedLikedUserIds
                )
            } else {
                return null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun unlikePost(postId: String): Post? {
        try {
            val post = postRepository.getPost(postId)
                ?: return null

            val user = userRepository.getCurrentUser()
                ?: return null
            val userId = user.id ?: return null

            val updatedLikedUserIds = post.likedUserIds.toMutableList()
            updatedLikedUserIds.removeAll { it.userId == userId }

            val updates = mapOf("likedUserIds" to updatedLikedUserIds)

            postRepository.updatePost(postId, updates)
            return post.copy(
                likedUserIds = updatedLikedUserIds
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun likeComment(commentId: String, emoji: String): Result<Unit> {
        return try {
            val comment = commentRepository.getById(commentId).getOrNull()
                ?: return Result.failure(IllegalArgumentException("Comment not found"))

            val user = userRepository.getCurrentUser()
                ?: return Result.failure(IllegalArgumentException("User not found"))
            val userId = user.id ?: return Result.failure(IllegalArgumentException("User ID is null"))

            val updatedLikedUserIds = comment.likedUserIds.toMutableList()
            if (!updatedLikedUserIds.any { it.userId == userId && it.emoji == emoji }) {
                updatedLikedUserIds.removeAll { it.userId == userId }
                updatedLikedUserIds.add(Reaction(userId, emoji))

                val updatedComment = comment.copy(
                    likedUserIds = updatedLikedUserIds
                )

                commentRepository.update(commentId, updatedComment)

                notificationService.createNotification(
                    recipientUserId = comment.userId,
                    type = NotificationType.COMMENT_LIKE,
                    senderId = userId,
                    relatedItemId = comment.id,
                )

                Result.success(Unit)
            } else {
                Result.success(Unit) // User already liked this comment with this emoji
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unlikeComment(commentId: String): Result<Unit> {
        return try {
            val comment = commentRepository.getById(commentId).getOrNull()
                ?: return Result.failure(IllegalArgumentException("Comment not found"))

            val user = userRepository.getCurrentUser()
                ?: return Result.failure(IllegalArgumentException("User not found"))
            val userId = user.id ?: return Result.failure(IllegalArgumentException("User ID is null"))

            val updatedLikedUserIds = comment.likedUserIds.toMutableList()
            updatedLikedUserIds.removeAll { it.userId == userId }

            val updatedComment = comment.copy(
                likedUserIds = updatedLikedUserIds
            )

            commentRepository.update(commentId, updatedComment)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCommentLike(commentId: String): List<FullReaction> {
        val comment = commentRepository.getById(commentId).getOrNull()
            ?: return emptyList()

        val result = mutableListOf<FullReaction>()
        for (reaction in comment.likedUserIds) {
            val user = userRepository.getUserById(reaction.userId) ?: continue
            result.add(FullReaction(user, reaction.emoji))
        }
        return result
    }

    override suspend fun getPostLike(postId: String): List<FullReaction> {
        val post = postRepository.getPost(postId)
            ?: return emptyList()

        val result = mutableListOf<FullReaction>()
        for (reaction in post.likedUserIds) {
            val user = userRepository.getUserById(reaction.userId) ?: continue
            result.add(FullReaction(user, reaction.emoji))
        }
        return result
    }
}