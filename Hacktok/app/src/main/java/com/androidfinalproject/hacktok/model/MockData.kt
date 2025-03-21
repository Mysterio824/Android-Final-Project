package com.androidfinalproject.hacktok.model

import org.bson.types.ObjectId

object MockData {
    val mockUsers = listOf(
        User(ObjectId(), "john", "john@example.com"),
        User(ObjectId(), "jane_smith", "jane@example.com"),
        User(ObjectId(), "bob_jones", "bob@example.com")
    )

    val mockPosts = listOf(
        Post(ObjectId(), "Hello world!", mockUsers[0]),
        Post(ObjectId(), "Learning Kotlin!", mockUsers[1]),
        Post(ObjectId(), "Jetpack Compose is fun!", mockUsers[2])
    )
}