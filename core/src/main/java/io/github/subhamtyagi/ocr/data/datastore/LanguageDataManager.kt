package io.github.subhamtyagi.ocr.data.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.github.subhamtyagi.ocr.R
import io.github.subhamtyagi.ocr.data.Constants
import io.github.subhamtyagi.ocr.data.model.Language
import kotlinx.coroutines.flow.Flow

import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.File

private val Context.dataStore by preferencesDataStore(name = "language_ds")

class LanguageDataManager(val context: Context) {
    private val TAG = "LanguageDataManagerDS"
    private val dataStore = context.dataStore
    private val json = Json { ignoreUnknownKeys = true }

    val languageNames = context.resources.getStringArray(R.array.ocr_engine_language_names)
    val languageCode = context.resources.getStringArray(R.array.ocr_engine_language_code)

    companion object {
        private val KEY_TESS_SELECTED_LANGUAGES =
            stringPreferencesKey("key_tess_selected_languages")

    }

    val selectedLanguages: Flow<Set<Language>> = dataStore.data.map { preferences ->
        preferences[KEY_TESS_SELECTED_LANGUAGES]?.let {
            try {
                json.decodeFromString<List<Language>>(it).toSet()
            } catch (_: Exception) {
                Log.d(TAG, "An exception occurred while decoding json from list")
                emptySet()
            }
        } ?: emptySet()
    }


    suspend fun saveSelectedLanguages(languages: Set<Language>) {
        val jsonString =
            json.encodeToString(ListSerializer(Language.serializer()), languages.toList())
        dataStore.edit { preferences ->
            preferences[KEY_TESS_SELECTED_LANGUAGES] = jsonString
            Log.d(TAG, "saveSelectedLanguages: jsonString to be saved: $jsonString")
        }
    }


    val baseDir: File = File(context.filesDir, Constants.DATA_DIR).apply {
        if (!exists()) mkdirs()
        context
    }

    fun getDataFile(languageCode: String): File = File(
        baseDir,
        Constants.LANGUAGE_DATA_FILE_NAME
            .format(languageCode)
    )

    fun isLanguageDataDownloaded(languageCode: String): Boolean = getDataFile(languageCode).exists()

    fun deleteLanguageData(languageCode: String): Boolean {
        val file = getDataFile(languageCode = languageCode)
        Log.d(TAG, "deleteLanguageData: language $file data deleted")
        return if (file.exists()) file.delete() else false
    }

    fun getLanguagesList(selectedLanguage: Set<Language>): List<Language> {
        val selectedCodes = selectedLanguage.map { it.code }.toSet()
        val items = languageCode.zip(languageNames) { code, name ->
            var isDownloaded = isLanguageDataDownloaded(code)
            Language(
                code,
                name,
                isDownloaded = isDownloaded,
                isSelected = code in selectedCodes,
                downloadedProgress = if (!isDownloaded) 0 else -1,
            )

        }
        return items
    }

}