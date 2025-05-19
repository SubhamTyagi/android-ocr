package io.github.subhamtyagi.ocr.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.github.subhamtyagi.ocr.R

import io.github.subhamtyagi.ocr.data.model.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.File

private val Context.dataStore by preferencesDataStore(name = "language_data")
class LanguageDataManager(val context: Context) {
    private val dataStore = context.dataStore
    val languageNames = context.resources.getStringArray(R.array.ocr_engine_language_names)
    val languageCode = context.resources.getStringArray(R.array.ocr_engine_language_code)
    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        val KEY_TESS_SELECTED_LANGUAGES = stringPreferencesKey("key_tess_languages")
    }

    val selectedLanguages: Flow<Set<Language>> = dataStore.data.map { preferences ->
        preferences[KEY_TESS_SELECTED_LANGUAGES]?.let {
            try {
                json.decodeFromString<List<Language>>(it).toSet()
            } catch (e: Exception) {
                emptySet()
            }
        } ?: emptySet()
    }

    suspend fun saveSelectedLanguages(languages: Set<Language>) {
        val jsonString =
            json.encodeToString(ListSerializer(Language.serializer()), languages.toList())
        context.dataStore.edit { preferences ->
            preferences[KEY_TESS_SELECTED_LANGUAGES] = jsonString
        }
    }


    private val baseDir: File = File(context.filesDir, "best/tessdata").apply {
        if (!exists()) mkdirs()
        context
    }

    fun getDataFile(languageCode: String): File = File(baseDir, languageCode)

    fun isLanguageDataDownloaded(languageCode: String): Boolean = getDataFile(languageCode).exists()

    fun deleteLanguageData(languageCode: String): Boolean {
        val file = getDataFile(languageCode = languageCode)
        return if (file.exists()) file.delete() else false
    }

    fun getLanguagesList(): List<Language> {
        val items = languageCode.zip(languageNames) { code, name ->
            Language(code, name, isDownloaded = isLanguageDataDownloaded(code))
        }
        return items
    }

}