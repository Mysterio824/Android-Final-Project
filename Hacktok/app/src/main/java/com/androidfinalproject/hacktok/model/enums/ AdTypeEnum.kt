package com.androidfinalproject.hacktok.model.enums

enum class AdType(val displayName: String) {
    BANNER("Banner Ad"),
    SPONSORED_POST("Sponsored Post"),
    PROMOTED_CONTENT("Promoted Content");

    companion object {
        fun fromString(value: String): AdType {
            return values().find { it.name == value } ?: SPONSORED_POST
        }
    }
}