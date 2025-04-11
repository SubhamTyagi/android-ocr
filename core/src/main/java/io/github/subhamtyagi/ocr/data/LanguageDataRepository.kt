package io.github.subhamtyagi.ocr.data

import android.content.Context
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LanguageDataRepository @Inject constructor(context: Context) {
    private val baseDir: File = File(context.filesDir, "best/data").apply {
        if (!exists()) mkdirs()
    }

    fun getDataFile(languageCode: String): File = File(baseDir, languageCode)

    fun isLanguageDataDownloaded(languageCode: String): Boolean = getDataFile(languageCode).exists()

    suspend fun downloadLanguageData(languageCode: String): File {
        val file = getDataFile(languageCode)
        //download here
        file.writeText("download file")
        return file
    }

    fun deleteLanguageData(languageCode: String): Boolean {
        val file = getDataFile(languageCode = languageCode)
        return if (file.exists()) file.delete() else false
    }


}
