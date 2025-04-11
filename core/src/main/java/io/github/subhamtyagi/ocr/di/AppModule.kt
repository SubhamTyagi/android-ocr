package io.github.subhamtyagi.ocr.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

import io.github.subhamtyagi.ocr.data.DataStoreManager
import io.github.subhamtyagi.ocr.data.LanguageDataRepository
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providePreferenceManager(@ApplicationContext context: Context): DataStoreManager {
        return DataStoreManager(context)
    }

    @Provides
    @Singleton
    fun provideDataRepository(@ApplicationContext context: Context): LanguageDataRepository {
        return LanguageDataRepository(context)
    }
}