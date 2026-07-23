package io.github.subhamtyagi.ocr.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.subhamtyagi.ocr.data.Constants
import io.github.subhamtyagi.ocr.data.Utils
import io.github.subhamtyagi.ocr.data.datastore.LanguageDataManager
import io.github.subhamtyagi.ocr.data.model.Language
import io.github.subhamtyagi.ocr.downloader.DownloadProgressListener
import io.github.subhamtyagi.ocr.downloader.DownloadResult
import io.github.subhamtyagi.ocr.downloader.ProgressResponseBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
) : ViewModel() {

    private val TAG = "DownloadLanguageVM"

    private val _selectedLanguage = MutableStateFlow<Set<Language>>(emptySet())
    val selectedLanguages = _selectedLanguage.asStateFlow()

    init {
        viewModelScope.launch {
            languageDataManager.selectedLanguages.collect {
                _selectedLanguage.value = it
            }
        }
    }

    private val _downloadProgressMap = MutableStateFlow<Map<String, Int>>(emptyMap())
    val downloadProgressMap: StateFlow<Map<String, Int>> = _downloadProgressMap

    private val _downloadResultFlow = MutableSharedFlow<DownloadResult>()
    val downloadResultFlow: SharedFlow<DownloadResult> = _downloadResultFlow.asSharedFlow()

    fun updateSelectedLanguages(language: Language, isSelected: Boolean) = viewModelScope.launch {
        val current = selectedLanguages.value.toMutableSet()
        if (isSelected) {
            current.add(language)
        } else {
            current.remove(language)

        }
        languageDataManager.saveSelectedLanguages(current)
    }

    fun downloadLanguage(
        language: Language
    ) = viewModelScope.launch(Dispatchers.IO) {
        if (!Utils.isNetworkAvailable(languageDataManager.context.applicationContext as Application)) {
            _downloadResultFlow.emit(DownloadResult.Failure(language, "No internet connection"))
            return@launch
        }

        try {
            val request = Request.Builder().url(Utils.getDownloadUrl(language.code)).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e(TAG, "Download failed for ${language.code}")
                    _downloadResultFlow.emit(
                        DownloadResult.Failure(
                            language,
                            "Failed to download file"
                        )
                    )
                    return@launch
                }

                val body = response.body

                val file = File(
                    languageDataManager.baseDir,
                    Constants.LANGUAGE_DATA_FILE_NAME.format(language.code)
                )

                val progressBody = ProgressResponseBody(body, object : DownloadProgressListener {
                    private var lastProgress = -1
                    override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {
                        if (contentLength <= 0) return
                        val progressValue = (100f * bytesRead / contentLength).toInt()

                        if (progressValue != lastProgress) {
                            lastProgress = progressValue
                            _downloadProgressMap.update { current ->
                                current.toMutableMap().apply { put(language.code, progressValue) }
                            }
                        }
                    }
                })

                file.sink().buffer().use { sinkBuffer ->
                    progressBody.source().use { source ->
                        sinkBuffer.writeAll(source)
                    }
                }

                _downloadProgressMap.update { current ->
                    current.toMutableMap().apply { remove(language.code) }
                }

                withContext(Dispatchers.Main) {
                    updateSelectedLanguages(language = language, isSelected = true)
                }
                _downloadResultFlow.emit(DownloadResult.Success(language))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error downloading ${language.code}", e)
            _downloadProgressMap.update { current ->
                current.toMutableMap().apply { remove(language.code) }
            }
            _downloadResultFlow.emit(DownloadResult.Failure(language, e.message ?: "Unknown error"))
        }
    }


    fun getLanguagesList(selected: Set<Language>): List<Language> {
        return languageDataManager.getLanguagesList(selected)
    }

    fun deleteLanguage(language: Language) = viewModelScope.launch {
        languageDataManager.deleteLanguageData(language.code)
        withContext(Dispatchers.Main) {
            updateSelectedLanguages(language = language, isSelected = false)
        }

    }

    /* fun isLanguageDataExist(language: Language): Boolean {
         return languageDataManager.isLanguageDataDownloaded(languageCode = language.code)
     }*/

    //second method to observe download
    //private lateinit var fileObserver: FileObserver

    /* fun observeTessDirectory(languages: List<Language>) {
         val tessdataDir = languageDataManager.baseDir
         fileObserver =
             object : FileObserver(tessdataDir.path, CREATE or DELETE or MOVED_TO or MOVED_FROM) {
                 override fun onEvent(event: Int, path: String?) {
                     viewModelScope.launch(Dispatchers.IO) {
                       //  val downloaded = languages.filter { isLanguageDataExist(it) }
                        // _downloadedLanguages.emit(downloaded)
                     }
                 }
             }
         fileObserver.startWatching()
     }*/

    override fun onCleared() {
        super.onCleared()
        // fileObserver.stopWatching()
    }
}