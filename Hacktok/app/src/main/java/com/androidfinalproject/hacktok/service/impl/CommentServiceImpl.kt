package com.androidfinalproject.hacktok.service.impl

import android.util.Log
import com.androidfinalproject.hacktok.model.Comment
import com.androidfinalproject.hacktok.model.UserSnapshot
import com.androidfinalproject.hacktok.model.enums.NotificationType
import com.androidfinalproject.hacktok.repository.CommentRepository
import com.androidfinalproject.hacktok.repository.PostRepository
import com.androidfinalproject.hacktok.repository.UserRepository
import com.androidfinalproject.hacktok.service.CommentService
import com.androidfinalproject.hacktok.service.FcmService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentServiceImpl @Inject constructor(
    private val commentRepository: CommentRepository,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val fcmService: FcmService,
) : CommentService {

    private val TAG = "CommentServiceImpl"
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun getCommentsForPost(postId: String): Flow<List<Comment>> {
        return commentRepository.getAll(postId)
            .catch { e ->
                Log.e(TAG, "Error in getCommentsForPost", e)
                emit(emptyList())
            }
    }
    
    override fun observeCommentsForPost(
        postId: String,
        parentCommentId: String?,
        sortAscending: Boolean
    ): Flow<Result<List<Comment>>> {
        return commentRepository.observeCommentsForPost(
            postId = postId,
            parentCommentId = parentCommentId,
            sortAscending = sortAscending
        )
        .onEach { result ->
            when {
                result.isSuccess -> {
                    val comments = result.getOrNull() ?: emptyList()
                }
                result.isFailure -> {
                    val error = result.exceptionOrNull()
                    Log.e(TAG, "Error in observeCommentsForPost flow", error)
                }
            }
        }
        .map { result ->
            // Process the result - add any additional sorting or filtering here
            result.map { comments ->
                try {
                    // First sort by like count (most likes first), then by timestamp
                    comments.sortedWith(
                        compareByDescending<Comment> { it.getLikeCount() }
                        .thenBy { if (sortAscending) it.createdAt.time else -it.createdAt.time }
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error sorting comments", e)
                    comments
                }
            }
        }
        .catch { error ->
            // This catch block handles any errors in the flow processing itself
            Log.e(TAG, "Critical error in observeCommentsForPost flow", error)
            emit(Result.failure(error))
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

            // Update post comment count
            val post = postRepository.getPost(postId)
            if (post != null) {
                postRepository.updatePost(postId, mapOf("commentCount" to post.commentCount + 1))
                
                // Send notification to post owner (if not the same user)
                serviceScope.launch {
                    fcmService.sendInteractionNotification(
                        recipientUserId = post.userId,
                        senderUserId = user.id,
                        notificationType = NotificationType.POST_COMMENT,
                        itemId = postId
                    )
                }
            }

            return Result.success(resComment)
        } catch (e: Exception) {
            Log.e(TAG, "Error adding comment", e)
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

            val result = commentRepository.add(reply)
            commentRepository.update(parentComment.id!!, parentComment.copy(
                replyCount = parentComment.replyCount + 1
            ))
            
            // Update post comment count
            val post = postRepository.getPost(parentComment.postId)
            if (post != null) {
                postRepository.updatePost(parentComment.postId, mapOf("commentCount" to post.commentCount + 1))
            }
            
            // Send notification to parent comment owner (if not the same user)
            if (parentComment.userId != user.id) {
                serviceScope.launch {
                    fcmService.sendInteractionNotification(
                        recipientUserId = parentComment.userId,
                        senderUserId = user.id,
                        notificationType = NotificationType.COMMENT_REPLY,
                        itemId = parentId
                    )
                    post!!.id?.let {
                        fcmService.sendInteractionNotification(
                            recipientUserId = post.userId,
                            senderUserId = user.id,
                            notificationType = NotificationType.POST_COMMENT,
                            itemId = it
                        )
                    }
                }
            }

            Result.success(result.getOrThrow())
        } catch (e: Exception) {
            Result.failure(e)
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
                
                // Send notification to comment owner (if not the same user)
                if (comment.userId != userId) {
                    serviceScope.launch {
                        fcmService.sendInteractionNotification(
                            recipientUserId = comment.userId,
                            senderUserId = userId,
                            notificationType = NotificationType.COMMENT_LIKE,
                            itemId = commentId
                        )
                    }
                }
                
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

    override suspend fun deleteComment(commentId: String): Result<Unit> {
        return try {
            // Verify comment exists
            val comment = commentRepository.getById(commentId).getOrNull()
                ?: return Result.failure(IllegalArgumentException("Comment not found"))
                
            // Update post comment count
            val post = postRepository.getPost(comment.postId)
            if (post != null) {
                postRepository.updatePost(comment.postId, mapOf("commentCount" to (post.commentCount - 1).coerceAtLeast(0)))
            }

            // If this is a parent comment, we should handle child comments
            if (comment.replyCount > 0) {
                //TODO
            }

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