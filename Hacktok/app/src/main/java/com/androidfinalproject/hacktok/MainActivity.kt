package com.androidfinalproject.hacktok

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
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
import com.androidfinalproject.hacktok.service.FcmService
import com.androidfinalproject.hacktok.ui.auth.AuthAction
import com.androidfinalproject.hacktok.ui.auth.AuthViewModel
import com.androidfinalproject.hacktok.ui.theme.MainAppTheme
import com.androidfinalproject.hacktok.utils.GooglePlayServicesHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import android.os.Build
import androidx.core.content.ContextCompat
import android.Manifest

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MainActivity"
        private const val RC_SIGN_IN = 9001
    }

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var authViewModel: AuthViewModel

    @Inject
    lateinit var fcmService: FcmService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("113050196515-lj7ceuo7l5acse99cjl3c74vtnhhdg60.apps.googleusercontent.com")
            .requestEmail()
            .build()
        
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        
        // Ensure Google Play Services is updated
        GooglePlayServicesHelper.updateAndroidSecurityProvider(this)

        setContent {
            MainAppTheme {
                val navController = rememberNavController()
                authViewModel = hiltViewModel()

                // Re-use the existing Google Sign-In Client in Compose
                val googleSignInClient = remember { googleSignInClient }

                // Launcher for Google Sign-In result
                val googleSignInLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    handleSignInResult(result.resultCode, result.data)
                }

                // --- NavHost Setup ---
                NavHost(
                    navController = navController,
                    startDestination = AuthRoute.Graph.route
                ) {
                    authNavigation(
                        navController = navController,
                        onGoogleSignInClicked = {
                            try {
                                Log.d(TAG, "Google Sign-In button clicked, launching intent.")
                                googleSignInLauncher.launch(googleSignInClient.signInIntent)
                            } catch (e: Exception) {
                                Log.e(TAG, "Failed to launch Google Sign-In", e)
                                // Fallback to compatibility workaround
                                startGoogleSignInWithCompatibilityWorkaround()
                            }
                        }
                    )
                    adminNavigation(navController)
                    mainNavigation(navController)
                    testNavigation(navController)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

    }
    
    /**
     * Fallback method for devices with Google Play Services issues
     */
    private fun startGoogleSignInWithCompatibilityWorkaround() {
        try {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
            Log.d(TAG, "Started Google Sign-In with compatibility workaround")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start Google Sign-In even with workaround", e)
        }
    }

    /**
     * Handle Google Sign-In result from startActivityForResult
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            handleSignInResult(resultCode, data)
        }
    }
    
    /**
     * Handle sign-in result from either launcher or compatibility workaround
     */
    private fun handleSignInResult(resultCode: Int, data: Intent?) {
        Log.d(TAG, "Sign-in result received. Result code: $resultCode")
        try {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    Log.d(TAG, "Google Sign-In successful. Account email: ${account?.email}")
                    account?.idToken?.let { idToken ->
                        Log.d(TAG, "Got ID token: ${idToken.take(10)}... Forwarding to ViewModel")
                        authViewModel.onAction(AuthAction.GoogleSignIn(idToken))
                    } ?: run {
                        Log.e(TAG, "Google Sign-In successful but ID token is null")
                    }
                } catch (e: ApiException) {
                    val statusCode = e.statusCode
                    val statusMessage = GoogleSignInStatusCodes.getStatusCodeString(statusCode)
                    Log.w(TAG, "Google sign in failed: $statusMessage (Code: $statusCode)", e)
                }
            } else {
                Log.w(TAG, "Google Sign-In failed or was canceled. Result code: $resultCode")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling Google Sign-In result", e)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("MainActivity", "Notification permission granted")
        } else {
            Log.d("MainActivity", "Notification permission denied")
        }
    }
}
