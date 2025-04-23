package com.androidfinalproject.hacktok.repository

import com.androidfinalproject.hacktok.model.User

interface UserRepository {
    suspend fun addUser(user: User): String
    suspend fun getUser(userId: String): User?
    suspend fun updateUser(userId: String, updates: Map<String, Any>)
    suspend fun deleteUser(userId: String)
    suspend fun getCurrentUser(): User?
    suspend fun getUserById(userId: String): User?
    suspend fun updateUserProfile(user: User): Boolean
    suspend fun updateUserBio(bio: String): Boolean
    suspend fun updateUserProfileImage(imageUrl: String): Boolean
    suspend fun followUser(userId: String): Boolean
    suspend fun unfollowUser(userId: String): Boolean
    suspend fun searchUsers(query: String): List<User>
    suspend fun updateUserFcmToken(userId: String, token: String?)

    // Search history methods
    suspend fun addSearchQuery(query: String)
    suspend fun getSearchHistory(): List<String>
    suspend fun clearSearchHistory()

    // New methods required by RelationshipRepository
    suspend fun getUsersByIds(userIds: List<String>): List<User>
    suspend fun getAllUsers(): List<User>
}