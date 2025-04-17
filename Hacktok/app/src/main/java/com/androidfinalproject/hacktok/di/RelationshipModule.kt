package com.androidfinalproject.hacktok.di

import com.androidfinalproject.hacktok.repository.RelationshipRepository
import com.androidfinalproject.hacktok.repository.impl.RelationshipRepositoryImpl
import com.androidfinalproject.hacktok.service.RelationshipService
import com.androidfinalproject.hacktok.service.impl.RelationshipServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RelationshipModule {
    @Binds
    @Singleton
    abstract fun bindRelationshipRepository(relationshipRepositoryImpl: RelationshipRepositoryImpl): RelationshipRepository

    @Binds
    @Singleton
    abstract fun bindRelationshipService(relationshipServiceImpl: RelationshipServiceImpl): RelationshipService
} 