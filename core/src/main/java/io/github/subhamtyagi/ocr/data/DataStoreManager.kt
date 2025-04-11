package io.github.subhamtyagi.ocr.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings_prefs")

class DataStoreManager(context: Context) {
    private val dataStore = context.dataStore
    companion object {
        val KEY_TESS_DATA_SOURCE = stringPreferencesKey("key_tess_training_data_source")
        val KEY_SELECTED_LANGUAGES = stringSetPreferencesKey("key_language_for_tesseract_multi")
        val KEY_ADVANCED_TESS = booleanPreferencesKey("key_advance_tess_option")
        val KEY_USE_GRAYSCALE = booleanPreferencesKey("key_grayscale_image_ocr")
        val KEY_PERSIST_DATA = booleanPreferencesKey("key_persist_data")
    }

    val tessDataSource: Flow<String> = dataStore.data.map { it[KEY_TESS_DATA_SOURCE] ?: "best" }
    val selectedLanguages: Flow<Set<String>> = dataStore.data.map { it[KEY_SELECTED_LANGUAGES] ?: emptySet() }
    val advancedTessEnabled: Flow<Boolean> = dataStore.data.map { it[KEY_ADVANCED_TESS] ?: false }
    val useGrayscale: Flow<Boolean> = dataStore.data.map { it[KEY_USE_GRAYSCALE] ?: true }
    val persistData: Flow<Boolean> = dataStore.data.map { it[KEY_PERSIST_DATA] ?: true }


    suspend fun setTessDataSource(value: String) {
        dataStore.edit { it[KEY_TESS_DATA_SOURCE] = value }
    }

    suspend fun setSelectedLanguages(value: Set<String>) {
        dataStore.edit { it[KEY_SELECTED_LANGUAGES] = value }
    }

    suspend fun setAdvancedTessEnabled(value: Boolean) {
        dataStore.edit { it[KEY_ADVANCED_TESS] = value }
    }

    suspend fun setUseGrayscale(value: Boolean) {
        dataStore.edit { it[KEY_USE_GRAYSCALE] = value }
    }

    suspend fun setPersistData(value: Boolean) {
        dataStore.edit { it[KEY_PERSIST_DATA] = value }
    }
}
