package io.github.subhamtyagi.ocr.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.subhamtyagi.ocr.data.Constants
import io.github.subhamtyagi.ocr.data.datastore.LanguageDataManager
import io.github.subhamtyagi.ocr.data.model.Language
import io.github.subhamtyagi.ocr.downloader.DownloadProgressListener
import io.github.subhamtyagi.ocr.downloader.ProgressResponseBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink
import java.io.File
import javax.inject.Inject

@HiltViewModel
open class DownloadLanguageViewModel @Inject constructor(
    private val languageDataManager: LanguageDataManager,
    private val client: OkHttpClient,
    private val application: Application
) :
    ViewModel() {
    private val _downloadProgress = MutableStateFlow(0f)
    val downloadProgress = _downloadProgress.asStateFlow()


    private val _downloadStatus = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val downloadStatus = _downloadStatus.asStateFlow()

    val languageList = languageDataManager.getLanguagesList()

    val selectedLanguages: StateFlow<Set<Language>> =
        languageDataManager.selectedLanguages.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptySet()
        )

    fun updateSelectedLanguages(value: Set<Language>) = viewModelScope.launch {
        languageDataManager.saveSelectedLanguages(value)
        value.forEach {
            if (!languageDataManager.isLanguageDataDownloaded(it.code)) {
                downloadLanguage(it)
            }
        }
    }

    private fun selectLanguage(language: Language) {
        val exists = isLanguageDataExist(language = language)
        val updateMap = _downloadStatus.value.toMutableMap().apply {
            put(language.code, exists)
        }
        _downloadStatus.value = updateMap
    }

    fun isLanguageDataExist(language: Language): Boolean {
        return languageDataManager.isLanguageDataDownloaded(languageCode = language.code)
    }

    fun downloadLanguage(language: Language) = viewModelScope.launch {
        downloadLanguageData(
            url=  getUrl(language.code),
            fileName = Constants.LANGUAGE_DATA_FILE_NAME.format(language.code),
            dirName = "best/tessdata"
        )
        selectLanguage(language)
    }

    private fun getDownloadUrl(dataType: String, lang: String): String {
        return when (dataType) {
            "best" -> when (lang) {
                "akk" -> Constants.TESSERACT_DATA_DOWNLOAD_URL_AKK_BEST
                "eqo" -> Constants.TESSERACT_DATA_DOWNLOAD_URL_EQU
                else -> String.format(Constants.TESSERACT_DATA_DOWNLOAD_URL_BEST, lang)
            }

            "standard" -> when (lang) {
                "akk" -> Constants.TESSERACT_DATA_DOWNLOAD_URL_AKK_STANDARD
                "eqo" -> Constants.TESSERACT_DATA_DOWNLOAD_URL_EQU
                else -> String.format(Constants.TESSERACT_DATA_DOWNLOAD_URL_STANDARD, lang)
            }

            else -> when (lang) {
                "akk" -> Constants.TESSERACT_DATA_DOWNLOAD_URL_AKK_FAST
                "eqo" -> Constants.TESSERACT_DATA_DOWNLOAD_URL_EQU
                else -> String.format(Constants.TESSERACT_DATA_DOWNLOAD_URL_FAST, lang)
            }
        }
    }

    fun getUrl(language: String): String {
        return when (language) {
            "akk" -> Constants.TESSERACT_DATA_DOWNLOAD_URL_AKK_BEST
            "equ" -> Constants.TESSERACT_DATA_DOWNLOAD_URL_EQU
            else -> Constants.TESSERACT_DATA_DOWNLOAD_URL_BEST.format(language)
        }
    }

    fun deleteLanguage(language: Language) = viewModelScope.launch {
        languageDataManager.deleteLanguageData(language.code)
        selectLanguage(language = language)
    }

    fun downloadLanguageData(
        url: String,
        fileName: String,
        dirName: String = "best"
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val request = Request.Builder().url(url).build()

            val response = client.newCall(request).execute()
            val body = response.body ?: return@launch

            val dir = application.getDir(dirName, Context.MODE_PRIVATE)
            if (!dir.exists()) dir.mkdirs()

            val file = File(dir, fileName)
            val sink = file.sink().buffer()

            val progressBody = ProgressResponseBody(body, object : DownloadProgressListener {
                override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {
                    val progressValue = (100f * bytesRead / contentLength)
                    _downloadProgress.value = progressValue
                }
            })

            progressBody.source().use { source ->
                sink.use { sinkBuffer ->
                    sinkBuffer.writeAll(source)
                }
            }

        }
    }

}