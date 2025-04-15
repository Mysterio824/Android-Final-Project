# Hacktok Authentication Flow Guide

## Table of Contents
1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Implementation Details](#implementation-details)
4. [Setup Instructions](#setup-instructions)
5. [Troubleshooting](#troubleshooting)

## Overview

The authentication system in Hacktok provides secure user authentication using Google Sign-In and Firebase Authentication. It follows the MVVM architecture pattern and uses Dagger Hilt for dependency injection.

### Key Features
- Google Sign-In integration
- User session management
- Admin role support
- Secure token handling
- Automatic user data creation in Firestore

## Architecture

### Components

1. **Repository Layer**
   - `AuthRepository` (Interface)
   - `AuthRepositoryImpl` (Implementation)

2. **Dependency Injection**
   - `AuthModule` (Dagger Hilt module)
   - `FirebaseModule` (Firebase dependencies)

3. **ViewModel Layer**
   - `AuthViewModel` (Manages authentication state)

4. **UI Layer**
   - `AuthScreen` (Composable UI)
   - `AuthState` (State management)

### Data Flow

1. User initiates Google Sign-In
2. Google returns ID token
3. Token is sent to Firebase Authentication
4. Firebase verifies token and creates/updates user
5. User data is stored/updated in Firestore
6. UI is updated based on authentication result

## Implementation Details

### 1. Repository Interface

```kotlin
interface AuthRepository {
    suspend fun signInWithGoogle(idToken: String): FirebaseUser?
    suspend fun isUserAdmin(userId: String): Boolean
    suspend fun signOut()
    fun getCurrentUser(): FirebaseUser?
}
```

### 2. Repository Implementation

```kotlin
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {
    
    override suspend fun signInWithGoogle(idToken: String): FirebaseUser? {
        Log.d("AuthRepository", "Attempting Firebase sign-in with Google token: ${idToken.take(10)}...")
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            authResult.user?.let {
                Log.d("AuthRepository", "Firebase sign-in successful. User: ${it.uid}")
                checkAndCreateUserData(it)
            }
            authResult.user
        } catch (e: Exception) {
            Log.e("AuthRepository", "Firebase sign-in with Google failed", e)
            null
        }
    }
    
    override suspend fun isUserAdmin(userId: String): Boolean {
        Log.d("AuthRepository", "Checking admin status for user: $userId")
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            document.getBoolean("isAdmin") ?: false
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error checking admin status for $userId", e)
            false
        }
    }
    
    override suspend fun signOut() {
        Log.d("AuthRepository", "Signing out user")
        try {
            firebaseAuth.signOut()
            Log.d("AuthRepository", "User signed out successfully")
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error signing out user", e)
            throw e
        }
    }
    
    override fun getCurrentUser(): FirebaseUser? {
        Log.d("AuthRepository", "Getting current user")
        return firebaseAuth.currentUser
    }
    
    private suspend fun checkAndCreateUserData(user: FirebaseUser) {
        Log.d("AuthRepository", "Checking/Creating user data for: ${user.uid}")
        val userRef = firestore.collection("users").document(user.uid)
        try {
            val document = userRef.get().await()
            if (!document.exists()) {
                val userData = hashMapOf(
                    "uid" to user.uid,
                    "email" to (user.email ?: ""),
                    "displayName" to (user.displayName ?: ""),
                    "photoUrl" to (user.photoUrl?.toString() ?: ""),
                    "isAdmin" to false,
                    "createdAt" to System.currentTimeMillis()
                )
                userRef.set(userData).await()
                Log.d("AuthRepository", "Created new user data for: ${user.uid}")
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error checking/creating user data for ${user.uid}", e)
        }
    }
}
```

### 3. Dependency Injection

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository
}

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
}
```

### 4. ViewModel

```kotlin
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val user = authRepository.signInWithGoogle(idToken)
                if (user != null) {
                    val isAdmin = authRepository.isUserAdmin(user.uid)
                    _authState.value = AuthState.Success(user, isAdmin)
                } else {
                    _authState.value = AuthState.Error("Authentication failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    fun signOut() {
        viewModelScope.launch {
            try {
                authRepository.signOut()
                _authState.value = AuthState.Initial
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Sign out failed")
            }
        }
    }
    
    fun checkAuthState() {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            viewModelScope.launch {
                try {
                    val isAdmin = authRepository.isUserAdmin(currentUser.uid)
                    _authState.value = AuthState.Success(currentUser, isAdmin)
                } catch (e: Exception) {
                    _authState.value = AuthState.Error(e.message ?: "Error checking admin status")
                }
            }
        } else {
            _authState.value = AuthState.Initial
        }
    }
}
```

### 5. State Management

```kotlin
sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    data class Success(val user: FirebaseUser, val isAdmin: Boolean) : AuthState()
    data class Error(val message: String) : AuthState()
}
```

### 6. UI Implementation

```kotlin
@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onAuthSuccess: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.checkAuthState()
    }
    
    when (authState) {
        is AuthState.Loading -> LoadingIndicator()
        is AuthState.Success -> {
            LaunchedEffect(Unit) {
                onAuthSuccess()
            }
        }
        is AuthState.Error -> ErrorMessage((authState as AuthState.Error).message)
        else -> SignInButton(
            onSignInClick = { idToken ->
                viewModel.signInWithGoogle(idToken)
            }
        )
    }
}
```

## Setup Instructions

### 1. Firebase Setup

1. Create a new Firebase project
2. Add your Android application to Firebase
3. Download `google-services.json` and place it in the `app` directory
4. Enable Google Sign-In in Firebase Authentication
5. Set up Firestore Database with the following security rules:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read: if request.auth != null;
      allow write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

### 2. Google Cloud Platform Setup

1. Create a new project
2. Enable Google Sign-In API
3. Create OAuth 2.0 credentials
4. Add SHA-1 and SHA-256 fingerprints to Firebase

### 3. Dependencies

Add the following dependencies to your `build.gradle` file:

```gradle
dependencies {
    // Firebase
    implementation platform('com.google.firebase:firebase-bom:32.7.0')
    implementation 'com.google.firebase:firebase-auth-ktx'
    implementation 'com.google.firebase:firebase-firestore-ktx'
    
    // Google Sign-In
    implementation 'com.google.android.gms:play-services-auth:20.7.0'
    
    // Dagger Hilt
    implementation 'com.google.dagger:hilt-android:2.48'
    kapt 'com.google.dagger:hilt-compiler:2.48'
    
    // Coroutines
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3'
}
```

## Troubleshooting

### Common Issues

1. **Google Sign-In Failures**
   - Check SHA-1 and SHA-256 fingerprints in Firebase console
   - Verify OAuth 2.0 configuration in Google Cloud Console
   - Ensure proper internet connectivity
   - Check if Google Play Services is up to date

2. **Firebase Authentication Issues**
   - Verify `google-services.json` is correctly placed in the app directory
   - Check if Firebase Authentication is enabled in Firebase Console
   - Ensure the package name in Firebase matches your app's package name

3. **Firestore Issues**
   - Check Firestore security rules
   - Verify Firestore is enabled in Firebase Console
   - Check internet connectivity

### Debug Tips

1. **Enable Debug Logging**
   ```kotlin
   Log.d("AuthRepository", "Debug message")
   ```

2. **Check Firebase Console**
   - Monitor authentication attempts
   - Review database operations
   - Check for any error messages

3. **Verify Token Flow**
   - Log the ID token (first few characters only)
   - Check if the token is being properly passed to Firebase
   - Verify the Firebase response 