package com.androidfinalproject.hacktok

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.androidfinalproject.hacktok.router.graph.adminNavigation
import com.androidfinalproject.hacktok.router.graph.authNavigation
import com.androidfinalproject.hacktok.router.graph.mainNavigation
import com.androidfinalproject.hacktok.router.graph.testNavigation
import com.androidfinalproject.hacktok.router.routes.AuthRoute
import com.androidfinalproject.hacktok.ui.auth.AuthAction
import com.androidfinalproject.hacktok.ui.auth.AuthViewModel
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // Add Hilt Entry Point
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainAppTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = hiltViewModel() // Get ViewModel

                // Configure Google Sign-In
                val gso = remember {
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken("113050196515-lj7ceuo7l5acse99cjl3c74vtnhhdg60.apps.googleusercontent.com") // Use your actual ID token here
                        .requestEmail()
                        .build()
                }
                val googleSignInClient = remember { GoogleSignIn.getClient(this, gso) }

                // Launcher for Google Sign-In result
                val googleSignInLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    Log.d("MainActivity", "Google Sign-In Activity Result Received. Result Code: ${result.resultCode}")
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    try {
                        val account = task.getResult(ApiException::class.java)!!
                        Log.d("MainActivity", "Google Sign-In Task Successful. Account Email: ${account.email}")
                        account.idToken?.let { idToken ->
                            Log.d("MainActivity", "Got ID Token: ${idToken.take(10)}... Sending to ViewModel.")
                            authViewModel.onAction(AuthAction.GoogleSignIn(idToken))
                        } ?: run {
                            Log.e("MainActivity", "Google Sign In - ID Token is null for account: ${account.email}")
                            // Show error to user (e.g., Toast)
                        }
                    } catch (e: ApiException) {
                        val statusCode = e.statusCode
                        val statusMessage = GoogleSignInStatusCodes.getStatusCodeString(statusCode)
                        Log.w("MainActivity", "Google sign in failed: $statusMessage (Code: $statusCode)", e)
                        // Show specific error to user based on statusCode
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Google sign in failed with general exception", e)
                        // Show generic error to user
                    }
                }

                // --- NavHost Setup ---
                NavHost(
                    navController = navController,
                    startDestination = AuthRoute.Graph.route // Or your actual start destination
                ) {
                    authNavigation(
                        navController = navController,
                        onGoogleSignInClicked = {
                            Log.d("MainActivity", "Google Sign-In button clicked, launching intent.")
                            val signInIntent = googleSignInClient.signInIntent
                            googleSignInLauncher.launch(signInIntent)
                        }
                    )
                    adminNavigation(navController)
                    mainNavigation(navController)
                    testNavigation(navController)
                }
            }
        }
    }
}
