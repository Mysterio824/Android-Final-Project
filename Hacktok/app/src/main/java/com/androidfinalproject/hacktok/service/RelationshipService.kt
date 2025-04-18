package com.androidfinalproject.hacktok.service

import com.androidfinalproject.hacktok.model.RelationInfo
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.model.enums.RelationshipStatus
import kotlinx.coroutines.flow.Flow

/**
 * Service to handle relationship operations in the context of the current user
 */
interface RelationshipService {
    // Get relationships for the current user
    suspend fun getMyRelationships(): Map<String, RelationInfo>

    // Get user's relation ship
    suspend fun getRelationships(userId: String): Map<String, RelationInfo>

    suspend fun getFriends(userId: String): Map<String, RelationInfo>

    // Get relationship
    suspend fun getRelationship(userId: String): RelationInfo?

    // Get friends for the current user
    suspend fun getMyFriends(): List<User>

    // Get friend requests for the current user
    suspend fun getMyFriendRequests(): List<User>

    suspend fun getUserFromRelationship(relations: Map<String, RelationInfo>): List<User>

    // Get friend suggestions for the current user
    suspend fun getFriendSuggestions(limit: Int = 10): List<User>
    
    // Send a friend request from the current user
    suspend fun sendFriendRequest(toUserId: String): Boolean
    
    // Cancel a friend request from the current user
    suspend fun cancelFriendRequest(toUserId: String): Boolean
    
    // Accept a friend request to the current user
    suspend fun acceptFriendRequest(fromUserId: String): Boolean
    
    // Decline a friend request to the current user
    suspend fun declineFriendRequest(fromUserId: String): Boolean
    
    // Block a user from the current user
    suspend fun blockUser(userId: String): Boolean
    
    // Unblock a user from the current user
    suspend fun unblockUser(userId: String): Boolean
    
    // Get relationship status between current user and another user
    suspend fun getRelationshipStatus(userId: String): RelationshipStatus
    
    // Remove a user from suggestions for the current user
    suspend fun removeFromSuggestions(suggestionId: String): Boolean

    // Stream of relationship changes for the current user
    fun observeMyRelationships(): Flow<Map<String, RelationInfo>>
} 