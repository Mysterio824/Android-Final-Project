package com.androidfinalproject.hacktok.repository.impl

import android.util.Log
import com.androidfinalproject.hacktok.model.User
import com.androidfinalproject.hacktok.model.enums.UserRole
import com.androidfinalproject.hacktok.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.QuerySnapshot
import java.util.Calendar
import java.util.Date
import java.util.LinkedList

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : UserRepository {
    private val TAG = "UserRepository"
    private val usersCollection = firestore.collection("users")
    
    // Search history - store in memory
    private val searchHistoryLimit = 10
    private val searchHistory = LinkedList<String>()

    override suspend fun addUser(user: User): String {
        val documentRef = usersCollection.add(user).await()
        usersCollection.document(documentRef.id).update("id", documentRef.id).await()
        return documentRef.id
    }

    override suspend fun getUser(userId: String): User? {
        val snapshot = usersCollection.document(userId).get().await()
        return snapshot.toObject(User::class.java)
    }

    override suspend fun updateUser(userId: String, updates: Map<String, Any>) {
        usersCollection.document(userId).update(updates).await()
    }

    override suspend fun deleteUser(userId: String) {
        usersCollection.document(userId).delete().await()
    }

    override suspend fun getCurrentUser(): User? {
        return try {
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                val userDoc = usersCollection.document(currentUser.uid).get().await()
                if (userDoc.exists()) {
                    userDoc.toObject(User::class.java)
                } else {
                    // Create new user document if it doesn't exist
                    val newUser = User.fromFirebaseUser(currentUser)
                    usersCollection.document(currentUser.uid).set(newUser).await()
                    newUser
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting current user", e)
            null
        }
    }

    override suspend fun getUserById(userId: String): User? {
        return try {
            val userDoc = usersCollection.document(userId).get().await()
            if (userDoc.exists()) {
                userDoc.toObject(User::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user by ID: $userId", e)
            null
        }
    }

    override suspend fun updateUserProfile(user: User): Boolean {
        return try {
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                usersCollection.document(currentUser.uid).set(user).await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user profile", e)
            false
        }
    }

    override suspend fun updateUserBio(bio: String): Boolean {
        return try {
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                usersCollection.document(currentUser.uid)
                    .update("bio", bio)
                    .await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user bio", e)
            false
        }
    }

    override suspend fun updateUserProfileImage(imageUrl: String): Boolean {
        return try {
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                usersCollection.document(currentUser.uid)
                    .update("profileImage", imageUrl)
                    .await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user profile image", e)
            false
        }
    }

    override suspend fun followUser(userId: String): Boolean {
        return try {
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                // Add to current user's following list
                usersCollection.document(currentUser.uid)
                    .update(
                        "following", FieldValue.arrayUnion(userId),
                        "followingCount", FieldValue.increment(1)
                    )
                    .await()

                // Add to target user's followers list
                usersCollection.document(userId)
                    .update(
                        "followers", FieldValue.arrayUnion(currentUser.uid),
                        "followerCount", FieldValue.increment(1)
                    )
                    .await()

                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error following user: $userId", e)
            false
        }
    }

    override suspend fun unfollowUser(userId: String): Boolean {
        return try {
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                // Remove from current user's following list
                usersCollection.document(currentUser.uid)
                    .update(
                        "following", FieldValue.arrayRemove(userId),
                        "followingCount", FieldValue.increment(-1)
                    )
                    .await()

                // Remove from target user's followers list
                usersCollection.document(userId)
                    .update(
                        "followers", FieldValue.arrayRemove(currentUser.uid),
                        "followerCount", FieldValue.increment(-1)
                    )
                    .await()

                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error unfollowing user: $userId", e)
            false
        }
    }

    // Helper function to map QuerySnapshot to List<User> with ID
    private fun mapSnapshotToUsers(snapshot: QuerySnapshot): List<User> {
        return snapshot.documents.mapNotNull { document ->
            try {
                // Map document data to User object
                val user = document.toObject(User::class.java)
                // Explicitly set the ID from the document ID
                user?.copy(id = document.id)
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing user document ${document.id}", e)
                null
            }
        }
    }

    override suspend fun searchUsers(query: String): List<User> {
        return try {
            val lowercaseQuery = query.lowercase()
            val currentUser = getCurrentUser()
                ?: return emptyList()
            // Get all users first
            val allUsers = getAllUsers()
            
            // Filter users in memory
            allUsers.filter { user ->
                (
                    user.username?.lowercase()?.contains(lowercaseQuery) == true ||
                    user.email.lowercase().contains(lowercaseQuery) ||
                    user.fullName?.lowercase()?.contains(lowercaseQuery) == true ||
                    user.bio?.lowercase()?.contains(lowercaseQuery) == true
                ) &&
                user.id != currentUser.id &&
                user.role != UserRole.ADMIN &&
                user.role != UserRole.MODERATOR
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error searching users with query: $query", e)
            emptyList()
        }
    }

    override suspend fun getUsersByIds(userIds: List<String>): List<User> {
        return try {
            if (userIds.isEmpty()) {
                return emptyList()
            }
            
            // Firestore has a limit of 10 items for 'in' queries
            // Split the list into chunks of 10 and query each chunk
            val users = mutableListOf<User>()
            
            userIds.chunked(10).forEach { chunk ->
                val snapshots = usersCollection
                    .whereIn("id", chunk)
                    .get()
                    .await()
                
                users.addAll(mapSnapshotToUsers(snapshots))
            }
            
            return users
        } catch (e: Exception) {
            Log.e(TAG, "Error getting users by IDs: $userIds", e)
            emptyList()
        }
    }
    
    override suspend fun getAllUsers(): List<User> {
        return try {
            val snapshot = usersCollection
                .limit(100) // Limit to prevent loading too many users
                .get()
                .await()
            
            mapSnapshotToUsers(snapshot)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting all users", e)
            emptyList()
        }
    }

    override suspend fun updateUserFcmToken(userId: String, token: String?) {
        try {
            val updates = mapOf("fcmToken" to token) // Field name in Firestore
            usersCollection.document(userId).update(updates).await()
            Log.d("UserRepository", "FCM token updated for user $userId")
        } catch (e: Exception) {
            // Handle error appropriately (log, throw custom exception, etc.)
            Log.e("UserRepository", "Error updating FCM token for user $userId", e)
            throw Exception("Failed to update FCM token: ${e.message}", e)
        }
    }

    // Search history implementation
    override suspend fun addSearchQuery(query: String) {
        try {
            // Remove if already exists to avoid duplicates
            searchHistory.remove(query)

            // Add to the front of the list
            searchHistory.addFirst(query)

            // Keep only the most recent queries
            while (searchHistory.size > searchHistoryLimit) {
                searchHistory.removeLast()
            }

            // Store in Firestore for persistence across app restarts
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                try {
                    // First check if user document exists
                    val userDoc = usersCollection.document(currentUser.uid).get().await()
                    if (userDoc.exists()) {
                        usersCollection.document(currentUser.uid)
                            .update("searchHistory", searchHistory.toList())
                            .await()
                    } else {
                        Log.d(TAG, "User document not found, cannot save search history")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error saving search history", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in addSearchQuery", e)
        }
    }

    override suspend fun getSearchHistory(): List<String> {
        try {
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                try {
                    val userDoc = usersCollection.document(currentUser.uid).get().await()
                    if (userDoc.exists()) {
                        val user = userDoc.toObject(User::class.java)

                        @Suppress("UNCHECKED_CAST")
                        val storedHistory = user?.searchHistory

                        if (storedHistory != null && storedHistory.isNotEmpty()) {
                            // Update local cache
                            searchHistory.clear()
                            searchHistory.addAll(storedHistory)

                            Log.d(TAG, "Retrieved search history: ${storedHistory.joinToString()}")
                        } else {
                            Log.d(TAG, "No search history found in user document")
                        }
                    } else {
                        Log.d(TAG, "User document not found, returning empty search history")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error loading search history from Firestore", e)
                }
            } else {
                Log.d(TAG, "No current user, returning empty search history")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in getSearchHistory", e)
        }

        return searchHistory.toList()
    }

    override suspend fun clearSearchHistory() {
        searchHistory.clear()

        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            try {
                usersCollection.document(currentUser.uid)
                    .update("searchHistory", emptyList<String>())
                    .await()
            } catch (e: Exception) {
                Log.e(TAG, "Error clearing search history", e)
            }
        }
    }

    override suspend fun banUser(userId: String, reason: String, duration: Long) {
        try {
            val user = getUserById(userId)
            if (user == null) {
                Log.e(TAG, "Cannot ban user: User $userId not found")
                return
            }

            val startDate = Date()
            val endDate = if (duration == Long.MAX_VALUE) {
                null // Permanent ban
            } else {
                Calendar.getInstance().apply {
                    time = startDate
                    add(Calendar.DAY_OF_YEAR, duration.toInt())
                }.time
            }

            val banInfo = mapOf(
                "isBanned" to true,
                "reason" to reason,
                "startDate" to startDate,
                "endDate" to endDate
            )

            updateUser(userId, mapOf("banInfo" to banInfo))
            Log.d(TAG, "User $userId banned successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error banning user $userId", e)
        }
    }
}