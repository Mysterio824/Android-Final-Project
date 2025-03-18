package com.androidfinalproject.hacktok.repository

import com.androidfinalproject.hacktok.model.User
import kotlinx.coroutines.flow.first

import org.bson.Document
import org.bson.types.ObjectId

object UsersRepository {
    // Define your database and collection names.
    private const val DATABASE_NAME = "instagramClone"
    private const val COLLECTION_NAME = "users"

    // Get the collection reference.
    private val collection = MongoDbManager.getCollection(DATABASE_NAME, COLLECTION_NAME)

    // Helper to convert a Document to a User object.
    private fun documentToUser(doc: Document): User? {
        return try {
            User(
                id = doc.getObjectId("_id"),
                username = doc.getString("username"),
                email = doc.getString("email"),
                createdAt = doc.getDate("createdAt"),
                isActive = doc.getBoolean("isActive", true)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Create a new user.
    suspend fun insertUser(user: User): ObjectId? {
        // Create a document from the User object.
        val doc = Document()
            .append("name", user.username)
            .append("email", user.email)
            .append("createdAt", user.createdAt)
            .append("isActive", user.isActive)
        user.id?.let { doc["_id"] = it }
        val result = collection.insertOne(doc)
        return result.insertedId?.asObjectId()?.value
    }

    // Find a user by their ObjectId.
    suspend fun findUserById(id: ObjectId): User? {
        val doc = collection.find(Document("_id", id)).first()
        return documentToUser(doc)
    }

    // Retrieve all users.
    suspend fun listUsers(): List<User> {
        val documents = mutableListOf<Document>()
        collection.find().collect { documents.add(it) }
        return documents.mapNotNull { documentToUser(it) }
    }

    // Update a user by their ObjectId.
    suspend fun updateUser(id: ObjectId, update: Document): Long {
        val result = collection.updateOne(Document("_id", id), update)
        return result.modifiedCount
    }

    // Delete a user by their ObjectId.
    suspend fun deleteUser(id: ObjectId): Long {
        val result = collection.deleteOne(Document("_id", id))
        return result.deletedCount
    }
}