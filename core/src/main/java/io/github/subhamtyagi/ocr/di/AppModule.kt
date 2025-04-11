package io.github.subhamtyagi.ocr.di

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