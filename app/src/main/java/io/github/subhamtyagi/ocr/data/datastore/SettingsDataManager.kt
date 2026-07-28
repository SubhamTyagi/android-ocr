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
        val KEY_APP_ENABLE_TILE = booleanPreferencesKey("key_app_tile")


        //main setting screen and some other screen are depends upon value of these
        val KEY_TESS_ADVANCE_OPTIONS_ENABLE = booleanPreferencesKey("key_tess_advance_tess_options")
        val KEY_IMAGE_PROCESSING_ENABLE = booleanPreferencesKey("key_images_processing")
        val KEY_SHOW_LANGUAGE_DIALOG = booleanPreferencesKey("key_show_language_dialog")
    }

    val advancedTessEnabled: Flow<Boolean> =
        dataStore.data.map { it[KEY_TESS_ADVANCE_OPTIONS_ENABLE] ?: false }
    var enableTile: Flow<Boolean> = dataStore.data.map { it[KEY_APP_ENABLE_TILE] ?: true }
    val useImageProcessing: Flow<Boolean> =
        dataStore.data.map { it[KEY_IMAGE_PROCESSING_ENABLE] ?: true }
    val showLanguageDialog: Flow<Boolean> =
        dataStore.data.map { it[KEY_SHOW_LANGUAGE_DIALOG] ?: true }

    suspend fun setTile(value: Boolean) {
        dataStore.edit { it[KEY_APP_ENABLE_TILE] = value }
    }

    suspend fun setUseImageProcessing(value: Boolean) {
        dataStore.edit { it[KEY_IMAGE_PROCESSING_ENABLE] = value }
    }

    suspend fun setAdvancedTessEnabled(value: Boolean) {
        dataStore.edit { it[KEY_TESS_ADVANCE_OPTIONS_ENABLE] = value }
    }

    suspend fun setShowLanguageDialog(value: Boolean) {
        dataStore.edit { it[KEY_SHOW_LANGUAGE_DIALOG] = value }
    }


}