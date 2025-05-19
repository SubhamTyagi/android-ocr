package io.github.subhamtyagi.ocr.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

import io.github.subhamtyagi.ocr.data.DataStoreManager
import io.github.subhamtyagi.ocr.data.db.HistoryDatabase
import io.github.subhamtyagi.ocr.data.HistoryRepository
import io.github.subhamtyagi.ocr.data.LanguageDataRepository
import io.github.subhamtyagi.ocr.data.dao.HistoryDao
import io.github.subhamtyagi.ocr.engine.ImageTextReader
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
    /*@Provides
    @Singleton
    fun provideOCR():ImageTextReader{

    }*/

}