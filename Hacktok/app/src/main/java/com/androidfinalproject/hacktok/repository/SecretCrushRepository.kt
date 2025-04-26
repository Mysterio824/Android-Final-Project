package com.androidfinalproject.hacktok.repository

import com.androidfinalproject.hacktok.model.SecretCrush
import kotlinx.coroutines.flow.Flow

interface SecretCrushRepository {
    /**
     * Observes secret crushes sent by the current user
     */
    fun observeMySecretCrushes(): Flow<Result<List<SecretCrush>>>

    /**
     * Observes secret crushes received by the current user
     */
    fun observeReceivedSecretCrushes(): Flow<Result<List<SecretCrush>>>

    /**
     * Sends a secret crush to another user
     * @param crushId The ID of the user to crush on
     */
    fun sendSecretCrush(crushId: String): Flow<Result<Unit>>

    /**
     * Reveals a secret crush to the receiver
     * @param crushId The ID of the secret crush to reveal
     */
    fun revealSecretCrush(crushId: String): Flow<Result<Unit>>

    /**
     * Deletes a secret crush
     * @param crushId The ID of the secret crush to delete
     */
    fun deleteSecretCrush(crushId: String): Flow<Result<Unit>>

    /**
     * Unreveals a secret crush
     * @param crushId The ID of the secret crush to unreveal
     */
    fun unrevealSecretCrush(crushId: String): Flow<Result<Unit>>
}