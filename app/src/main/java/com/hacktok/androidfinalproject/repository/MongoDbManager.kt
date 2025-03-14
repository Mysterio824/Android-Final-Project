package com.hacktok.androidfinalproject.repository

import android.annotation.SuppressLint
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.ServerApi
import com.mongodb.ServerApiVersion
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoCollection
import org.bson.Document

object MongoDbManager {
    private const val CONNECTION_STRING = "mongodb+srv://vhakhoa22:yDiU2oJjr7cjUgKq@cluster0.yord7.mongodb.net/?retryWrites=true&w=majority"

    private val client: MongoClient by lazy {
        val serverApi = ServerApi.builder().version(ServerApiVersion.V1).build()
        val settings = MongoClientSettings.builder()
            .applyConnectionString(ConnectionString(CONNECTION_STRING))
            .serverApi(serverApi)
            .build()
        MongoClient.create(settings)
    }

    // Get a database by name.
    fun getDatabase(databaseName: String) = client.getDatabase(databaseName)

    // Get a collection as Document type.
    fun getCollection(databaseName: String, collectionName: String): MongoCollection<Document> =
        getDatabase(databaseName).getCollection(collectionName)

    fun close() {
        client.close()
    }
}