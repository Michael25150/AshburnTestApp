package com.example.ashburntestapp.di

import com.example.ashburntestapp.data.repositories.DataManagingRepositoryImpl
import com.example.ashburntestapp.domain.repositories.DataManagingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindAuthRepository(dataManagingRepository: DataManagingRepositoryImpl): DataManagingRepository
}