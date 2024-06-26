package com.example.ashburntestapp.di

import android.content.Context
import com.example.ashburntestapp.common.FileManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {

    @Provides
    @Singleton
    fun provideFileManager(
        @ApplicationContext context: Context,
    ): FileManager = FileManager(context)
}