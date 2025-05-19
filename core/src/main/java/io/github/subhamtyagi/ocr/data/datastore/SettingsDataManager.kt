package io.github.subhamtyagi.ocr.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")
class SettingsDataManager(val context: Context) {
    private val dataStore = context.dataStore

    companion object {
        //main setting screen
        val KEY_TESS_DATA_SOURCE = stringPreferencesKey("key_tess_training_data_source")
        val KEY_APP_ENABLE_TILE = booleanPreferencesKey("key_app_tile")
        val KEY_APP_PERSIST_DATA = booleanPreferencesKey("key_app_persist_data")
        //main setting screen and some other screen are depends upon value of these
        val KEY_TESS_ADVANCE_OPTIONS_ENABLE = booleanPreferencesKey("key_tess_advance_tess_options")
        val KEY_IMAGE_PROCESSING_ENABLE = booleanPreferencesKey("key_images_processing")

    }

    val tessDataSource: Flow<String> = dataStore.data.map { it[KEY_TESS_DATA_SOURCE] ?: "best" }
    val advancedTessEnabled: Flow<Boolean> =
        dataStore.data.map { it[KEY_TESS_ADVANCE_OPTIONS_ENABLE] ?: false }
    val persistData: Flow<Boolean> = dataStore.data.map { it[KEY_APP_PERSIST_DATA] ?: true }
    var enableTile: Flow<Boolean> = dataStore.data.map { it[KEY_APP_ENABLE_TILE] ?: true }
    val useImageProcessing: Flow<Boolean> =
        dataStore.data.map { it[KEY_IMAGE_PROCESSING_ENABLE] ?: true }

    suspend fun setTessDataSource(value: String) {
        dataStore.edit { it[KEY_TESS_DATA_SOURCE] = value }
    }

    suspend fun setTile(value: Boolean) {
        dataStore.edit { it[KEY_APP_ENABLE_TILE] = value }
    }

    suspend fun setPersistData(value: Boolean) {
        dataStore.edit { it[KEY_APP_PERSIST_DATA] = value }
    }

    // Image processing
    suspend fun setUseImageProcessing(value: Boolean) {
        dataStore.edit { it[KEY_IMAGE_PROCESSING_ENABLE] = value }
    }

    suspend fun setAdvancedTessEnabled(value: Boolean) {
        dataStore.edit { it[KEY_TESS_ADVANCE_OPTIONS_ENABLE] = value }
    }


}