package com.androidfinalproject.hacktok.model

import com.google.firebase.firestore.PropertyName
import java.time.Instant

enum class RelationshipStatus {
    NONE,               // no relationship / can send request
    PENDING_OUTGOING,   // I sent them a request
    PENDING_INCOMING,   // they sent me a request
    FRIENDS,            // we’re friends
    BLOCKED,            // they block me
    BLOCKING            // i block them
}

data class RelationInfo(
    @PropertyName("id")val id: String,
    @PropertyName("status")val status: RelationshipStatus = RelationshipStatus.NONE,
    @PropertyName("lastActionByMe")val lastActionByMe: Boolean = false,   // did I send/accept/block?
    @PropertyName("updatedAt")val updatedAt: Instant? = null         // when it last changed
)