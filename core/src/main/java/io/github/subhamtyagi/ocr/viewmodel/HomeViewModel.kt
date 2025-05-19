package io.github.subhamtyagi.ocr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.subhamtyagi.ocr.data.DataStoreManager
import io.github.subhamtyagi.ocr.data.HistoryRepository
import io.github.subhamtyagi.ocr.data.model.History
import io.github.subhamtyagi.ocr.engine.ImageTextReader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    val history = historyRepository.getHistoryList()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList<History>())

    private val _selectedLanguage = MutableStateFlow<Set<String>>(emptySet())
    val selectedLanguages = _selectedLanguage.asStateFlow();
    var ocr:ImageTextReader?=null
    init {
        viewModelScope.launch {
            dataStoreManager.selectedLanguages.collect {
                _selectedLanguage.value = it
            }
        }
        viewModelScope.launch {
            ocr= ImageTextReader.getInstance (
                path = "",
                pageSegMode = 0,
                languages = selectedLanguages.value,
                parameters = emptyMap(),
                isParameterSet = false,

            ){

            }
        }
    }

    fun addHistory(history: History) = viewModelScope.launch {
        historyRepository.insert(history = history)
    }

    fun deleteHistory(history: History) = viewModelScope.launch {
        historyRepository.delete(history = history)
    }


}