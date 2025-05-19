package io.github.subhamtyagi.ocr.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private val Context.dataStore by preferencesDataStore(name = "tess_parameter")
class TesseractParameterDataManager(val context: Context) {
    private val dataStore = context.dataStore
    companion object {
        //Tesseract parameters keys
        val KEY_TESS_SEG_MODE = intPreferencesKey("key_tess_seg_mode")
        val KEY_TESS_OCR_MODE = intPreferencesKey("key_tess_ocr_mode")
        val KEY_TESS_JC_MODIFIERS = booleanPreferencesKey("key_tess_jc_modifiers")
        val KEY_TESS_JC_PRESERVE_INTER_WORD_SPACES =
            stringPreferencesKey("key_tess_jc_interword_space")
        val KEY_TESS_JC_CHOP = stringPreferencesKey("key_tess_jc_chop")

        val KEY_TESS_JC_LANGUAGE_NGRAM_ON = stringPreferencesKey("key_tess_jc_ngram")
        val KEY_TESS_JC_TEXORT_FORCE_MAKE_PROP_WORDS = stringPreferencesKey("key_tess_jc_texort")
        val KEY_TESS_JC_EDGE_MAX_CHILD_PER_OUTLINE =
            stringPreferencesKey("key_tess_jc_edge_max_child")
    }

    var pageSegMode: Flow<Int> = dataStore.data.map { it[KEY_TESS_SEG_MODE] ?: 6 }

    var ocrMode: Flow<Int> = dataStore.data.map {
        it[KEY_TESS_OCR_MODE] ?: 0 }

    var enableJCModifier: Flow<Boolean> = dataStore.data.map { it[KEY_TESS_JC_MODIFIERS] ?: false }

    var preserveInterWordSpaces: Flow<String> =
        dataStore.data.map { it[KEY_TESS_JC_PRESERVE_INTER_WORD_SPACES] ?: "0" }
    var chopEnable: Flow<String> = dataStore.data.map { it[KEY_TESS_JC_CHOP] ?: "T" }

    var languageNgramOn: Flow<String> =
        dataStore.data.map { it[KEY_TESS_JC_LANGUAGE_NGRAM_ON] ?: "F" }
    var textortForceMakePropWords: Flow<String> =
        dataStore.data.map { it[KEY_TESS_JC_TEXORT_FORCE_MAKE_PROP_WORDS] ?: "F" }
    var edgeMaxChildrenPerOutline: Flow<String> =
        dataStore.data.map { it[KEY_TESS_JC_EDGE_MAX_CHILD_PER_OUTLINE] ?: "40" }


    // advance tess parameters
    suspend fun setPageSegMode(value: Int) {
        dataStore.edit { it[KEY_TESS_SEG_MODE] = value }
    }

    suspend fun setOCRMode(value: Int) {
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