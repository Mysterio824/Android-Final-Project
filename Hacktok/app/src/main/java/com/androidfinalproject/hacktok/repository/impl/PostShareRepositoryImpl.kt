package com.androidfinalproject.hacktok.repository.impl

import android.util.Log
import com.androidfinalproject.hacktok.model.PostShare
import com.androidfinalproject.hacktok.repository.PostShareRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostShareRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : PostShareRepository {

    private val TAG = "PostShareRepository"
    private val postShareCollection = firestore.collection("postShares")

    override suspend fun sharePost(postId: String, caption: String, privacy: String): String {
        return try {
            val currentUser = auth.currentUser ?: throw Exception("User not authenticated")

            val share = PostShare(
                postId = postId,
                sharedBy = currentUser.uid,
                createdAt = Date(),
                content = caption,
                privacy = privacy
            )

            val docRef = postShareCollection.add(share).await()
            postShareCollection.document(docRef.id).update("id", docRef.id).await()

            Log.d(TAG, "Post shared with ID: ${docRef.id}")
            docRef.id
        } catch (e: Exception) {
            Log.e(TAG, "Failed to share post", e)
            throw e
        }
    }
}
