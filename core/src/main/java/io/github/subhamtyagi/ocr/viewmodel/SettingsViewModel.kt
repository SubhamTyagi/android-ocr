package io.github.subhamtyagi.ocr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.subhamtyagi.ocr.data.datastore.SettingsDataManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class SettingsViewModel @Inject constructor(private val settingsDataManager: SettingsDataManager) :
    ViewModel() {

    private val _advancedTessEnabled = MutableStateFlow(false)
    val advancedTessEnabled: StateFlow<Boolean> = _advancedTessEnabled.asStateFlow()

    private val _useImageProcessing = MutableStateFlow(true)
    val useImageProcessing: StateFlow<Boolean> = _useImageProcessing.asStateFlow()

    private val _persistData = MutableStateFlow(true)
    val persistData: StateFlow<Boolean> = _persistData.asStateFlow()

    private val _tile = MutableStateFlow(false)
    val tile: StateFlow<Boolean> = _tile.asStateFlow()

    init {
        viewModelScope.launch {
            settingsDataManager.advancedTessEnabled.collect { _advancedTessEnabled.value = it }
        }
        viewModelScope.launch {
            settingsDataManager.useImageProcessing.collect { _useImageProcessing.value = it }
        }
        viewModelScope.launch {
            settingsDataManager.persistData.collect { _persistData.value = it }
        }
        viewModelScope.launch {
            settingsDataManager.enableTile.collect { _tile.value = it }
        }
    }

    fun updateAdvancedTessEnabled(value: Boolean) = viewModelScope.launch {
        settingsDataManager.setAdvancedTessEnabled(value)
    }

    fun updateUseGrayscale(value: Boolean) = viewModelScope.launch {
        settingsDataManager.setUseImageProcessing(value)
    }

    fun updatePersistData(value: Boolean) = viewModelScope.launch {
        settingsDataManager.setPersistData(value)
    }

    fun updateTile(value: Boolean) = viewModelScope.launch {
        settingsDataManager.setTile(value)
    }
}
