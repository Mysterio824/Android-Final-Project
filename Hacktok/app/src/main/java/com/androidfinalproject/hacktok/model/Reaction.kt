package com.androidfinalproject.hacktok.model

data class Reaction(
    val userId: String,
    val emoji: String,
) {
    constructor() : this("", "")

    override fun toString(): String {
        return "Reaction(userId='$userId', emoji='$emoji')"
    }
}

data class FullReaction(
    val user: User,
    val emoji: String
)
