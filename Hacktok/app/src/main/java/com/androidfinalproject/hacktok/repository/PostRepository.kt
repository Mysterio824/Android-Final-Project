package com.androidfinalproject.hacktok.repository

import com.androidfinalproject.hacktok.model.Post
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface PostRepository {
    suspend fun addPost(post: Post): String
    suspend fun getPost(postId: String): Post?
    suspend fun getPostsByUser(userId: String): List<Post>
    suspend fun updatePost(postId: String, updates: Map<String, Any>)
    suspend fun updatePostContentOnly(postId: String, newContent: String, newPrivacy: String, newImageLink: String)
    suspend fun deletePost(postId: String)
    suspend fun incrementLikeCount(postId: String)
    suspend fun searchPosts(query: String): List<Post>
    suspend fun getNextPosts(userId: String, friendList: List<String>, limit: Long = 10): List<Post>
    fun resetPagination()
}