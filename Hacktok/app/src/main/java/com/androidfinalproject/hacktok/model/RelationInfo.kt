package com.androidfinalproject.hacktok.model

import com.androidfinalproject.hacktok.model.enums.RelationshipStatus
import com.google.firebase.firestore.PropertyName
import java.time.Instant

data class RelationInfo(
    @PropertyName("id")val id: String,
    @PropertyName("status")val status: RelationshipStatus = RelationshipStatus.NONE,
    @PropertyName("lastActionByMe")val lastActionByMe: Boolean = false,   // did I send/accept/block?
    @PropertyName("updatedAt")val updatedAt: Instant? = null         // when it last changed
)