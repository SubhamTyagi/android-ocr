package io.github.subhamtyagi.ocr.viewmodel

import android.os.FileObserver
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
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

    val selectedLanguages: StateFlow<Set<Language>> = languageDataManager.selectedLanguages.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet()
    )

    private val _downloadProgressMap = MutableStateFlow<Map<String, Int>>(emptyMap())
    val downloadProgressMap: StateFlow<Map<String, Int>> = _downloadProgressMap

    private val _downloadedLanguages = MutableStateFlow<List<Language>>(emptyList())
    val downloadedLanguages: StateFlow<List<Language>> = _downloadedLanguages

    fun checkDownloadedLanguages(allLanguages: List<Language>) =
        viewModelScope.launch(Dispatchers.IO) {
            val downloaded = allLanguages.filter { language ->
                isLanguageDataExist(language)
            }
            _downloadedLanguages.emit(downloaded)
        }

    private val _downloadResultFlow = MutableSharedFlow<DownloadResult>()
    val downloadResultFlow: SharedFlow<DownloadResult> = _downloadResultFlow.asSharedFlow()

    fun updateSelectedLanguages(language: Language, isSelected: Boolean) = viewModelScope.launch {
        val current = selectedLanguages.value.toMutableSet()
        if (isSelected) {
            if (!isLanguageDataExist(language = language)) {
                // downloadLanguage(language)
            }
            var added = current.add(language)
            Log.d(TAG, "updateSelectedLanguages: updated language added=$added")
        } else {
            var deleted = current.remove(language)
            Log.d(TAG, "updateSelectedLanguages: selected lang deleted=$deleted")
        }
        languageDataManager.saveSelectedLanguages(current)
    }

    //TODO: Check for any error and no internet connections
    fun downloadLanguage(
        language: Language
    ) = viewModelScope.launch(Dispatchers.IO) {
        val request = Request.Builder().url(Utils.getDownloadUrl(language.code)).build()
        val response = client.newCall(request).execute()

        if (!response.isSuccessful || response.body == null) {
            Log.e(TAG, "Download failed for ${language.code}")
            _downloadResultFlow.emit(DownloadResult.Failure(language, "Failed to download file"))
            return@launch
        }

        val file = File(
            languageDataManager.baseDir, Constants.LANGUAGE_DATA_FILE_NAME.format(language.code)
        )
        val sink = file.sink().buffer()

        val progressBody = ProgressResponseBody(response.body!!, object : DownloadProgressListener {
            override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {
                val progressValue = (100f * bytesRead / contentLength).toInt()
                _downloadProgressMap.update { current ->
                    current.toMutableMap().apply { put(language.code, progressValue) }
                }
                language.copy(downloadedProgress = progressValue.toInt())
            }
        })

        try {
            progressBody.source().use { source ->
                sink.use { sinkBuffer ->
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
            //first method to observe download
            addToDownloadedLanguage(language)

        } catch (e: Exception) {
            _downloadResultFlow.emit(DownloadResult.Failure(language, e.message ?: "Unknown error"))
        }

    }

    //first method to observe download
    fun addToDownloadedLanguage(language: Language) {
        val current = _downloadedLanguages.value
        if (current.none { it == language }) {
            _downloadedLanguages.value = current + language
        }
    }

    fun getLanguagesList(selected: Set<Language>): List<Language> {
        return languageDataManager.getLanguagesList(selected)
    }

    fun deleteLanguage(language: Language) = viewModelScope.launch {
        languageDataManager.deleteLanguageData(language.code)
        //first method to observe download
        _downloadedLanguages.value = _downloadedLanguages.value.filterNot { it == language }
    }

    fun isLanguageDataExist(language: Language): Boolean {
        return languageDataManager.isLanguageDataDownloaded(languageCode = language.code)
    }

    //second method to observe download
    private lateinit var fileObserver: FileObserver

    fun observeTessDirectory(languages: List<Language>) {
        val tessdataDir = languageDataManager.baseDir
        fileObserver =
            object : FileObserver(tessdataDir.path, CREATE or DELETE or MOVED_TO or MOVED_FROM) {
                override fun onEvent(event: Int, path: String?) {
                    viewModelScope.launch(Dispatchers.IO) {
                        val downloaded = languages.filter { isLanguageDataExist(it) }
                        _downloadedLanguages.emit(downloaded)
                    }
                }
            }
        fileObserver.startWatching()
    }

    override fun onCleared() {
        super.onCleared()
        fileObserver.stopWatching()
    }
}