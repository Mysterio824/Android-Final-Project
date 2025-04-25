package com.androidfinalproject.hacktok.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

/**
 * Singleton utility class to handle notification permissions across the app
 */
object NotificationPermissionHandler {
    private const val TAG = "NotificationPermissionHandler"

    // For tracking if we've already requested permissions this session
    private var hasRequestedPermission = false

    /**
     * Checks and requests notification permissions if needed and not previously requested
     *
     * @param context The application context
     * @param permissionLauncher The permission launcher to request permissions
     * @param force Force request permissions even if previously requested
     */
    fun checkAndRequestNotificationPermission(
        context: Context,
        permissionLauncher: ActivityResultLauncher<String>,
        force: Boolean = false
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission && (force || !hasRequestedPermission)) {
                Log.d(TAG, "Requesting notification permission")
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                hasRequestedPermission = true
            } else if (hasPermission) {
                Log.d(TAG, "Notification permission already granted")
            } else {
                Log.d(TAG, "Already requested notification permission this session")
            }
        } else {
            Log.d(TAG, "Notification permission not required for this Android version")
        }
    }

    /**
     * Checks if notification permissions are granted
     *
     * @param context The application context
     * @return True if permissions are granted or not required, false otherwise
     */
    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Permission not required for older Android versions
            true
        }
    }

    /**
     * Reset the permission request tracking
     * Useful if you want to request permissions again after some time
     */
    fun resetPermissionRequestTracking() {
        hasRequestedPermission = false
    }

    /**
     * Register a permission callback handler
     *
     * @param onPermissionResult Callback with permission result
     * @return ActivityResultLauncher that can be used for permission requests
     */
    fun registerPermissionCallback(
        activity: androidx.activity.ComponentActivity,
        onPermissionResult: (Boolean) -> Unit
    ): ActivityResultLauncher<String> {
        return activity.registerForActivityResult(
            androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                Log.d(TAG, "Notification permission granted")
            } else {
                Log.d(TAG, "Notification permission denied")
            }
            onPermissionResult(isGranted)
        }
    }
}