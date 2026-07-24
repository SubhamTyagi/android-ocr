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


    private val _tile = MutableStateFlow(false)
    val tile: StateFlow<Boolean> = _tile.asStateFlow()

    private val _showLanguageDialog = MutableStateFlow(false)
    val showLanguageDialog: StateFlow<Boolean> = _showLanguageDialog.asStateFlow()

    init {
        viewModelScope.launch {
            settingsDataManager.advancedTessEnabled.collect { _advancedTessEnabled.value = it }
        }
        viewModelScope.launch {
            settingsDataManager.useImageProcessing.collect { _useImageProcessing.value = it }
        }

        viewModelScope.launch {
            settingsDataManager.enableTile.collect { _tile.value = it }
        }
        viewModelScope.launch {
            settingsDataManager.showLanguageDialog.collect { _showLanguageDialog.value = it }
        }
    }

    fun updateAdvancedTessEnabled(value: Boolean) = viewModelScope.launch {
        settingsDataManager.setAdvancedTessEnabled(value)
    }

    fun updateUseGrayscale(value: Boolean) = viewModelScope.launch {
        settingsDataManager.setUseImageProcessing(value)
    }


    fun updateTile(value: Boolean) = viewModelScope.launch {
        settingsDataManager.setTile(value)
    }

    fun updateShowLanguageDialog(value: Boolean) = viewModelScope.launch {
        settingsDataManager.setShowLanguageDialog(value)
    }
}
