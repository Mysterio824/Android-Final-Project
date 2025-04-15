package com.androidfinalproject.hacktok.repository

import android.util.Log
import com.androidfinalproject.hacktok.model.Post
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PostRepository {
    private val TAG = "PostRepository"
    private val postsCollection = firestore.collection("posts")

    override suspend fun addPost(post: Post): String {
        val documentRef = postsCollection.add(post).await()
        postsCollection.document(documentRef.id).update("id", documentRef.id).await()
        return documentRef.id
    }

    override suspend fun getPost(postId: String): Post? {
        val snapshot = postsCollection.document(postId).get().await()
        return snapshot.toObject(Post::class.java)
    }

    override suspend fun getPostsByUser(userId: String): List<Post> {
        val snapshot = postsCollection.whereEqualTo("userId", userId).get().await()
        return snapshot.toObjects(Post::class.java)
    }

    override suspend fun updatePost(postId: String, updates: Map<String, Any>) {
        postsCollection.document(postId).update(updates).await()
    }

    override suspend fun deletePost(postId: String) {
        postsCollection.document(postId).delete().await()
    }

    override suspend fun incrementLikeCount(postId: String) {
        postsCollection.document(postId).update("likeCount", FieldValue.increment(1)).await()
    }

    override suspend fun searchPosts(query: String): List<Post> {
        return try {
            val snapshot = postsCollection.get().await()
            snapshot.toObjects(Post::class.java).filter { post ->
                post.content.contains(query, ignoreCase = true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error searching posts", e)
            emptyList()
        }
    }
} 