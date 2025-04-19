package com.androidfinalproject.hacktok.repository

import com.androidfinalproject.hacktok.model.Post
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

interface PostShareRepository {
    suspend fun sharePost(postId: String, caption: String, privacy: String): String
}