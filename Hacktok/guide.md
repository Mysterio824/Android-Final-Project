# Hacktok - Android Social Media Application Guide

## Table of Contents
1. [Project Overview](#project-overview)
2. [Setup Instructions](#setup-instructions)
3. [Architecture](#architecture)
4. [Features](#features)
5. [Technical Implementation](#technical-implementation)
6. [Firebase Integration](#firebase-integration)
7. [Testing](#testing)
8. [Troubleshooting](#troubleshooting)

## Project Overview

Hacktok is a modern social media application built for Android using Kotlin. It features a TikTok-like interface with video sharing capabilities, user authentication, and social interactions.

### Key Features
- Google Sign-In Authentication
- Video Upload and Playback
- User Profiles
- Social Interactions (Likes, Comments, Shares)
- Admin Dashboard
- Real-time Updates

## Setup Instructions

### Prerequisites
- Android Studio Arctic Fox or newer
- JDK 11 or newer
- Google Cloud Platform account
- Firebase account

### Installation Steps

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/Hacktok.git
   cd Hacktok
   ```

2. **Firebase Setup**
   - Create a new Firebase project
   - Add your Android application to Firebase
   - Download `google-services.json` and place it in the `app` directory
   - Enable Google Sign-In in Firebase Authentication
   - Set up Firestore Database

3. **Google Cloud Platform Setup**
   - Create a new project
   - Enable YouTube Data API
   - Create OAuth 2.0 credentials
   - Add SHA-1 and SHA-256 fingerprints to Firebase

4. **Build and Run**
   - Open the project in Android Studio
   - Sync Gradle files
   - Run the application on an emulator or physical device

## Architecture

### MVVM Architecture
The application follows the Model-View-ViewModel (MVVM) architecture pattern:

```
app/
├── data/
│   ├── repository/
│   └── models/
├── di/
│   ├── AuthModule.kt
│   └── FirebaseModule.kt
├── ui/
│   ├── auth/
│   ├── home/
│   ├── profile/
│   └── upload/
└── utils/
```

### Key Components

1. **Repository Layer**
   - `AuthRepository`: Handles authentication operations
   - `VideoRepository`: Manages video-related operations
   - `UserRepository`: Handles user data operations

2. **ViewModel Layer**
   - `AuthViewModel`: Manages authentication state
   - `HomeViewModel`: Handles home screen logic
   - `ProfileViewModel`: Manages profile screen state

3. **UI Layer**
   - Composable functions for modern UI
   - Material Design 3 components
   - Custom animations and transitions

## Features

### Authentication
- Google Sign-In integration
- User session management
- Admin role support
- Secure token handling

### Video Features
- Video upload and compression
- Video playback with custom controls
- Video feed with infinite scroll
- Video sharing capabilities

### Social Features
- Like and comment on videos
- Follow/unfollow users
- Share videos to other platforms
- Real-time notifications

### Profile Management
- Custom user profiles
- Profile editing
- Video management
- Analytics dashboard

## Technical Implementation

### Dependency Injection
Using Dagger Hilt for dependency injection:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository
}
```

### Repository Pattern
Example of repository implementation:

```kotlin
interface AuthRepository {
    suspend fun signInWithGoogle(idToken: String): FirebaseUser?
    suspend fun isUserAdmin(userId: String): Boolean
    suspend fun signOut()
    fun getCurrentUser(): FirebaseUser?
}
```

### ViewModel Implementation
Example of ViewModel with state management:

```kotlin
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
}
```

### UI Implementation
Example of Composable function:

```kotlin
@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onAuthSuccess: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    
    when (authState) {
        is AuthState.Loading -> LoadingIndicator()
        is AuthState.Success -> onAuthSuccess()
        is AuthState.Error -> ErrorMessage(authState.message)
        else -> SignInButton()
    }
}
```

## Firebase Integration

### Authentication
- Google Sign-In configuration
- User session management
- Token refresh handling

### Firestore
Database structure:
```
users/
  ├── userId/
  │   ├── uid: string
  │   ├── email: string
  │   ├── displayName: string
  │   ├── photoUrl: string
  │   ├── isAdmin: boolean
  │   └── createdAt: timestamp
  └── ...
```

### Storage
- Video storage configuration
- File naming conventions
- Access control rules

## Testing

### Unit Tests
- Repository tests
- ViewModel tests
- Utility function tests

### UI Tests
- Composable function tests
- Navigation tests
- State management tests

### Firebase Tests
- Authentication tests
- Database operation tests
- Storage operation tests

## Troubleshooting

### Common Issues

1. **Google Sign-In Failures**
   - Check SHA-1 and SHA-256 fingerprints
   - Verify OAuth 2.0 configuration
   - Ensure proper internet connectivity

2. **Video Upload Issues**
   - Check file size limits
   - Verify storage permissions
   - Ensure proper video format

3. **Firebase Connection Issues**
   - Verify internet connectivity
   - Check Firebase configuration
   - Ensure proper initialization

### Debug Tips

1. **Enable Debug Logging**
   ```kotlin
   Log.d("AuthRepository", "Debug message")
   ```

2. **Check Firebase Console**
   - Monitor authentication attempts
   - Review database operations
   - Check storage usage

3. **Use Android Studio Profiler**
   - Monitor memory usage
   - Track network calls
   - Analyze UI performance

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contact

For any questions or support, please contact:
- Email: your.email@example.com
- GitHub: [your-username](https://github.com/your-username)

# Authentication Flow in Hacktok

## Authentication Flow Overview

The authentication flow in Hacktok follows a clean, well-structured approach using Google Sign-In with Firebase Authentication. Here's a detailed breakdown of the entire authentication system:

### 1. User Authentication Flow

1. **User Initiates Sign-In**
   - User taps the Google Sign-In button in the UI
   - The app launches the Google Sign-In client

2. **Google Authentication**
   - Google Sign-In client handles the OAuth flow
   - User selects their Google account
   - Google returns an ID token to the app

3. **Firebase Authentication**
   - The app sends the ID token to Firebase Authentication
   - Firebase verifies the token and creates/updates the user account
   - Firebase returns a `FirebaseUser` object

4. **User Data Creation**
   - The app checks if the user exists in Firestore
   - If not, it creates a new user document with default values
   - User data includes: uid, email, displayName, photoUrl, isAdmin status

5. **Session Management**
   - Firebase maintains the authentication state
   - The app can check the current user at any time
   - The app can sign out the user when needed

### 2. Repository Layer

The repository layer abstracts the authentication logic from the rest of the app:

```kotlin
interface AuthRepository {
    suspend fun signInWithGoogle(idToken: String): FirebaseUser?
    suspend fun isUserAdmin(userId: String): Boolean
    suspend fun signOut()
    fun getCurrentUser(): FirebaseUser?
}
```

This interface defines four key operations:
- `signInWithGoogle`: Authenticates a user with Google
- `isUserAdmin`: Checks if a user has admin privileges
- `signOut`: Signs out the current user
- `getCurrentUser`: Gets the currently authenticated user

The implementation (`AuthRepositoryImpl`) handles:
- Firebase Authentication operations
- Firestore database operations
- Error handling and logging
- User data creation and management

### 3. Dependency Injection

Dagger Hilt is used for dependency injection, making the authentication system modular and testable:

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

This setup:
- Binds the repository implementation to its interface
- Provides Firebase services as singletons
- Makes dependencies available throughout the app

### 4. ViewModel Layer

The ViewModel manages the authentication state and user interactions:

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

The ViewModel:
- Manages authentication state using `StateFlow`
- Handles sign-in, sign-out, and state checking
- Provides error handling
- Uses coroutines for asynchronous operations

### 5. State Management

The authentication state is represented by a sealed class:

```kotlin
sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    data class Success(val user: FirebaseUser, val isAdmin: Boolean) : AuthState()
    data class Error(val message: String) : AuthState()
}
```

This state class:
- Represents the initial state before authentication
- Shows loading state during authentication
- Represents successful authentication with user and admin status
- Represents error state with error message

### 6. UI Layer

The UI layer uses Jetpack Compose to render the authentication screen:

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

The UI:
- Observes the authentication state
- Shows appropriate UI based on the state
- Handles user interactions
- Navigates to the main app on successful authentication

## Authentication Flow Diagram

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   User UI   │────▶│ AuthViewModel│────▶│ AuthRepository│────▶│  Firebase   │
└─────────────┘     └─────────────┘     └─────────────┘     └─────────────┘
       ▲                   ▲                   ▲                   ▲
       │                   │                   │                   │
       ▼                   ▼                   ▼                   ▼
┌─────────────┐     ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  AuthState  │◀────│  Repository │◀────│  Firebase   │◀────│  Firestore  │
└─────────────┘     │  Interface  │     │  Auth       │     │  Database   │
                    └─────────────┘     └─────────────┘     └─────────────┘
```

## Key Authentication Components

### 1. Google Sign-In Process

```kotlin
// In AuthRepositoryImpl
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
```

This method:
1. Takes a Google ID token
2. Creates a Firebase credential
3. Signs in to Firebase with the credential
4. Creates/updates user data in Firestore
5. Returns the Firebase user or null on failure

### 2. User Data Management

```kotlin
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
```

This method:
1. Checks if the user exists in Firestore
2. If not, creates a new user document
3. Stores user information and sets default values
4. Handles errors gracefully

### 3. Admin Status Check

```kotlin
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
```

This method:
1. Retrieves the user document from Firestore
2. Checks the `isAdmin` field
3. Returns true if the user is an admin, false otherwise
4. Handles errors by defaulting to false

### 4. Sign Out Process

```kotlin
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
```

This method:
1. Signs out the user from Firebase
2. Logs the operation
3. Throws any exceptions for proper error handling

## Authentication State Management

The authentication state is managed through a sealed class and StateFlow:

```kotlin
sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    data class Success(val user: FirebaseUser, val isAdmin: Boolean) : AuthState()
    data class Error(val message: String) : AuthState()
}
```

The ViewModel updates this state based on authentication operations:

```kotlin
// Sign-in
_authState.value = AuthState.Loading
// ... authentication process ...
_authState.value = AuthState.Success(user, isAdmin)

// Sign-out
_authState.value = AuthState.Initial

// Error
_authState.value = AuthState.Error(errorMessage)
```

The UI observes this state and updates accordingly:

```kotlin
when (authState) {
    is AuthState.Loading -> LoadingIndicator()
    is AuthState.Success -> { /* Navigate to main app */ }
    is AuthState.Error -> ErrorMessage((authState as AuthState.Error).message)
    else -> SignInButton()
}
```

## Conclusion

The authentication system in Hacktok follows a clean, modular architecture that separates concerns and makes the code maintainable and testable. It uses:

1. **Repository Pattern** to abstract data sources
2. **Dependency Injection** to provide dependencies
3. **ViewModel** to manage UI state
4. **StateFlow** for reactive state management
5. **Jetpack Compose** for modern UI
6. **Firebase Authentication** for secure user authentication
7. **Firestore** for user data storage

This architecture ensures that the authentication system is robust, secure, and easy to maintain. 