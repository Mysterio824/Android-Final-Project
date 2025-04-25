package com.androidfinalproject.hacktok.repository.impl

import android.util.Log
import com.androidfinalproject.hacktok.model.SecretCrush
import com.androidfinalproject.hacktok.model.enums.NotificationType
import com.androidfinalproject.hacktok.repository.SecretCrushRepository
import com.androidfinalproject.hacktok.service.FcmService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Singleton
class SecretCrushRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val fcmService: FcmService
) : SecretCrushRepository {

    private val TAG = "SecretCrushRepository"
    private val crushesCollection = firestore.collection("secret_crushes")
    private val usersCollection = firestore.collection("users")
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun observeMySecretCrushes(): Flow<Result<List<SecretCrush>>> = callbackFlow {
        val currentUserId = auth.currentUser?.uid ?: run {
            Log.e(TAG, "User not authenticated")
            trySend(Result.failure(IllegalStateException("User not authenticated")))
            close()
            return@callbackFlow
        }

        Log.d(TAG, "Observing my secret crushes for user: $currentUserId")

        val registration = crushesCollection
            .whereEqualTo("senderId", currentUserId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error getting my secret crushes", error)
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    Log.d(TAG, "Got ${snapshot.documents.size} crushes")
                    val crushes = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(SecretCrush::class.java)?.copy(id = doc.id)
                    }
                    trySend(Result.success(crushes))
                }
            }

        awaitClose { registration.remove() }
    }

    override fun observeReceivedSecretCrushes(): Flow<Result<List<SecretCrush>>> = callbackFlow {
        val currentUserId = auth.currentUser?.uid ?: run {
            Log.e(TAG, "User not authenticated")
            trySend(Result.failure(IllegalStateException("User not authenticated")))
            close()
            return@callbackFlow
        }

        Log.d(TAG, "Observing received secret crushes for user: $currentUserId")

        val registration = crushesCollection
            .whereEqualTo("receiverId", currentUserId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error getting received secret crushes", error)
                    if (error.message?.contains("FAILED_PRECONDITION") == true) {
                        trySend(Result.failure(IllegalStateException(
                            "Please create the required Firestore index. " +
                            "Click the link in the logs to create it automatically."
                        )))
                    } else {
                        trySend(Result.failure(error))
                    }
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    Log.d(TAG, "Got ${snapshot.documents.size} received crushes")
                    val crushes = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(SecretCrush::class.java)?.copy(id = doc.id)
                    }
                    trySend(Result.success(crushes))
                }
            }

        awaitClose { registration.remove() }
    }

    override fun sendSecretCrush(crushId: String): Flow<Result<Unit>> = callbackFlow {
        val currentUserId = auth.currentUser?.uid ?: run {
            trySend(Result.failure(IllegalStateException("User not authenticated")))
            close()
            return@callbackFlow
        }

        // Check if user already has a crush on this person
        crushesCollection
            .whereEqualTo("senderId", currentUserId)
            .whereEqualTo("receiverId", crushId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    trySend(Result.failure(IllegalStateException("You already have a crush on this person")))
                    return@addOnSuccessListener
                }

                // Get current user data
                usersCollection.document(currentUserId).get()
                    .addOnSuccessListener { senderDoc ->
                        val senderName = senderDoc.getString("username") ?: ""
                        val senderImageUrl = senderDoc.getString("profileImage") ?: ""

                        // Get crush user data
                        usersCollection.document(crushId).get()
                            .addOnSuccessListener { receiverDoc ->
                                val receiverName = receiverDoc.getString("username") ?: ""
                                val receiverImageUrl = receiverDoc.getString("profileImage") ?: ""

                                // Check if the other person also has a crush on the current user
                                crushesCollection
                                    .whereEqualTo("senderId", crushId)
                                    .whereEqualTo("receiverId", currentUserId)
                                    .get()
                                    .addOnSuccessListener { reciprocalDocs ->
                                        val isMatch = !reciprocalDocs.isEmpty

                                        // Create the new crush
                                        val secretCrush = SecretCrush(
                                            senderId = currentUserId,
                                            senderName = senderName,
                                            senderImageUrl = senderImageUrl,
                                            receiverId = crushId,
                                            receiverName = receiverName,
                                            receiverImageUrl = receiverImageUrl,
                                            isMatch = isMatch,
                                            createdAt = Date()
                                        )

                                        // Add to Firestore
                                        crushesCollection.add(secretCrush)
                                            .addOnSuccessListener {
                                                // If it's a match, update the other person's crush
                                                if (isMatch) {
                                                    reciprocalDocs.documents.firstOrNull()?.reference?.update(
                                                        "isMatch", true
                                                    )
                                                }

                                                // Send notification to the receiver
                                                serviceScope.launch {
                                                    fcmService.sendInteractionNotification(
                                                        recipientUserId = crushId,
                                                        senderUserId = currentUserId,
                                                        notificationType = NotificationType.SECRET_CRUSH,
                                                        itemId = it.id
                                                    )
                                                }

                                                trySend(Result.success(Unit))
                                            }
                                            .addOnFailureListener { e ->
                                                Log.e(TAG, "Error sending secret crush", e)
                                                trySend(Result.failure(e))
                                            }
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e(TAG, "Error checking for reciprocal crush", e)
                                        trySend(Result.failure(e))
                                    }
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error getting receiver data", e)
                                trySend(Result.failure(e))
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error getting sender data", e)
                        trySend(Result.failure(e))
                    }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error checking existing crush", e)
                trySend(Result.failure(e))
            }

        awaitClose { }
    }

    override fun revealSecretCrush(crushId: String): Flow<Result<Unit>> = callbackFlow {
        val currentUserId = auth.currentUser?.uid ?: run {
            trySend(Result.failure(IllegalStateException("User not authenticated")))
            close()
            return@callbackFlow
        }

        crushesCollection.document(crushId).get()
            .addOnSuccessListener { document ->
                val crush = document.toObject(SecretCrush::class.java)

                if (crush == null) {
                    trySend(Result.failure(IllegalStateException("Crush not found")))
                    return@addOnSuccessListener
                }

                // Verify the current user is the sender
                if (crush.senderId != currentUserId) {
                    trySend(Result.failure(IllegalStateException("You can only reveal your own crushes")))
                    return@addOnSuccessListener
                }

                // Update the crush to be revealed
                crushesCollection.document(crushId)
                    .update(
                        mapOf(
                            "isRevealed" to true,
                            "revealedAt" to Date()
                        )
                    )
                    .addOnSuccessListener {
                        trySend(Result.success(Unit))
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Error revealing crush", e)
                        trySend(Result.failure(e))
                    }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error getting crush document", e)
                trySend(Result.failure(e))
            }

        awaitClose { }
    }

    override fun deleteSecretCrush(crushId: String): Flow<Result<Unit>> = callbackFlow {
        val currentUserId = auth.currentUser?.uid ?: run {
            Log.e(TAG, "User not authenticated while trying to delete crush: $crushId")
            trySend(Result.failure(IllegalStateException("User not authenticated")))
            close()
            return@callbackFlow
        }

        Log.d(TAG, "Starting delete operation for crush: $crushId by user: $currentUserId")

        crushesCollection.document(crushId).get()
            .addOnSuccessListener { document ->
                val crush = document.toObject(SecretCrush::class.java)

                if (crush == null) {
                    Log.e(TAG, "Crush document not found with ID: $crushId")
                    trySend(Result.failure(IllegalStateException("Crush not found")))
                    return@addOnSuccessListener
                }

                // Verify the current user is the sender
                if (crush.senderId != currentUserId) {
                    Log.e(TAG, "User $currentUserId attempted to delete crush owned by ${crush.senderId}")
                    trySend(Result.failure(IllegalStateException("You can only delete your own crushes")))
                    return@addOnSuccessListener
                }

                Log.d(TAG, "Attempting to delete crush document: $crushId")
                // Delete the crush
                crushesCollection.document(crushId)
                    .delete()
                    .addOnSuccessListener {
                        Log.d(TAG, "Successfully deleted crush document: $crushId")
                        // If it was a match, update the other person's crush to no longer be a match
                        if (crush.isMatch) {
                            Log.d(TAG, "Updating match status for reciprocal crush")
                            crushesCollection
                                .whereEqualTo("senderId", crush.receiverId)
                                .whereEqualTo("receiverId", crush.senderId)
                                .get()
                                .addOnSuccessListener { documents ->
                                    documents.documents.firstOrNull()?.reference?.update(
                                        "isMatch", false
                                    )
                                    Log.d(TAG, "Updated match status for reciprocal crush")
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Failed to update match status for reciprocal crush", e)
                                }
                        }
                        trySend(Result.success(Unit))
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Failed to delete crush document: $crushId", e)
                        trySend(Result.failure(e))
                    }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to get crush document: $crushId", e)
                trySend(Result.failure(e))
            }

        awaitClose { }
    }
}