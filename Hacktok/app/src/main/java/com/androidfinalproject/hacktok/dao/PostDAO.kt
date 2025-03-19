package com.androidfinalproject.hacktok.dao

import com.androidfinalproject.hacktok.model.Post
import com.androidfinalproject.hacktok.model.User
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import org.bson.Document
import org.bson.types.ObjectId
import java.util.Date

class PostDAO(private val collection: MongoCollection<Document>) {

    // Helper function to convert a Document to a Post data class.
    private fun documentToPost(doc: Document): Post {
        return Post(
            id = doc.getObjectId("_id"),
            content = doc.getString("content"),
            user = User(
                id = doc.getObjectId("userId"),
                username = "", // Có thể null hoặc cần lấy từ UserDAO
                email = "",
                createdAt = doc.getDate("createdAt") ?: Date(),
                isActive = doc.getBoolean("isActive", true)
            ),
            createdAt = doc.getDate("createdAt") ?: Date(),
            isActive = doc.getBoolean("isActive", true)
        )
    }

    // Create a new post.
    suspend fun createPost(post: Post): ObjectId? {
        val doc = Document()
            .append("content", post.content)
            .append("userId", post.user.id) // Lưu userId thay vì toàn bộ user object
            .append("createdAt", post.createdAt)
            .append("isActive", post.isActive)
        // Optionally include _id if provided.
        post.id?.let { doc["_id"] = it }
        val result = collection.insertOne(doc)
        return result.insertedId?.asObjectId()?.value
    }

    // Retrieve a post by its ObjectId.
    suspend fun getPostById(id: ObjectId): Post? {
        val doc = collection.find(Document("_id", id)).first()
        return doc?.let { documentToPost(it) }
    }

    // Retrieve all posts.
    suspend fun getAllPosts(): List<Post> {
        return collection.find().toList().map { documentToPost(it) }
    }

    // Update a post by its ObjectId using an update document.
    suspend fun updatePost(id: ObjectId, update: Document): Long {
        val result = collection.updateOne(Document("_id", id), update)
        return result.modifiedCount
    }

    // Delete a post by its ObjectId.
    suspend fun deletePost(id: ObjectId): Long {
        val result = collection.deleteOne(Document("_id", id))
        return result.deletedCount
    }
}