package io.github.subhamtyagi.ocr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.subhamtyagi.ocr.data.DataStoreManager
import io.github.subhamtyagi.ocr.data.LanguageDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class DownloadLanguageViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private var languageDataRepository: LanguageDataRepository
) :
    ViewModel() {
    private val _downloadStatus = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val downloadStatus = _downloadStatus.asStateFlow();

    private val _selectedLanguage = MutableStateFlow<Set<String>>(emptySet())
    val selectedLanguages = _selectedLanguage.asStateFlow();

    init {
        viewModelScope.launch {
            dataStoreManager.selectedLanguages.collect {
                _selectedLanguage.value = it
            }
        }

    }

    fun updateSelectedLanguages(value: Set<String>) = viewModelScope.launch {
        dataStoreManager.setSelectedLanguages(value)
    }

    fun checkLanguageDownload(languageCode: String) {
        val exists = languageDataRepository.isLanguageDataDownloaded(languageCode = languageCode)
        val updateMap = _downloadStatus.value.toMutableMap().apply {
            put(languageCode, exists)
        }
        _downloadStatus.value = updateMap
    }

    fun downloadLanguage(languageCode: String) = viewModelScope.launch {
        languageDataRepository.downloadLanguageData(languageCode = languageCode)
        checkLanguageDownload(languageCode)
    }

    fun deleteLanguage(languageCode: String) = viewModelScope.launch {
        languageDataRepository.deleteLanguageData(languageCode)
        checkLanguageDownload(languageCode = languageCode)
    }


}