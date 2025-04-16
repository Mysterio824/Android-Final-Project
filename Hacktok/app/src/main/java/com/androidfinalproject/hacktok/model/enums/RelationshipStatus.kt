package com.androidfinalproject.hacktok.model.enums

enum class RelationshipStatus {
    NONE,               // no relationship / can send request
    PENDING_OUTGOING,   // I sent them a request
    PENDING_INCOMING,   // they sent me a request
    FRIENDS,            // we’re friends
    BLOCKED,            // they block me
    BLOCKING            // i block them
}