package com.androidfinalproject.hacktok.dao

import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.model.User
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import org.bson.Document

class UserDAO(private val collection: MongoCollection<Document>) {

    // Helper function to convert a Document to a User data class.
    private fun documentToUser(doc: Document): User {
        return MockData.mockUsers.first()
    }

    // Create a new user.
    suspend fun createUser(user: User): String? {
        val doc = Document()
            .append("name", user.username)
            .append("email", user.email)
            .append("createdAt", user.createdAt)
            .append("isActive", user.isActive)
        // Optionally include _id if provided.
        user.id?.let { doc["_id"] = it }
        val result = collection.insertOne(doc)
        return result.insertedId?.asString()?.value
    }

    // Retrieve a user by its String.
    suspend fun getUserById(id: String): User? {
        val doc = collection.find(Document("_id", id)).first()
        return doc?.let { documentToUser(it) }
    }

    // Retrieve all users.
    suspend fun getAllUsers(): List<User> {
        return collection.find().toList().map { documentToUser(it) }
    }

    // Update a user by its String using an update document.
    suspend fun updateUser(id: String, update: Document): Long {
        val result = collection.updateOne(Document("_id", id), update)
        return result.modifiedCount
    }

    // Delete a user by its String.
    suspend fun deleteUser(id: String): Long {
        val result = collection.deleteOne(Document("_id", id))
        return result.deletedCount
    }
}
