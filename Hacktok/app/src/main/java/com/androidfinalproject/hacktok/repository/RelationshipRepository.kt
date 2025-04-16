package com.androidfinalproject.hacktok.repository

import com.androidfinalproject.hacktok.model.RelationInfo
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.model.enums.RelationshipStatus
import kotlinx.coroutines.flow.Flow

interface RelationshipRepository {
    // Get relationships for a user
    suspend fun getRelationshipsForUser(userId: String): Map<String, RelationInfo>
    
    // Get friends for a user
    suspend fun getFriendsForUser(userId: String): List<User>
    
    // Get users with friend requests
    suspend fun getFriendRequestsForUser(userId: String): List<User>
    
    // Get friend suggestions
    suspend fun getFriendSuggestions(userId: String, limit: Int = 10): List<User>
    
    // Send a friend request
    suspend fun sendFriendRequest(fromUserId: String, toUserId: String): Boolean
    
    // Cancel a friend request
    suspend fun cancelFriendRequest(fromUserId: String, toUserId: String): Boolean
    
    // Accept a friend request
    suspend fun acceptFriendRequest(fromUserId: String, toUserId: String): Boolean
    
    // Decline a friend request
    suspend fun declineFriendRequest(fromUserId: String, toUserId: String): Boolean
    
    // Block a user
    suspend fun blockUser(fromUserId: String, toUserId: String): Boolean
    
    // Unblock a user
    suspend fun unblockUser(fromUserId: String, toUserId: String): Boolean
    
    // Get relationship status
    suspend fun getRelationshipStatus(userId1: String, userId2: String): RelationshipStatus
    
    // Remove from suggestions
    suspend fun removeFromSuggestions(userId: String, suggestionId: String): Boolean
    
    // Stream of relationship changes
    fun observeRelationships(userId: String): Flow<Map<String, RelationInfo>>
} 