package com.androidfinalproject.hacktok.utils

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PublicKey
import javax.crypto.KeyAgreement
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import java.security.MessageDigest
import android.util.Base64

class KeyExchangeManager {
    private var keyPair: KeyPair? = null
    private var sharedSecret: SecretKey? = null

    fun generateKeyPair(): String {
        val keyPairGenerator = KeyPairGenerator.getInstance("EC")
        keyPairGenerator.initialize(256)
        keyPair = keyPairGenerator.generateKeyPair()
        
        // Trả về public key dưới dạng Base64
        return Base64.encodeToString(
            keyPair?.public?.encoded,
            Base64.DEFAULT
        )
    }

    fun generateSharedSecret(peerPublicKeyBase64: String) {
        val peerPublicKey = java.security.spec.X509EncodedKeySpec(
            Base64.decode(peerPublicKeyBase64, Base64.DEFAULT)
        ).let {
            java.security.KeyFactory.getInstance("EC").generatePublic(it)
        }

        val keyAgreement = KeyAgreement.getInstance("ECDH")
        keyAgreement.init(keyPair?.private)
        keyAgreement.doPhase(peerPublicKey, true)

        // Tạo shared secret từ key agreement
        val sharedSecretBytes = keyAgreement.generateSecret()
        val hash = MessageDigest.getInstance("SHA-256").digest(sharedSecretBytes)
        sharedSecret = SecretKeySpec(hash, "AES")
    }

    fun getSharedSecret(): SecretKey {
        return sharedSecret ?: throw IllegalStateException("Shared secret not generated")
    }
} 