package com.androidfinalproject.hacktok.repository.impl

import android.util.Log
import com.androidfinalproject.hacktok.model.Story
import com.androidfinalproject.hacktok.repository.StoryRepository
import com.androidfinalproject.hacktok.ui.newPost.PRIVACY
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoryRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : StoryRepository {
    private val TAG = "StoryRepository"
    private val storiesCollection = firestore.collection("stories")

    override suspend fun addStory(story: Story): String {
        val documentRef = storiesCollection.add(story).await()
        storiesCollection.document(documentRef.id).update("id", documentRef.id).await()
        return documentRef.id
    }

    override suspend fun getStory(storyId: String): Story? {
        val snapshot = storiesCollection.document(storyId).get().await()
        return snapshot.toObject(Story::class.java)
    }

    override suspend fun getStoriesByUser(userId: String): List<Story> {
        val snapshot = storiesCollection.whereEqualTo("userId", userId).get().await()
        return snapshot.toObjects(Story::class.java)
    }

    override suspend fun getActiveStories(excludeExpired: Boolean): Flow<List<Story>> = callbackFlow {
        val queryRef = if (excludeExpired) {
            storiesCollection
                .whereGreaterThan("expiresAt", Date())
                .orderBy("expiresAt")
                .orderBy("createdAt", Query.Direction.DESCENDING)
        } else {
            storiesCollection
                .orderBy("createdAt", Query.Direction.DESCENDING)
        }

        val registration = queryRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e(TAG, "Error getting stories", error)
                trySend(emptyList())
                return@addSnapshotListener
            }

            val stories = snapshot?.toObjects(Story::class.java) ?: emptyList()
            trySend(stories)
        }

        awaitClose { registration.remove() }
    }

    override suspend fun getStoriesByPrivacy(privacy: PRIVACY): List<Story> {
        val snapshot = storiesCollection
            .whereEqualTo("privacy", privacy)
            .whereGreaterThan("expiresAt", Date())
            .get()
            .await()
        return snapshot.toObjects(Story::class.java)
    }

    override suspend fun updateStory(storyId: String, updates: Map<String, Any>) {
        storiesCollection.document(storyId).update(updates).await()
    }

    override suspend fun deleteStory(storyId: String) {
        storiesCollection.document(storyId).delete().await()
    }

    override suspend fun addViewer(storyId: String, viewerId: String) {
        val storyRef = storiesCollection.document(storyId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(storyRef)
            val story = snapshot.toObject(Story::class.java)

            if (story != null && !story.viewerIds.contains(viewerId)) {
                val updatedViewers = story.viewerIds + viewerId
                transaction.update(storyRef, "viewerIds", updatedViewers)
            }
        }.await()
    }

    override suspend fun observeUserStories(userId: String): Flow<List<Story>> = callbackFlow {
        val listenerRegistration = storiesCollection
            .whereEqualTo("userId", userId)
            .whereGreaterThan("expiresAt", Date())
            .orderBy("expiresAt")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error observing user stories", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val stories = snapshot?.toObjects(Story::class.java) ?: emptyList()
                trySend(stories)
            }

        awaitClose { listenerRegistration.remove() }
    }

    override suspend fun observeActiveStories(userId: String?): Flow<Result<List<Story>>> = callbackFlow {
        val query = if (userId != null) {
            storiesCollection
                .whereEqualTo("userId", userId)
                .whereGreaterThan("expiresAt", Date())
        } else {
            storiesCollection
                .whereGreaterThan("expiresAt", Date())
        }

        val listenerRegistration = query
            .orderBy("expiresAt")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error observing active stories", error)
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }

                try {
                    val stories = snapshot?.toObjects(Story::class.java) ?: emptyList()
                    trySend(Result.success(stories))
                } catch (e: Exception) {
                    Log.e(TAG, "Error converting stories", e)
                    trySend(Result.failure(e))
                }
            }

        awaitClose { listenerRegistration.remove() }
    }
}