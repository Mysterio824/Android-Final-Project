package com.androidfinalproject.hacktok.repository

import com.androidfinalproject.hacktok.model.RelationInfo
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing relationship data in the database
 */
interface RelationshipRepository {
    
    /**
     * Get a single relationship document by ID
     */
    suspend fun getRelationship(relationshipId: String): Map<String, Any>?
    
    /**
     * Get all relationship documents for a user
     */
    suspend fun getRelationshipDocs(userId: String): List<Map<String, Any>>
    
    /**
     * Save a relationship document
     */
    suspend fun saveRelationship(relationshipId: String, data: Map<String, Any>): Boolean
    
    /**
     * Delete a relationship document
     */
    suspend fun deleteRelationship(relationshipId: String): Boolean
    
    /**
     * Get list of hidden suggestion user IDs
     */
    suspend fun getHiddenSuggestions(userId: String): List<String>
    
    /**
     * Update the list of hidden suggestion user IDs
     */
    suspend fun updateHiddenSuggestions(userId: String, hiddenUserIds: List<String>): Boolean
    
    /**
     * Observe changes to relationships for a user
     */
    fun observeRelationships(userId: String): Flow<List<Map<String, Any>>>
} 