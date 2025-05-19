package io.github.subhamtyagi.ocr.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

import io.github.subhamtyagi.ocr.data.datastore.SettingsDataManager
import io.github.subhamtyagi.ocr.data.db.HistoryDatabase
import io.github.subhamtyagi.ocr.data.HistoryRepository
import io.github.subhamtyagi.ocr.data.datastore.LanguageDataManager
import io.github.subhamtyagi.ocr.data.dao.HistoryDao
import io.github.subhamtyagi.ocr.data.datastore.ImageProcessingDataManager
import io.github.subhamtyagi.ocr.data.datastore.TesseractParameterDataManager
import okhttp3.OkHttpClient
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providePreferenceManager(@ApplicationContext context: Context): SettingsDataManager {
        return SettingsDataManager(context)
    }

    @Provides
    @Singleton
    fun provideLanguageDataManager(@ApplicationContext context: Context): LanguageDataManager {
        return LanguageDataManager(context)
    }

    @Provides
    @Singleton
    fun provideTesseractParameterDataManager(@ApplicationContext context: Context): TesseractParameterDataManager {
        return TesseractParameterDataManager(context)
    }

    @Provides
    @Singleton
    fun provideImageProcessingDataManager(@ApplicationContext context: Context): ImageProcessingDataManager {
        return ImageProcessingDataManager(context)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): HistoryDatabase {
        return Room.databaseBuilder(
            context = context,
            HistoryDatabase::class.java,
            name = "app_history_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun  provideHistoryDao(database: HistoryDatabase): HistoryDao{
        return database.historyDao()
    }

    @Provides
    @Singleton
    fun provideHistoryRepository(historyDao: HistoryDao): HistoryRepository {
        return HistoryRepository(historyDao)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

}