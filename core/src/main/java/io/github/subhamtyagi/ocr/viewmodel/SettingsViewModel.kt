package io.github.subhamtyagi.ocr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.subhamtyagi.ocr.data.DataStoreManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class SettingsViewModel @Inject constructor(private val dataStoreManager: DataStoreManager) :
    ViewModel() {

    private val _tessDataSource = MutableStateFlow("best")
    val tessDataSource: StateFlow<String> = _tessDataSource.asStateFlow()

    private val _selectedLanguages = MutableStateFlow(setOf<String>())
    val selectedLanguages: StateFlow<Set<String>> = _selectedLanguages.asStateFlow()

    private val _advancedTessEnabled = MutableStateFlow(false)
    val advancedTessEnabled: StateFlow<Boolean> = _advancedTessEnabled.asStateFlow()

    private val _useGrayscale = MutableStateFlow(true)
    val useGrayscale: StateFlow<Boolean> = _useGrayscale.asStateFlow()

    private val _persistData = MutableStateFlow(true)
    val persistData: StateFlow<Boolean> = _persistData.asStateFlow()

    private val _tile = MutableStateFlow(false)
    val tile: StateFlow<Boolean> = _tile.asStateFlow()

    init {
        viewModelScope.launch {
            dataStoreManager.tessDataSource.collect { _tessDataSource.value = it }
        }
        viewModelScope.launch {
            dataStoreManager.selectedLanguages.collect { _selectedLanguages.value = it }
        }
        viewModelScope.launch {
            dataStoreManager.advancedTessEnabled.collect { _advancedTessEnabled.value = it }
        }
        viewModelScope.launch {
            dataStoreManager.useImageProcessing.collect { _useGrayscale.value = it }
        }
        viewModelScope.launch {
            dataStoreManager.persistData.collect { _persistData.value = it }
        }
        viewModelScope.launch {
            dataStoreManager.enableTile.collect { _tile.value = it }
        }
    }

    fun updateTessDataSource(value: String) = viewModelScope.launch {
        dataStoreManager.setTessDataSource(value)
    }

    fun updateAdvancedTessEnabled(value: Boolean) = viewModelScope.launch {
        dataStoreManager.setAdvancedTessEnabled(value)
    }

    fun updateUseGrayscale(value: Boolean) = viewModelScope.launch {
        dataStoreManager.setUseImageProcessing(value)
    }

    fun updatePersistData(value: Boolean) = viewModelScope.launch {
        dataStoreManager.setPersistData(value)
    }
    fun updateTile(value: Boolean)=viewModelScope.launch {
        dataStoreManager.setTile(value)
    }
}
