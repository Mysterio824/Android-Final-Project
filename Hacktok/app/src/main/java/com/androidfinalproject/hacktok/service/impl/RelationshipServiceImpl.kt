package com.androidfinalproject.hacktok.service.impl

import android.util.Log
import com.androidfinalproject.hacktok.model.RelationInfo
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.model.enums.NotificationType
import com.androidfinalproject.hacktok.model.enums.RelationshipStatus
import com.androidfinalproject.hacktok.model.enums.UserRole
import com.androidfinalproject.hacktok.repository.RelationshipRepository
import com.androidfinalproject.hacktok.repository.UserRepository
import com.androidfinalproject.hacktok.service.AuthService
import com.androidfinalproject.hacktok.service.NotificationService
import com.androidfinalproject.hacktok.service.RelationshipService
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RelationshipServiceImpl @Inject constructor(
    private val relationshipRepository: RelationshipRepository,
    private val userRepository: UserRepository,
    private val authService: AuthService,
    private val notificationService: NotificationService
) : RelationshipService {

    private val TAG = "RelationshipService"

    override suspend fun getMyRelationships(): Map<String, RelationInfo> {
        val currentUserId = authService.getCurrentUserId() ?: return emptyMap()
        return getRelationshipsForUser(currentUserId)
    }

    override suspend fun getRelationships(userId: String): Map<String, RelationInfo> {
        return getRelationshipsForUser(userId)
    }

    override suspend fun getFriends(userId: String): Map<String, RelationInfo> {
        return getRelationshipsForUser(userId)
            .filter { it.value.status == RelationshipStatus.FRIENDS }
    }

    override suspend fun getRelationship(userId: String): RelationInfo {
        val currentUserId = authService.getCurrentUserId() ?: return RelationInfo( id = "" )
        if (currentUserId != userId) {
            val relationships = getMyRelationships()
            return relationships[userId] ?: RelationInfo( id = "" )
        } else {
            return RelationInfo( id = "" )
        }
    }

    override suspend fun getMyFriends(): List<User> {
        val currentUserId = authService.getCurrentUserId() ?: return emptyList()
        val relationships = getRelationshipsForUser(currentUserId)
        
        // Filter relationships to get only friends
        val friendIds = relationships
            .filter { it.value.status == RelationshipStatus.FRIENDS }
            .keys
            .toList()
            
        // Fetch user details for friend IDs
        return userRepository.getUsersByIds(friendIds)
    }

    override suspend fun getMyFriendRequests(): List<User> {
        val currentUserId = authService.getCurrentUserId() ?: return emptyList()
        val relationships = getRelationshipsForUser(currentUserId)
        
        // Filter relationships to get only incoming requests
        val requestIds = relationships
            .filter { it.value.status == RelationshipStatus.PENDING_INCOMING }
            .keys
            .toList()
            
        // Fetch user details for request IDs
        return userRepository.getUsersByIds(requestIds)
    }

    override suspend fun getUserFromRelationship(relations: Map<String, RelationInfo>): List<User> {
        // Filter relationships to get only friends
        val friendIds = relations
            .filter { it.value.status == RelationshipStatus.FRIENDS }
            .keys
            .toList()

        // Fetch user details for friend IDs
        return userRepository.getUsersByIds(friendIds)
    }

    override suspend fun getFriendSuggestions(limit: Int): List<User> {
        val currentUserId = authService.getCurrentUserId() ?: return emptyList()

        try {
            val relationships = getRelationshipsForUser(currentUserId)
            val existingUserIds = relationships.keys

            val hiddenSuggestions = relationshipRepository.getHiddenSuggestions(currentUserId)

            val directRelations = convertToRelationInfoMap(currentUserId, relationshipRepository.getRelationshipDocs(currentUserId))
            val friendsOfFriends = fetchFriendsOfFriends(directRelations)

            val fofUserIds = friendsOfFriends.keys.toList()
            val fofUsers = userRepository.getUsersByIds(fofUserIds)
                .filter { user ->
                    user.id != null &&
                            user.id != currentUserId &&
                            !hiddenSuggestions.contains(user.id)
                }

            if (fofUsers.size >= limit) {
                return fofUsers.take(limit)
            }

            val allUsers = userRepository.getAllUsers()
            val additionalUsers = allUsers
                .filter { user ->
                    user.id != null &&
                            user.id != currentUserId &&
                            !existingUserIds.contains(user.id) &&
                            !hiddenSuggestions.contains(user.id) &&
                            !fofUserIds.contains(user.id) &&
                            user.role != UserRole.ADMIN && user.role != UserRole.MODERATOR
                }

            return (fofUsers + additionalUsers).take(limit)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting friend suggestions: ${e.message}")
            return emptyList()
        }
    }

    private suspend fun fetchFriendsOfFriends(directRelations: Map<String, RelationInfo>): Map<String, RelationInfo> {
        val currentUserId = authService.getCurrentUserId() ?: return emptyMap()
        val result = mutableMapOf<String, RelationInfo>()

        try {
            // Get direct friends only
            val myFriendIds = directRelations
                .filter { it.value.status == RelationshipStatus.FRIENDS }
                .keys
                .toList()

            // For each friend, get their friends
            myFriendIds.forEach { friendId ->
                // Get relationships for this friend
                val friendRelationships = getRelationshipsForUser(friendId)

                // Get only friends of this friend
                val secondDegreeConnections = friendRelationships
                    .filter { it.value.status == RelationshipStatus.FRIENDS }
                    .keys
                    .toList()

                // Add second-degree connections (friend of friend)
                secondDegreeConnections.forEach { fofId ->
                    // Skip if it's the current user or already in direct relationships
                    if (fofId != currentUserId && !directRelations.containsKey(fofId)) {
                        // Create a RelationInfo for friend-of-friend
                        result[fofId] = RelationInfo(
                            id = fofId,
                            status = RelationshipStatus.NONE,
                            lastActionByMe = false,
                            updatedAt = null
                        )
                    }
                }
            }

            return result
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching friends of friends: ${e.message}")
            return emptyMap()
        }
    }

    override suspend fun sendFriendRequest(toUserId: String): Boolean {
        Log.d("start function", toUserId)

        val currentUserId = authService.getCurrentUserId() ?: run {
            Log.d("not found user", "null")
            return false
        }
        if (currentUserId == toUserId) return false // Cannot send request to self
        Log.d("start", "$currentUserId:$toUserId")

        val success = createOrUpdateRelationship(
            fromUserId = currentUserId,
            toUserId = toUserId,
            status = RelationshipStatus.PENDING_OUTGOING,
            lastActionByFromUser = true
        )

        if (success) {
            // Create notification for the recipient
            notificationService.createNotification(
                recipientUserId = toUserId,
                type = NotificationType.FRIEND_REQUEST,
                senderId = currentUserId,
                relatedItemId = currentUserId // For friend request, related item is the sender
            )
        }
        return success
    }

    override suspend fun cancelFriendRequest(toUserId: String): Boolean {
        Log.d("RelationshipService", "start unfriend")
        val currentUserId = authService.getCurrentUserId() ?: return false
        
        // Simply delete the relationship document (sets status to NONE implicitly via createOrUpdate)
        val success = createOrUpdateRelationship(
            fromUserId = currentUserId,
            toUserId = toUserId,
            status = RelationshipStatus.NONE, // This will trigger deletion in createOrUpdateRelationship
            lastActionByFromUser = true 
        )
        Log.d("RelationshipService", "done unfriend")

        return success
    }

    override suspend fun acceptFriendRequest(fromUserId: String): Boolean {
        val currentUserId = authService.getCurrentUserId() ?: return false
        
        val success = createOrUpdateRelationship(
            fromUserId = fromUserId, 
            toUserId = currentUserId,
            status = RelationshipStatus.FRIENDS,
            lastActionByFromUser = false // Current user (toUser) is taking action
        )
        
        if (success) {
             // Create notification for the original sender
             notificationService.createNotification(
                 recipientUserId = fromUserId,
                 type = NotificationType.FRIEND_ACCEPT,
                 senderId = currentUserId,
                 relatedItemId = currentUserId // For friend accept, related item is the acceptor
             )
        }
        return success
    }

    override suspend fun declineFriendRequest(fromUserId: String): Boolean {
        val currentUserId = authService.getCurrentUserId() ?: return false
        
        // Simply delete the relationship document (sets status to NONE implicitly via createOrUpdate)
         val success = createOrUpdateRelationship(
            fromUserId = fromUserId, 
            toUserId = currentUserId,
            status = RelationshipStatus.NONE, // This will trigger deletion
            lastActionByFromUser = false 
        )
         // Optional: Delete pending notification? See cancelFriendRequest comment.
         return success
    }

    override suspend fun blockUser(userId: String): Boolean {
        val currentUserId = authService.getCurrentUserId() ?: return false
        if (currentUserId == userId) return false // Cannot block self

        return createOrUpdateRelationship(
            fromUserId = currentUserId,
            toUserId = userId,
            status = RelationshipStatus.BLOCKING,
            lastActionByFromUser = true
        )
        // No notification needed for blocking
    }

    override suspend fun unblockUser(userId: String): Boolean {
        val currentUserId = authService.getCurrentUserId() ?: return false
        
        // Set status back to NONE
         return createOrUpdateRelationship(
            fromUserId = currentUserId,
            toUserId = userId,
            status = RelationshipStatus.NONE, // This will trigger deletion
            lastActionByFromUser = true 
        )
         // No notification needed for unblocking
    }

    override suspend fun getRelationshipStatus(userId: String): RelationshipStatus {
        val currentUserId = authService.getCurrentUserId() ?: return RelationshipStatus.NONE
        val relationships = getRelationshipsForUser(currentUserId)
        return relationships[userId]?.status ?: RelationshipStatus.NONE
    }

    override suspend fun removeFromSuggestions(suggestionId: String): Boolean {
        val currentUserId = authService.getCurrentUserId() ?: return false
        
        try {
            val hiddenSuggestions = relationshipRepository.getHiddenSuggestions(currentUserId).toMutableList()
            
            if (!hiddenSuggestions.contains(suggestionId)) {
                 hiddenSuggestions.add(suggestionId)
                 return relationshipRepository.updateHiddenSuggestions(currentUserId, hiddenSuggestions)
            }
            return true // Already hidden
        } catch (e: Exception) {
            Log.e(TAG, "Error removing from suggestions: ${e.message}")
            return false
        }
        // No notification needed
    }

    override fun observeMyRelationships(): Flow<Map<String, RelationInfo>> {
        val currentUserId = authService.getCurrentUserIdSync() ?: return emptyFlow()
        return relationshipRepository.observeRelationships(currentUserId)
            .map { mapList -> convertToRelationInfoMap(currentUserId, mapList) }
    }
    
    // Helper method to create or update a relationship in the repository
    private suspend fun createOrUpdateRelationship(
        fromUserId: String,
        toUserId: String,
        status: RelationshipStatus,
        lastActionByFromUser: Boolean // True if the action (changing status) is initiated by fromUserId
    ): Boolean {
        try {
            // Determine user1Id and user2Id (always store with smaller ID first)
            val (user1Id, user2Id) = if (fromUserId < toUserId) {
                fromUserId to toUserId
            } else {
                toUserId to fromUserId
            }

            // Determine the status and lastActionByUser1 based on the canonical user1/user2 ordering
            val (finalStatus, finalLastActionByUser1) = if (fromUserId < toUserId) {
                // fromUserId is user1
                status to lastActionByFromUser
            } else {
                // fromUserId is user2, need to potentially invert status and action indicator
                val invertedStatus = when (status) {
                    RelationshipStatus.PENDING_OUTGOING -> RelationshipStatus.PENDING_INCOMING
                    RelationshipStatus.PENDING_INCOMING -> RelationshipStatus.PENDING_OUTGOING
                    RelationshipStatus.BLOCKING -> RelationshipStatus.BLOCKED
                    RelationshipStatus.BLOCKED -> RelationshipStatus.BLOCKING
                    else -> status // FRIENDS, NONE remain the same
                }
                invertedStatus to !lastActionByFromUser
            }

            // Create relationship ID
            val relationshipId = "${user1Id}_${user2Id}"
            
            // Prepare data for saving
            val data = hashMapOf(
                "user1Id" to user1Id,
                "user2Id" to user2Id,
                "status" to finalStatus.toString(),
                "lastActionByUser1" to finalLastActionByUser1,
                "updatedAt" to Timestamp.now() // Update timestamp on every action
            )
            Log.d("RelationshipService", "during unfriend")


            // If status is NONE, delete the document instead of saving NONE status
            return if (finalStatus == RelationshipStatus.NONE) {
                 relationshipRepository.deleteRelationship(relationshipId)
            } else {
                 relationshipRepository.saveRelationship(relationshipId, data)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creating/updating relationship between $fromUserId and $toUserId: ${e.message}")
            return false
        }
    }
    
    // Helper method to get relationships for a user and convert to RelationInfo Map
    private suspend fun getRelationshipsForUser(userId: String): Map<String, RelationInfo> {
        return try {
            val relationshipDocs = relationshipRepository.getRelationshipDocs(userId)
            convertToRelationInfoMap(userId, relationshipDocs)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting and converting relationships for user $userId: ${e.message}")
            emptyMap() // Return empty map on error
        }
    }
    
    // Helper method to convert raw List<Map<String, Any>> from repository to Map<String, RelationInfo> keyed by otherUserId
    private fun convertToRelationInfoMap(currentUserId: String, relationshipDocs: List<Map<String, Any>>): Map<String, RelationInfo> {
        val result = mutableMapOf<String, RelationInfo>()
        
        for (docData in relationshipDocs) {
            try {
                val docId = docData["id"] as? String // This is the actual document ID (user1_user2)
                val user1Id = docData["user1Id"] as? String ?: continue
                val user2Id = docData["user2Id"] as? String ?: continue
                val statusString = docData["status"] as? String ?: continue
                val lastActionByUser1 = docData["lastActionByUser1"] as? Boolean ?: false
                val firestoreTimestamp = docData["updatedAt"] as? Timestamp
                // isUser1 is added by the repository implementation
                val isCurrentUserUser1 = docData["isUser1"] as? Boolean ?: (currentUserId == user1Id) 

                // Determine the other user ID
                val otherUserId = if (isCurrentUserUser1) user2Id else user1Id

                val updatedAtInstant: Instant? = firestoreTimestamp?.toDate()?.toInstant()

                // Determine RelationshipStatus from the current user's perspective
                val status = try {
                    val rawStatus = RelationshipStatus.valueOf(statusString)
                    // If the current user is user2, invert directional statuses
                    if (!isCurrentUserUser1) {
                        when (rawStatus) {
                            RelationshipStatus.PENDING_OUTGOING -> RelationshipStatus.PENDING_INCOMING
                            RelationshipStatus.PENDING_INCOMING -> RelationshipStatus.PENDING_OUTGOING
                            RelationshipStatus.BLOCKING -> RelationshipStatus.BLOCKED
                            RelationshipStatus.BLOCKED -> RelationshipStatus.BLOCKING
                            else -> rawStatus // NONE, FRIENDS are symmetrical
                        }
                    } else {
                        rawStatus // Status is already from user1's perspective (which is current user)
                    }
                } catch (e: IllegalArgumentException) {
                    Log.w(TAG, "Invalid relationship status string '$statusString' in doc $docId")
                    RelationshipStatus.NONE // Fallback for invalid status string
                }

                // Determine if the last action was by the current user
                val lastActionByMe = if (isCurrentUserUser1) lastActionByUser1 else !lastActionByUser1
                
                // Create RelationInfo object
                val relationInfo = RelationInfo(
                    id = otherUserId, // Use other user's ID for the RelationInfo ID field
                    status = status,
                    lastActionByMe = lastActionByMe,
                    updatedAt = updatedAtInstant
                )
                
                // Add to result map, keyed by the other user's ID
                result[otherUserId] = relationInfo
            } catch (e: Exception) {
                // Catch potential casting errors or other issues during processing
                Log.e(TAG, "Error processing relationship doc data: ${docData}. Error: ${e.message}", e)
                continue // Skip this document and proceed with others
            }
        }
        
        return result
    }
}
