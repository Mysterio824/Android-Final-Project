package com.androidfinalproject.hacktok.service.impl

import android.util.Log
import com.androidfinalproject.hacktok.model.Post
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
    override suspend fun likePost(postId: String): Post? {
        try {
            val post = postRepository.getPost(postId)
                ?: return null

            val user = userRepository.getCurrentUser()
                ?: return null
            val userId = user.id

            val updatedLikedUserIds = post.likedUserIds.toMutableList()
            // Only add like and send notification if the user hasn't liked yet
            if (!updatedLikedUserIds.contains(userId)) {
                updatedLikedUserIds.add(userId!!)

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

            val updatedLikedUserIds = post.likedUserIds.toMutableList()
            updatedLikedUserIds.remove(user.id)

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

    override suspend fun likeComment(commentId: String): Result<Unit> {
        return try {
            val comment = commentRepository.getById(commentId).getOrNull()
                ?: return Result.failure(IllegalArgumentException("Comment not found"))

            val user = userRepository.getCurrentUser()
                ?: return Result.failure(IllegalArgumentException("User not found"))
            val userId = user.id

            val updatedLikedUserIds = comment.likedUserIds.toMutableList()
            // Only add like and send notification if the user hasn't liked yet
            if (!updatedLikedUserIds.contains(userId)) {
                updatedLikedUserIds.add(userId!!)

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
                Result.success(Unit) // User already liked this comment
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
            val userId = user.id

            val updatedLikedUserIds = comment.likedUserIds.toMutableList()
            updatedLikedUserIds.remove(userId)

            val updatedComment = comment.copy(
                likedUserIds = updatedLikedUserIds
            )

            commentRepository.update(commentId, updatedComment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}