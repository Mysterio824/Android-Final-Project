package com.androidfinalproject.hacktok.repository.impl

import android.util.Log
import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.repository.PostRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PostRepository {
    private val TAG = "PostRepository"
    private val postsCollection = firestore.collection("posts")
    private var lastVisibleSnapshot: DocumentSnapshot? = null

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

    override suspend fun updatePostContentOnly(postId: String, newContent: String, newPrivacy: String, newImageLink: String) {
        val updates = mapOf(
            "content" to newContent,
            "privacy" to newPrivacy,
            "imageLink" to newImageLink
        )

        updatePost(postId, updates)
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

    override suspend fun getNextPosts(
        userId: String,
        friendList: List<String>,
        limit: Long
    ): List<Post> {
        try {
            // Fetch more than needed to account for filtering
            val query = firestore.collection("posts")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .let { baseQuery ->
                    lastVisibleSnapshot?.let {
                        baseQuery.startAfter(it)
                    } ?: baseQuery
                }
                .limit(limit)

            val snapshot = query.get().await()
            val documents = snapshot.documents

            if (documents.isNotEmpty()) {
                lastVisibleSnapshot = documents.last()
            }

            val filteredPosts = documents.mapNotNull { doc ->
                val post = doc.toObject(Post::class.java)?.copy(id = doc.id)
                val authorId = post?.userId?.trim()

                when (post?.privacy) {
                    "PUBLIC" -> post
                    "FRIENDS" -> if (authorId in friendList || authorId == userId) post else null
                    else -> null // PRIVATE or unknown
                }
            }
            val finalPosts = filteredPosts.take(limit.toInt())
            return finalPosts
        } catch (e: Exception) {
            Log.e("PostRepository", "Error fetching posts", e)
            return emptyList()
        }
    }
    override fun resetPagination() {
        lastVisibleSnapshot = null
    }
}