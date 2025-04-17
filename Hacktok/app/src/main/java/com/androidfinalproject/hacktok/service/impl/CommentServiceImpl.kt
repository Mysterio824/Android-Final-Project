package com.androidfinalproject.hacktok.service.impl

import android.net.http.NetworkException
import android.util.Log
import com.androidfinalproject.hacktok.model.Comment
import com.androidfinalproject.hacktok.model.UserSnapshot
import com.androidfinalproject.hacktok.repository.CommentRepository
import com.androidfinalproject.hacktok.repository.UserRepository
import com.androidfinalproject.hacktok.service.CommentService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentServiceImpl @Inject constructor(
    private val commentRepository: CommentRepository,
    private val userRepository: UserRepository
) : CommentService {

    override fun getCommentsForPost(postId: String): Flow<List<Comment>> {
        return commentRepository.getAll(postId)
            .catch { e ->
                e.printStackTrace()
                emit(emptyList())
            }
    }

    override suspend fun addComment(content: String, postId: String): Result<Comment> {
        return try {

            if (content.isBlank()) {
                return Result.failure(IllegalArgumentException("Comment content cannot be empty"))
            }

            val user = userRepository.getCurrentUser()
                ?: return Result.failure(IllegalStateException("User not logged in"))

            val userSnapshot = UserSnapshot(
                username = user.username!!,
                profileImage = user.profileImage
            )

            val newComment = Comment(
                content = content,
                userId = user.id!!,
                postId = postId,
                userSnapshot = userSnapshot
            )

            val resComment = commentRepository.add(newComment).getOrNull()
                ?: return Result.failure(IllegalArgumentException("Can't add comment"))

            return Result.success(resComment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun replyComment(content: String, parentId: String): Result<Comment> {
        return try {
            if (content.isBlank()) {
                return Result.failure(IllegalArgumentException("Reply content cannot be empty"))
            }

            val user = userRepository.getCurrentUser()
                ?: return Result.failure(IllegalStateException("User not logged in"))

            val parentCommentResult = commentRepository.getById(parentId)
            val parentComment = parentCommentResult.getOrElse {
                return Result.failure(IllegalArgumentException("Parent comment not found"))
            }

            val userSnapshot = UserSnapshot(
                username = user.username!!,
                profileImage = user.profileImage
            )

            val reply = Comment(
                content = content,
                userId = user.id!!,
                postId = parentComment.postId,
                userSnapshot = userSnapshot,
                parentCommentId = parentComment.id
            )

            commentRepository.add(reply)
            commentRepository.update(parentComment.id!!, parentComment.copy(
                replyCount = parentComment.replyCount + 1
            ))

            Result.success(reply)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun likeComment(commentId: String, userId: String): Result<Unit> {
        return try {
            val comment = commentRepository.getById(commentId).getOrNull()
                ?: return Result.failure(IllegalArgumentException("Comment not found"))

            val updatedLikedUserIds = comment.likedUserIds.toMutableList()
            if (!updatedLikedUserIds.contains(userId)) {
                updatedLikedUserIds.add(userId)
            }

            val updatedComment = comment.copy(
                likedUserIds = updatedLikedUserIds
            )

            commentRepository.update(commentId, updatedComment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unlikeComment(commentId: String, userId: String): Result<Unit> {
        return try {
            val comment = commentRepository.getById(commentId).getOrNull()
                ?: return Result.failure(IllegalArgumentException("Comment not found"))

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

    override suspend fun deleteComment(commentId: String): Result<Unit> {
        return try {
            // Verify comment exists
            commentRepository.getById(commentId).getOrNull()
                ?: return Result.failure(IllegalArgumentException("Comment not found"))

            // Delete the comment
            commentRepository.delete(commentId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserSnapshots(
        commentIds: List<String>,
        newSnapshots: Map<String, UserSnapshot>
    ): Result<Unit> {
        return try {
            for (commentId in commentIds) {
                val comment = commentRepository.getById(commentId).getOrNull() ?: continue
                val userSnapshot = newSnapshots[comment.userId] ?: continue

                val updatedComment = comment.copy(
                    userSnapshot = userSnapshot
                )
                commentRepository.update(commentId, updatedComment)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}