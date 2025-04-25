package com.androidfinalproject.hacktok.repository.impl

import android.util.Log
import com.androidfinalproject.hacktok.model.enums.RelationshipStatus
import com.androidfinalproject.hacktok.repository.RelationshipRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RelationshipRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : RelationshipRepository {

    private val TAG = "RelationshipRepo"
    private val relationshipsCollection = firestore.collection("relationships")
    private val suggestionsCollection = firestore.collection("suggestions")
    
    override suspend fun getRelationship(relationshipId: String): Map<String, Any>? {
        return try {
            val doc = relationshipsCollection.document(relationshipId).get().await()
            
            if (!doc.exists()) return null
            
            // Return the document data as a map
            doc.data?.toMutableMap()?.apply {
                this["id"] = doc.id
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting relationship $relationshipId: ${e.message}")
            null
        }
    }
    
    override suspend fun getRelationshipDocs(userId: String): List<Map<String, Any>> {
        try {
            // Query relationships where the user is involved
            val query1 = relationshipsCollection
                .whereEqualTo("user1Id", userId)
                .get()
                .await()
                
            val query2 = relationshipsCollection
                .whereEqualTo("user2Id", userId)
                .get()
                .await()
                
            val relationships = mutableListOf<Map<String, Any>>()
            
            // Process where user is user1
            for (doc in query1.documents) {
                val data = doc.data
                if (data != null) {
                    relationships.add(data.toMutableMap().apply {
                        this["id"] = doc.id
                        this["isUser1"] = true
                    })
                }
            }
            
            // Process where user is user2
            for (doc in query2.documents) {
                val data = doc.data
                if (data != null) {
                    relationships.add(data.toMutableMap().apply {
                        this["id"] = doc.id
                        this["isUser1"] = false
                    })
                }
            }
            
            return relationships
        } catch (e: Exception) {
            Log.e(TAG, "Error getting relationships for user $userId: ${e.message}")
            return emptyList()
        }
    }
    
    override suspend fun saveRelationship(relationshipId: String, data: Map<String, Any>): Boolean {
        return try {
            val docRef = relationshipsCollection.document(relationshipId)
            docRef.set(data).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error saving relationship $relationshipId: ${e.message}")
            false
        }
    }
    
    override suspend fun deleteRelationship(relationshipId: String): Boolean {
        return try {
            val docRef = relationshipsCollection.document(relationshipId)
            docRef.delete().await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting relationship $relationshipId: ${e.message}")
            false
        }
    }
    
    override suspend fun getHiddenSuggestions(userId: String): List<String> {
        return try {
            val doc = suggestionsCollection.document(userId).get().await()
            (doc.get("hidden") as? List<String>) ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting hidden suggestions for user $userId: ${e.message}")
            emptyList()
        }
    }
    
    override suspend fun updateHiddenSuggestions(userId: String, hiddenUserIds: List<String>): Boolean {
        return try {
            val docRef = suggestionsCollection.document(userId)
            docRef.set(mapOf("hidden" to hiddenUserIds)).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error updating hidden suggestions for user $userId: ${e.message}")
            false
        }
    }
    
    override fun observeRelationships(userId: String): Flow<List<Map<String, Any>>> = callbackFlow {
        val query1 = relationshipsCollection.whereEqualTo("user1Id", userId)
        val query2 = relationshipsCollection.whereEqualTo("user2Id", userId)

        // Keep track of the latest results from each query
        var results1: List<Map<String, Any>>? = null
        var results2: List<Map<String, Any>>? = null

        // Function to combine and send results
        fun sendCombinedResults() {
            val combined = mutableListOf<Map<String, Any>>()
            results1?.let { combined.addAll(it) }
            results2?.let { combined.addAll(it) }
            if (results1 != null && results2 != null) { // Only send when both queries have provided results at least once
                 trySend(combined.distinctBy { it["id"] }) // Ensure distinct results based on document ID
            }
        }

        val listener1 = query1.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Error observing relationships (user1Id): ${error.message}")
                close(error) // Close the flow on error
                return@addSnapshotListener
            }
            results1 = snapshot?.documents?.mapNotNull { doc ->
                doc.data?.toMutableMap()?.apply {
                    this["id"] = doc.id
                    this["isUser1"] = true
                }
            } ?: emptyList()
            sendCombinedResults()
        }

        val listener2 = query2.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Error observing relationships (user2Id): ${error.message}")
                close(error) // Close the flow on error
                return@addSnapshotListener
            }
            results2 = snapshot?.documents?.mapNotNull { doc ->
                 doc.data?.toMutableMap()?.apply {
                    this["id"] = doc.id
                    this["isUser1"] = false
                }
            } ?: emptyList()
            sendCombinedResults()
        }

        awaitClose {
            listener1.remove()
            listener2.remove()
        }
    }

    override suspend fun getFriendsOfUser(userId: String): List<String> {
        try {
            // Query relationships where user is involved and status is FRIENDS
            val query1 = relationshipsCollection
                .whereEqualTo("user1Id", userId)
                .whereEqualTo("status", RelationshipStatus.FRIENDS.toString())
                .get()
                .await()
            Log.d(TAG, "Get data for user1 $userId: ${query1.count()}")

            val query2 = relationshipsCollection
                .whereEqualTo("user2Id", userId)
                .whereEqualTo("status", RelationshipStatus.FRIENDS.toString())
                .get()
                .await()
            Log.d(TAG, "Get data for user2 $userId: ${query2.count()}")

            val friendIds = mutableListOf<String>()
            
            // Process where user is user1
            for (doc in query1.documents) {
                val data = doc.data ?: continue
                val user2Id = data["user2Id"] as? String ?: continue
                friendIds.add(user2Id)
            }
            
            // Process where user is user2
            for (doc in query2.documents) {
                val data = doc.data ?: continue
                val user1Id = data["user1Id"] as? String ?: continue
                friendIds.add(user1Id)
            }
            Log.d(TAG, "Get data $userId: ${friendIds.distinct().count()}")

            return friendIds.distinct()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting friends for user $userId: ${e.message}")
            return emptyList()
        }
    }
} 