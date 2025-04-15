package com.androidfinalproject.hacktok.repository

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun signInWithGoogle(idToken: String): FirebaseUser?
    suspend fun isUserAdmin(userId: String): Boolean
    suspend fun signOut()
    fun getCurrentUser(): FirebaseUser?
} 