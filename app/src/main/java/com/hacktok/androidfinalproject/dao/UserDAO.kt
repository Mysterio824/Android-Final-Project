package com.hacktok.androidfinalproject.dao

import com.hacktok.androidfinalproject.model.User
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import org.bson.Document
import org.bson.types.ObjectId

class UserDAO(private val collection: MongoCollection<Document>) {

    // Helper function to convert a Document to a User data class.
    private fun documentToUser(doc: Document): User {
        return User(
            id = doc.getObjectId("_id"),
            username = doc.getString("name"),
            email = doc.getString("email"),
            createdAt = doc.getDate("createdAt"),
            isActive = doc.getBoolean("isActive", true)
        )
    }

    // Create a new user.
    suspend fun createUser(user: User): ObjectId? {
        val doc = Document()
            .append("name", user.username)
            .append("email", user.email)
            .append("createdAt", user.createdAt)
            .append("isActive", user.isActive)
        // Optionally include _id if provided.
        user.id?.let { doc["_id"] = it }
        val result = collection.insertOne(doc)
        return result.insertedId?.asObjectId()?.value
    }

    // Retrieve a user by its ObjectId.
    suspend fun getUserById(id: ObjectId): User? {
        val doc = collection.find(Document("_id", id)).first()
        return doc?.let { documentToUser(it) }
    }

    // Retrieve all users.
    suspend fun getAllUsers(): List<User> {
        return collection.find().toList().map { documentToUser(it) }
    }

    // Update a user by its ObjectId using an update document.
    suspend fun updateUser(id: ObjectId, update: Document): Long {
        val result = collection.updateOne(Document("_id", id), update)
        return result.modifiedCount
    }

    // Delete a user by its ObjectId.
    suspend fun deleteUser(id: ObjectId): Long {
        val result = collection.deleteOne(Document("_id", id))
        return result.deletedCount
    }
}
