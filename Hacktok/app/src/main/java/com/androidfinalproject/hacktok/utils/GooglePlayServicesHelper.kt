package com.androidfinalproject.hacktok.utils

import android.content.Context
import android.util.Log
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.security.ProviderInstaller
import java.security.NoSuchAlgorithmException
import javax.net.ssl.SSLContext

/**
 * Helper class to handle Google Play Services issues
 */
object GooglePlayServicesHelper {
    private const val TAG = "GooglePlayServices"

    /**
     * Updates Android Security Provider to help prevent SSL/TLS handshake issues
     * particularly in older Android versions.
     *
     * @param context Application context
     * @return true if security provider was successfully updated
     */
    fun updateAndroidSecurityProvider(context: Context): Boolean {
        return try {
            ProviderInstaller.installIfNeeded(context)
            Log.d(TAG, "Security provider updated successfully")
            
            try {
                // Create SSL context with updated security provider
                SSLContext.getInstance("TLSv1.2").apply {
                    init(null, null, null)
                }
                true
            } catch (e: NoSuchAlgorithmException) {
                Log.e(TAG, "Error creating SSLContext", e)
                false
            }
        } catch (e: GooglePlayServicesRepairableException) {
            // Google Play Services is available but needs repair
            val statusCode = e.connectionStatusCode
            Log.w(TAG, "Google Play Services needs repair: $statusCode")
            
            // Show dialog to install/update/fix Google Play Services if needed
            GoogleApiAvailability.getInstance()
                .showErrorNotification(context, statusCode)
            false
        } catch (e: GooglePlayServicesNotAvailableException) {
            // Google Play Services is not available
            Log.e(TAG, "Google Play Services not available", e)
            false
        } catch (e: Exception) {
            // Handle any other exceptions that might occur
            Log.e(TAG, "Unknown error updating security provider", e)
            false
        }
    }

    /**
     * Checks if Google Play Services is available and up to date
     *
     * @param context Application context
     * @return true if Google Play Services is available and up to date
     */
    fun isGooglePlayServicesAvailable(context: Context): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context)
        
        return if (resultCode == com.google.android.gms.common.ConnectionResult.SUCCESS) {
            Log.d(TAG, "Google Play Services is available and up to date")
            true
        } else {
            Log.w(TAG, "Google Play Services is not available: $resultCode")
            false
        }
    }
} 