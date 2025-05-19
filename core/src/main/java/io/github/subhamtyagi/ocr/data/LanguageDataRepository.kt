package io.github.subhamtyagi.ocr.data

import android.content.Context
import io.github.subhamtyagi.ocr.R
import io.github.subhamtyagi.ocr.data.model.Language
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageDataRepository
@Inject constructor(
    context: Context
) {
    val languageNames = context.resources.getStringArray(R.array.ocr_engine_language_names)
    val languageCode = context.resources.getStringArray(R.array.ocr_engine_language_code)

    private val baseDir: File = File(context.filesDir, "best/data").apply {
        if (!exists()) mkdirs()
        context
    }

    fun getDataFile(languageCode: String): File = File(baseDir, languageCode)

    fun isLanguageDataDownloaded(languageCode: String): Boolean = getDataFile(languageCode).exists()

    suspend fun downloadLanguageData(languageCode: String): File {
        val file = getDataFile(languageCode)
        //TODO: download here
        file.writeText("download file")
        return file
    }

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
