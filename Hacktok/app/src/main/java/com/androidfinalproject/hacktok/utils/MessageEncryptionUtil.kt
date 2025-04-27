package com.androidfinalproject.hacktok.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.MessageDigest

object MessageEncryptionUtil {
    private const val GCM_IV_LENGTH = 12
    private const val GCM_TAG_LENGTH = 128
    private const val KEY_SIZE = 256 // bits
    private const val PREFS_NAME = "encryption_prefs"
    private const val KEY_PREF = "encryption_key"

    private var secretKey: SecretKey? = null
    private lateinit var prefs: SharedPreferences

    fun initialize(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        // Load existing key if available
        val savedKey = prefs.getString(KEY_PREF, null)
        if (savedKey != null) {
            val keyBytes = Base64.decode(savedKey, Base64.DEFAULT)
            secretKey = SecretKeySpec(keyBytes, "AES")
        }
    }

    fun initializeKey(keyString: String) {
        // Tạo key từ string bằng cách hash
        val keyBytes = MessageDigest.getInstance("SHA-256")
            .digest(keyString.toByteArray())
        secretKey = SecretKeySpec(keyBytes, "AES")
        
        // Lưu key vào SharedPreferences
        if (::prefs.isInitialized) {
            prefs.edit().putString(KEY_PREF, Base64.encodeToString(keyBytes, Base64.DEFAULT)).apply()
        }
    }

    private fun getKey(): SecretKey {
        return secretKey ?: throw IllegalStateException("Key not initialized. Call initializeKey first.")
    }

    fun encrypt(message: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getKey())
        
        val iv = cipher.iv
        val encryptedBytes = cipher.doFinal(message.toByteArray(Charsets.UTF_8))
        
        // Combine IV and encrypted data
        val combined = ByteArray(iv.size + encryptedBytes.size)
        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(encryptedBytes, 0, combined, iv.size, encryptedBytes.size)
        
        return Base64.encodeToString(combined, Base64.DEFAULT)
    }

    fun decrypt(encryptedMessage: String): String {
        val combined = Base64.decode(encryptedMessage, Base64.DEFAULT)
        
        // Validate input data
        if (combined.size <= GCM_IV_LENGTH) {
            throw IllegalArgumentException("Invalid encrypted message: too short")
        }
        
        // Extract IV and encrypted data
        val iv = ByteArray(GCM_IV_LENGTH)
        val encryptedBytes = ByteArray(combined.size - GCM_IV_LENGTH)
        System.arraycopy(combined, 0, iv, 0, GCM_IV_LENGTH)
        System.arraycopy(combined, GCM_IV_LENGTH, encryptedBytes, 0, encryptedBytes.size)
        
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, getKey(), spec)
        
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }
}
