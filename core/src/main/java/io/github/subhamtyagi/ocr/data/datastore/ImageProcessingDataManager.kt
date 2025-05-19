package io.github.subhamtyagi.ocr.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private val Context.dataStore by preferencesDataStore(name = "image_processing")
class ImageProcessingDataManager(val context: Context) {
    private val dataStore = context.dataStore
    companion object{
        //Image processing keys
        val KEY_IMAGE_ENHANCE_CONTRAST = booleanPreferencesKey("key_image_enhance_contrast")
        val KEY_IMAGE_UN_SHARP_MASKING = booleanPreferencesKey("key_image_un_sharp_masking")
        val KEY_IMAGE_OTSU = booleanPreferencesKey("key_image_otsu")
        val KEY_IMAGE_DE_SKEW = booleanPreferencesKey("key_image_de_skew")
    }

    var enhanceContrast: Flow<Boolean> =
        dataStore.data.map { it[KEY_IMAGE_ENHANCE_CONTRAST] ?: true }
    var unSharpMasking: Flow<Boolean> =
        dataStore.data.map { it[KEY_IMAGE_UN_SHARP_MASKING] ?: true }
    var otsu: Flow<Boolean> = dataStore.data.map { it[KEY_IMAGE_OTSU] ?: true }
    var deSkew: Flow<Boolean> = dataStore.data.map { it[KEY_IMAGE_DE_SKEW] ?: true }

    suspend fun setEnhanceContrast(value: Boolean) {
        dataStore.edit { it[KEY_IMAGE_ENHANCE_CONTRAST] = value }
    }

    suspend fun setUnSharpMasking(value: Boolean) {
        dataStore.edit { it[KEY_IMAGE_UN_SHARP_MASKING] = value }
    }

    suspend fun setOTSU(value: Boolean) {
        dataStore.edit { it[KEY_IMAGE_OTSU] = value }
    }

    suspend fun setDeSkew(value: Boolean) {
        dataStore.edit { it[KEY_IMAGE_DE_SKEW] = value }
    }
}