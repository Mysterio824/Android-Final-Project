package com.androidfinalproject.hacktok.repository

import com.androidfinalproject.hacktok.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import org.bson.types.ObjectId
import java.util.Date

object UsersRepository {
    private const val COLLECTION_NAME = "users"

    // Get Firestore instance
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection(COLLECTION_NAME)

    // Helper to convert Firestore document to User object
    private fun documentToUser(doc: Map<String, Any>, id: String): User? {
        return try {
            User(
                id = try { ObjectId(id) } catch (e: Exception) { null }, // Convert String to ObjectId
                username = doc["username"] as? String ?: "",
                email = doc["email"] as? String ?: "",
                createdAt = doc["createdAt"] as? Date ?: Date(),
                isActive = doc["isActive"] as? Boolean ?: true
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Create a new user
    suspend fun insertUser(user: User): ObjectId? {
        return try {
            val userData = hashMapOf(
                "username" to user.username,
                "email" to user.email,
                "createdAt" to user.createdAt,
                "isActive" to user.isActive
            )

            val result = if (user.id == null) {
                // If no ID provided, create new document with auto-generated ID
                collection.add(userData).await()
            } else {
                // If ID provided, use it
                collection.document(user.id.toString()).set(userData).await()
                null // Return null since we're using provided ID
            }

            val newId = result?.id ?: user.id?.toString()
            newId?.let { ObjectId(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Find a user by their ObjectId
    suspend fun findUserById(id: ObjectId): User? {
        return try {
            val document = collection.document(id.toString()).get().await()
            if (document.exists()) {
                documentToUser(document.data!!, document.id)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Retrieve all users
    suspend fun listUsers(): List<User> {
        return try {
            val result = collection.get().await()
            result.documents.mapNotNull { doc ->
                doc.data?.let { documentToUser(it, doc.id) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // Update a user by their ObjectId
    suspend fun updateUser(id: ObjectId, update: Map<String, Any>): Long {
        return try {
            collection.document(id.toString())
                .set(update, SetOptions.merge())
                .await()
            1L // Firestore doesn't return modified count, return 1 if successful
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }

    // Delete a user by their ObjectId
    suspend fun deleteUser(id: ObjectId): Long {
        return try {
            collection.document(id.toString()).delete().await()
            1L // Firestore doesn't return deleted count, return 1 if successful
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }
}