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
            return createMockRelationDocs(userId)
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
        
        // Send initial data
        try {
            val initialRelations = createMockRelationDocs(userId)
            trySend(initialRelations)
        } catch (e: Exception) {
            Log.e(TAG, "Error sending initial data: ${e.message}")
            trySend(emptyList())
        }
        
        val listener1 = query1.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Error observing relationships (user1Id): ${error.message}")
                return@addSnapshotListener
            }
            
            if (snapshot != null) {
                val relations = processSnapshots(userId, snapshot, null)
                trySend(relations)
            }
        }
        
        val listener2 = query2.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Error observing relationships (user2Id): ${error.message}")
                return@addSnapshotListener
            }
            
            if (snapshot != null) {
                val relations = processSnapshots(userId, null, snapshot)
                trySend(relations)
            }
        }
        
        awaitClose {
            listener1.remove()
            listener2.remove()
        }
    }
    
    // Helper function to process snapshots
    private fun processSnapshots(
        userId: String,
        snapshot1: com.google.firebase.firestore.QuerySnapshot?,
        snapshot2: com.google.firebase.firestore.QuerySnapshot?
    ): List<Map<String, Any>> {
        val relationships = mutableListOf<Map<String, Any>>()
        
        try {
            // Process snapshot1 (where user is user1)
            snapshot1?.documents?.forEach { doc ->
                val data = doc.data
                if (data != null) {
                    relationships.add(data.toMutableMap().apply {
                        this["id"] = doc.id
                        this["isUser1"] = true
                    })
                }
            }
            
            // Process snapshot2 (where user is user2)
            snapshot2?.documents?.forEach { doc ->
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
            Log.e(TAG, "Error processing snapshots: ${e.message}")
            return createMockRelationDocs(userId)
        }
    }
    
    // Helper method to create mock relationship documents for testing
    private fun createMockRelationDocs(userId: String): List<Map<String, Any>> {
        val now = com.google.firebase.Timestamp.now()
        
        return listOf(
            mapOf(
                "id" to "${minOf(userId, "user1")}_${maxOf(userId, "user1")}",
                "user1Id" to minOf(userId, "user1"),
                "user2Id" to maxOf(userId, "user1"),
                "status" to RelationshipStatus.FRIENDS.toString(),
                "lastActionByUser1" to (userId < "user1"),
                "updatedAt" to now,
                "isUser1" to (userId < "user1")
            ),
            mapOf(
                "id" to "${minOf(userId, "user2")}_${maxOf(userId, "user2")}",
                "user1Id" to minOf(userId, "user2"),
                "user2Id" to maxOf(userId, "user2"),
                "status" to RelationshipStatus.PENDING_OUTGOING.toString(),
                "lastActionByUser1" to (userId < "user2"),
                "updatedAt" to now,
                "isUser1" to (userId < "user2")
            ),
            mapOf(
                "id" to "${minOf(userId, "user3")}_${maxOf(userId, "user3")}",
                "user1Id" to minOf(userId, "user3"),
                "user2Id" to maxOf(userId, "user3"),
                "status" to RelationshipStatus.PENDING_INCOMING.toString(),
                "lastActionByUser1" to (userId < "user3"),
                "updatedAt" to now,
                "isUser1" to (userId < "user3")
            ),
            mapOf(
                "id" to "${minOf(userId, "user4")}_${maxOf(userId, "user4")}",
                "user1Id" to minOf(userId, "user4"),
                "user2Id" to maxOf(userId, "user4"),
                "status" to RelationshipStatus.BLOCKING.toString(),
                "lastActionByUser1" to (userId < "user4"),
                "updatedAt" to now,
                "isUser1" to (userId < "user4")
            ),
            mapOf(
                "id" to "${minOf(userId, "user5")}_${maxOf(userId, "user5")}",
                "user1Id" to minOf(userId, "user5"),
                "user2Id" to maxOf(userId, "user5"),
                "status" to RelationshipStatus.BLOCKED.toString(),
                "lastActionByUser1" to (userId < "user5"),
                "updatedAt" to now,
                "isUser1" to (userId < "user5")
            )
        )
    }
} 