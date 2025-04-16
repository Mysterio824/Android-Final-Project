package com.androidfinalproject.hacktok.repository.impl

import com.androidfinalproject.hacktok.model.MockData
import com.androidfinalproject.hacktok.model.RelationInfo
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.model.enums.RelationshipStatus
import com.androidfinalproject.hacktok.repository.RelationshipRepository
import com.androidfinalproject.hacktok.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.Instant
import javax.inject.Inject
import android.util.Log

class RelationshipRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userRepository: UserRepository
) : RelationshipRepository {

    private val relationshipsCollection = firestore.collection("relationships")
    private val suggestionsCollection = firestore.collection("suggestions")
    
    override suspend fun getRelationshipsForUser(userId: String): Map<String, RelationInfo> {
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
                
            val relationships = mutableMapOf<String, RelationInfo>()
            
            // Process where user is user1
            for (doc in query1.documents) {
                val user2Id = doc.getString("user2Id") ?: continue
                val statusStr = doc.getString("status") ?: continue
                val status = try {
                    RelationshipStatus.valueOf(statusStr)
                } catch (e: Exception) {
                    RelationshipStatus.NONE
                }
                val lastActionByMe = doc.getBoolean("lastActionByUser1") ?: false
                val timestamp = doc.getTimestamp("updatedAt")
                val updatedAt = timestamp?.toDate()?.toInstant() ?: Instant.now()
                
                relationships[user2Id] = RelationInfo(
                    id = doc.id,
                    status = status,
                    lastActionByMe = lastActionByMe,
                    updatedAt = updatedAt
                )
            }
            
            // Process where user is user2
            for (doc in query2.documents) {
                val user1Id = doc.getString("user1Id") ?: continue
                val statusStr = doc.getString("status") ?: continue
                var status = try {
                    RelationshipStatus.valueOf(statusStr)
                } catch (e: Exception) {
                    RelationshipStatus.NONE
                }
                val lastActionByUser1 = doc.getBoolean("lastActionByUser1") ?: false
                val timestamp = doc.getTimestamp("updatedAt")
                val updatedAt = timestamp?.toDate()?.toInstant() ?: Instant.now()
                
                // Invert status based on perspective (if needed)
                status = when (status) {
                    RelationshipStatus.PENDING_OUTGOING -> RelationshipStatus.PENDING_INCOMING
                    RelationshipStatus.PENDING_INCOMING -> RelationshipStatus.PENDING_OUTGOING
                    RelationshipStatus.BLOCKING -> RelationshipStatus.BLOCKED
                    RelationshipStatus.BLOCKED -> RelationshipStatus.BLOCKING
                    else -> status
                }
                
                relationships[user1Id] = RelationInfo(
                    id = doc.id,
                    status = status,
                    lastActionByMe = !lastActionByUser1,
                    updatedAt = updatedAt
                )
            }
            
            return relationships
        } catch (e: Exception) {
            // Return mock data for now (replace with proper error handling)
            return createMockRelations(userId)
        }
    }

    override suspend fun getFriendsForUser(userId: String): List<User> {
        val relationships = getRelationshipsForUser(userId)
        val friendIds = relationships
            .filter { it.value.status == RelationshipStatus.FRIENDS }
            .keys
            .toList()
            
        // Get user details for all friend IDs
        return userRepository.getUsersByIds(friendIds)
    }

    override suspend fun getFriendRequestsForUser(userId: String): List<User> {
        val relationships = getRelationshipsForUser(userId)
        val requestIds = relationships
            .filter { it.value.status == RelationshipStatus.PENDING_INCOMING }
            .keys
            .toList()
            
        // Get user details for all request IDs
        return userRepository.getUsersByIds(requestIds)
    }

    override suspend fun getFriendSuggestions(userId: String, limit: Int): List<User> {
        try {
            // Get existing relationships
            val relationships = getRelationshipsForUser(userId)
            val existingUserIds = relationships.keys
            
            // Query for suggestions
            val suggestionsDoc = try {
                suggestionsCollection.document(userId).get().await()
            } catch (e: Exception) {
                null
            }
            
            val hiddenSuggestions = try {
                suggestionsDoc?.get("hidden") as? List<String> ?: listOf()
            } catch (e: Exception) {
                listOf<String>()
            }
            
            // Get a list of users excluding existing relationships and hidden suggestions
            val allUsers = try {
                userRepository.getAllUsers()
            } catch (e: Exception) {
                // Return mock users if we can't get all users
                MockData.mockUsers
            }
            
            return allUsers
                .filter { 
                    it.id != userId && 
                    it.id != null && 
                    !existingUserIds.contains(it.id) && 
                    !hiddenSuggestions.contains(it.id) 
                }
                .take(limit)
                
        } catch (e: Exception) {
            // Return mock data for now
            return MockData.mockUsers
                .filter { it.id != userId }
                .take(limit)
        }
    }

    override suspend fun sendFriendRequest(fromUserId: String, toUserId: String): Boolean {
        return updateRelationship(
            user1Id = fromUserId,
            user2Id = toUserId,
            status = RelationshipStatus.PENDING_OUTGOING,
            lastActionByUser1 = true
        )
    }

    override suspend fun cancelFriendRequest(fromUserId: String, toUserId: String): Boolean {
        return updateRelationship(
            user1Id = fromUserId,
            user2Id = toUserId,
            status = RelationshipStatus.NONE,
            lastActionByUser1 = true
        )
    }

    override suspend fun acceptFriendRequest(fromUserId: String, toUserId: String): Boolean {
        return updateRelationship(
            user1Id = toUserId, // Note: fromUserId is the one accepting, so they're user2
            user2Id = fromUserId,
            status = RelationshipStatus.FRIENDS,
            lastActionByUser1 = false
        )
    }

    override suspend fun declineFriendRequest(fromUserId: String, toUserId: String): Boolean {
        return updateRelationship(
            user1Id = toUserId, // Note: fromUserId is the one declining, so they're user2
            user2Id = fromUserId,
            status = RelationshipStatus.NONE,
            lastActionByUser1 = false
        )
    }

    override suspend fun blockUser(fromUserId: String, toUserId: String): Boolean {
        return updateRelationship(
            user1Id = fromUserId,
            user2Id = toUserId,
            status = RelationshipStatus.BLOCKING,
            lastActionByUser1 = true
        )
    }

    override suspend fun unblockUser(fromUserId: String, toUserId: String): Boolean {
        return updateRelationship(
            user1Id = fromUserId,
            user2Id = toUserId,
            status = RelationshipStatus.NONE,
            lastActionByUser1 = true
        )
    }

    override suspend fun getRelationshipStatus(userId1: String, userId2: String): RelationshipStatus {
        val relationships = getRelationshipsForUser(userId1)
        return relationships[userId2]?.status ?: RelationshipStatus.NONE
    }

    override suspend fun removeFromSuggestions(userId: String, suggestionId: String): Boolean {
        try {
            val docRef = suggestionsCollection.document(userId)
            val doc = docRef.get().await()
            
            val hiddenList = doc.get("hidden") as? List<String> ?: listOf()
            val updatedList = hiddenList + suggestionId
            
            docRef.update("hidden", updatedList).await()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    override fun observeRelationships(userId: String): Flow<Map<String, RelationInfo>> = callbackFlow {
        val query1 = relationshipsCollection.whereEqualTo("user1Id", userId)
        val query2 = relationshipsCollection.whereEqualTo("user2Id", userId)
        
        // Send initial mock data if any error happens
        try {
            val initialRelations = createMockRelations(userId)
            trySend(initialRelations)
        } catch (e: Exception) {
            Log.e("RelationshipRepo", "Error sending initial data: ${e.message}")
            trySend(emptyMap())
        }
        
        val listener1 = query1.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("RelationshipRepo", "Error observing relationships (user1Id): ${error.message}")
                return@addSnapshotListener
            }
            
            // Process the changes without using suspend functions
            if (snapshot != null) {
                val relations = processSnapshotsAndCreateRelations(userId, snapshot, null)
                trySend(relations)
            }
        }
        
        val listener2 = query2.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("RelationshipRepo", "Error observing relationships (user2Id): ${error.message}")
                return@addSnapshotListener
            }
            
            // Process the changes without using suspend functions
            if (snapshot != null) {
                val relations = processSnapshotsAndCreateRelations(userId, null, snapshot)
                trySend(relations)
            }
        }
        
        awaitClose {
            listener1.remove()
            listener2.remove()
        }
    }
    
    // Non-suspending helper function to process snapshots
    private fun processSnapshotsAndCreateRelations(
        userId: String,
        snapshot1: com.google.firebase.firestore.QuerySnapshot?,
        snapshot2: com.google.firebase.firestore.QuerySnapshot?
    ): Map<String, RelationInfo> {
        val relationships = mutableMapOf<String, RelationInfo>()
        
        try {
            // Process snapshot1 (where user is user1)
            snapshot1?.documents?.forEach { doc ->
                val user2Id = doc.getString("user2Id") ?: return@forEach
                val statusStr = doc.getString("status") ?: return@forEach
                val status = try {
                    RelationshipStatus.valueOf(statusStr)
                } catch (e: Exception) {
                    RelationshipStatus.NONE
                }
                val lastActionByMe = doc.getBoolean("lastActionByUser1") ?: false
                val timestamp = doc.getTimestamp("updatedAt")
                val updatedAt = timestamp?.toDate()?.toInstant() ?: Instant.now()
                
                relationships[user2Id] = RelationInfo(
                    id = doc.id,
                    status = status,
                    lastActionByMe = lastActionByMe,
                    updatedAt = updatedAt
                )
            }
            
            // Process snapshot2 (where user is user2)
            snapshot2?.documents?.forEach { doc ->
                val user1Id = doc.getString("user1Id") ?: return@forEach
                val statusStr = doc.getString("status") ?: return@forEach
                val status = try {
                    val rawStatus = RelationshipStatus.valueOf(statusStr)
                    // Invert status based on perspective (if needed)
                    when (rawStatus) {
                        RelationshipStatus.PENDING_OUTGOING -> RelationshipStatus.PENDING_INCOMING
                        RelationshipStatus.PENDING_INCOMING -> RelationshipStatus.PENDING_OUTGOING
                        RelationshipStatus.BLOCKING -> RelationshipStatus.BLOCKED
                        RelationshipStatus.BLOCKED -> RelationshipStatus.BLOCKING
                        else -> rawStatus
                    }
                } catch (e: Exception) {
                    RelationshipStatus.NONE
                }
                val lastActionByUser1 = doc.getBoolean("lastActionByUser1") ?: false
                val timestamp = doc.getTimestamp("updatedAt")
                val updatedAt = timestamp?.toDate()?.toInstant() ?: Instant.now()
                
                relationships[user1Id] = RelationInfo(
                    id = doc.id,
                    status = status,
                    lastActionByMe = !lastActionByUser1,
                    updatedAt = updatedAt
                )
            }
            
            return relationships
        } catch (e: Exception) {
            Log.e("RelationshipRepo", "Error processing snapshots: ${e.message}")
            return createMockRelations(userId)
        }
    }
    
    // Helper function to update or create a relationship
    private suspend fun updateRelationship(
        user1Id: String,
        user2Id: String,
        status: RelationshipStatus,
        lastActionByUser1: Boolean
    ): Boolean {
        try {
            // Always store with smaller ID as user1 for consistent querying
            val (finalUser1Id, finalUser2Id, finalStatus, finalLastActionByUser1) = if (user1Id < user2Id) {
                arrayOf(user1Id, user2Id, status, lastActionByUser1)
            } else {
                // Invert the status if we swap users
                val invertedStatus = when (status) {
                    RelationshipStatus.PENDING_OUTGOING -> RelationshipStatus.PENDING_INCOMING
                    RelationshipStatus.PENDING_INCOMING -> RelationshipStatus.PENDING_OUTGOING
                    RelationshipStatus.BLOCKING -> RelationshipStatus.BLOCKED
                    RelationshipStatus.BLOCKED -> RelationshipStatus.BLOCKING
                    else -> status
                }
                arrayOf(user2Id, user1Id, invertedStatus, !lastActionByUser1)
            }
            
            // Get relationship document ID
            val relationshipId = "${finalUser1Id}_${finalUser2Id}"
            val docRef = relationshipsCollection.document(relationshipId)
            
            val update = hashMapOf(
                "user1Id" to finalUser1Id,
                "user2Id" to finalUser2Id,
                "status" to finalStatus.toString(),
                "lastActionByUser1" to finalLastActionByUser1,
                "updatedAt" to com.google.firebase.Timestamp.now()
            )
            
            docRef.set(update).await()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    // Helper method to create mock relationships for testing
    private fun createMockRelations(userId: String): Map<String, RelationInfo> {
        val now = Instant.now()
        
        return mapOf(
            "user1" to RelationInfo(
                id = "${minOf(userId, "user1")}_${maxOf(userId, "user1")}",
                status = RelationshipStatus.FRIENDS,
                lastActionByMe = true,
                updatedAt = now.minusSeconds(3600) // 1 hour ago
            ),
            "user2" to RelationInfo(
                id = "${minOf(userId, "user2")}_${maxOf(userId, "user2")}",
                status = RelationshipStatus.PENDING_OUTGOING,
                lastActionByMe = true,
                updatedAt = now.minusSeconds(7200) // 2 hours ago
            ),
            "user3" to RelationInfo(
                id = "${minOf(userId, "user3")}_${maxOf(userId, "user3")}",
                status = RelationshipStatus.PENDING_INCOMING,
                lastActionByMe = false,
                updatedAt = now.minusSeconds(10800) // 3 hours ago
            ),
            "user4" to RelationInfo(
                id = "${minOf(userId, "user4")}_${maxOf(userId, "user4")}",
                status = RelationshipStatus.BLOCKING,
                lastActionByMe = true,
                updatedAt = now.minusSeconds(14400) // 4 hours ago
            ),
            "user5" to RelationInfo(
                id = "${minOf(userId, "user5")}_${maxOf(userId, "user5")}",
                status = RelationshipStatus.BLOCKED,
                lastActionByMe = false,
                updatedAt = now.minusSeconds(18000) // 5 hours ago
            )
        )
    }
} 