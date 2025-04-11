package io.github.subhamtyagi.ocr.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings_prefs")

class DataStoreManager(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        //for downloaded and selected viewmodel and download screen
        val KEY_TESS_SELECTED_LANGUAGES = stringSetPreferencesKey("key_tess_languages")
        //main setting screen
        val KEY_TESS_DATA_SOURCE = stringPreferencesKey("key_tess_training_data_source")
        val KEY_APP_ENABLE_TILE = booleanPreferencesKey("key_app_tile")
        val KEY_APP_PERSIST_DATA = booleanPreferencesKey("key_app_persist_data")
        //main setting screen and some other screen are depends upon value of these
        val KEY_TESS_ADVANCE_OPTIONS_ENABLE = booleanPreferencesKey("key_tess_advance_tess_options")
        val KEY_IMAGE_PROCESSING_ENABLE = booleanPreferencesKey("key_images_processing")
        //Image processing keys
        val KEY_IMAGE_ENHANCE_CONTRAST = booleanPreferencesKey("key_image_enhance_contrast")
        val KEY_IMAGE_UN_SHARP_MASKING = booleanPreferencesKey("key_image_un_sharp_masking")
        val KEY_IMAGE_OTSU = booleanPreferencesKey("key_image_otsu")
        val KEY_IMAGE_DE_SKEW = booleanPreferencesKey("key_image_de_skew")
        //Tesseract parameters keys
        val KEY_TESS_SEG_MODE = stringPreferencesKey("key_tess_seg_mode")
        val KEY_TESS_OCR_MODE = stringPreferencesKey("key_tess_ocr_mode")
        val KEY_TESS_JC_MODIFIERS = booleanPreferencesKey("key_tess_jc_modifiers")
        val KEY_TESS_JC_PRESERVE_INTER_WORD_SPACES =
            stringPreferencesKey("key_tess_jc_interword_space")
        val KEY_TESS_JC_CHOP = stringPreferencesKey("key_tess_jc_chop")
        val KEY_TESS_JC_NEW_STATE_COST = stringPreferencesKey("key_tess_jc_new_state_cost")
        val KEY_TESS_JC_SEGMENT_SEG_COST_RATING = stringPreferencesKey("key_tess_jc_seg_cost")
        val KEY_TESS_JC_NEW_SEG_SEARCH = stringPreferencesKey("key_tess_jc_new_seg_search")
        val KEY_TESS_JC_LANGUAGE_NGRAM_ON = stringPreferencesKey("key_tess_jc_ngram")
        val KEY_TESS_JC_TEXORT_FORCE_MAKE_PROP_WORDS = stringPreferencesKey("key_tess_jc_texort")
        val KEY_TESS_JC_EDGE_MAX_CHILD_PER_OUTLINE =
            stringPreferencesKey("key_tess_jc_edge_max_child")
    }


    val selectedLanguages: Flow<Set<String>> =
        dataStore.data.map { it[KEY_TESS_SELECTED_LANGUAGES] ?: emptySet() }
    val tessDataSource: Flow<String> = dataStore.data.map { it[KEY_TESS_DATA_SOURCE] ?: "best" }
    val advancedTessEnabled: Flow<Boolean> =
        dataStore.data.map { it[KEY_TESS_ADVANCE_OPTIONS_ENABLE] ?: false }
    val persistData: Flow<Boolean> = dataStore.data.map { it[KEY_APP_PERSIST_DATA] ?: true }
    var enableTile: Flow<Boolean> = dataStore.data.map { it[KEY_APP_ENABLE_TILE] ?: true }

    val useImageProcessing: Flow<Boolean> =
        dataStore.data.map { it[KEY_IMAGE_PROCESSING_ENABLE] ?: true }

    var enhanceContrast: Flow<Boolean> =
        dataStore.data.map { it[KEY_IMAGE_ENHANCE_CONTRAST] ?: true }
    var unSharpMasking: Flow<Boolean> =
        dataStore.data.map { it[KEY_IMAGE_UN_SHARP_MASKING] ?: true }
    var otsu: Flow<Boolean> = dataStore.data.map { it[KEY_IMAGE_OTSU] ?: true }
    var deSkew: Flow<Boolean> = dataStore.data.map { it[KEY_IMAGE_DE_SKEW] ?: true }


    var pageSegMode: Flow<String> = dataStore.data.map { it[KEY_TESS_SEG_MODE] ?: "TODO" }
    var ocrMode: Flow<String> = dataStore.data.map { it[KEY_TESS_OCR_MODE] ?: "TODO" }
    var enableJCModifier: Flow<Boolean> = dataStore.data.map { it[KEY_TESS_JC_MODIFIERS] ?: false }

    var preserveInterWordSpaces: Flow<String> =
        dataStore.data.map { it[KEY_TESS_JC_PRESERVE_INTER_WORD_SPACES] ?: "0" }
    var chopEnable: Flow<String> = dataStore.data.map { it[KEY_TESS_JC_CHOP] ?: "T" }
    var newStateCost: Flow<String> = dataStore.data.map { it[KEY_TESS_JC_NEW_STATE_COST] ?: "F" }
    var segmentSegCostRating: Flow<String> =
        dataStore.data.map { it[KEY_TESS_JC_SEGMENT_SEG_COST_RATING] ?: "F" }
    var newSegSearch: Flow<String> = dataStore.data.map { it[KEY_TESS_JC_NEW_SEG_SEARCH] ?: "0" }
    var languageNgramOn: Flow<String> =
        dataStore.data.map { it[KEY_TESS_JC_LANGUAGE_NGRAM_ON] ?: "F" }
    var textortForceMakePropWords: Flow<String> =
        dataStore.data.map { it[KEY_TESS_JC_TEXORT_FORCE_MAKE_PROP_WORDS] ?: "F" }
    var edgeMaxChildrenPerOutline: Flow<String> =
        dataStore.data.map { it[KEY_TESS_JC_EDGE_MAX_CHILD_PER_OUTLINE] ?: "40" }


    suspend fun setTessDataSource(value: String) {
        dataStore.edit { it[KEY_TESS_DATA_SOURCE] = value }
    }

    suspend fun setSelectedLanguages(value: Set<String>) {
        dataStore.edit { it[KEY_TESS_SELECTED_LANGUAGES] = value }
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

    suspend fun setAdvancedTessEnabled(value: Boolean) {
        dataStore.edit { it[KEY_TESS_ADVANCE_OPTIONS_ENABLE] = value }
    }

    // advance tess parameters
    suspend fun setPageSegMode(value: String) {
        dataStore.edit { it[KEY_TESS_SEG_MODE] = value }
    }

    suspend fun setOCRMode(value: String) {
        dataStore.edit { it[KEY_TESS_OCR_MODE] = value }
    }

    suspend fun setJCModifier(value: Boolean) {
        dataStore.edit { it[KEY_TESS_JC_MODIFIERS] = value }
    }

    //JC Modifiers
    suspend fun setPreserveInterwordSpaces(value: String) {
        dataStore.edit { it[KEY_TESS_JC_PRESERVE_INTER_WORD_SPACES] = value }
    }

    suspend fun setChop(value: String) {
        dataStore.edit { it[KEY_TESS_JC_CHOP] = value }
    }

    suspend fun setNewStateCost(value: String) {
        dataStore.edit { it[KEY_TESS_JC_NEW_STATE_COST] = value }
    }

    suspend fun setSegmentSegCostRating(value: String) {
        dataStore.edit { it[KEY_TESS_JC_SEGMENT_SEG_COST_RATING] = value }
    }

    suspend fun setNewSegSearch(value: String) {
        dataStore.edit { it[KEY_TESS_JC_NEW_SEG_SEARCH] = value }
    }

    suspend fun setLanguageNgramOn(value: String) {
        dataStore.edit { it[KEY_TESS_JC_LANGUAGE_NGRAM_ON] = value }
    }

    suspend fun setTexortForceMakePropWords(value: String) {
        dataStore.edit { it[KEY_TESS_JC_TEXORT_FORCE_MAKE_PROP_WORDS] = value }
    }

    suspend fun setEdgeMaxChildPerOutline(value: String) {
        dataStore.edit { it[KEY_TESS_JC_EDGE_MAX_CHILD_PER_OUTLINE] = value }
    }
}