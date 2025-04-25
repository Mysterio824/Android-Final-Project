package com.androidfinalproject.hacktok.repository

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun signInWithGoogle(idToken: String): FirebaseUser?
    suspend fun signInWithEmail(email: String, password: String): FirebaseUser?
    suspend fun signOut()
    fun getCurrentUser(): FirebaseUser?
} 