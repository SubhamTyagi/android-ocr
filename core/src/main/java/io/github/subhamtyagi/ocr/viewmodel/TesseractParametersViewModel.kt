package io.github.subhamtyagi.ocr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.subhamtyagi.ocr.data.DataStoreManager
import io.github.subhamtyagi.ocr.data.model.JCMState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TesseractParametersViewModel @Inject constructor(private val dataStoreManager: DataStoreManager) :
    ViewModel() {

    private val _pageSegMode = MutableStateFlow("best")
    val pageSegMode: StateFlow<String> = _pageSegMode.asStateFlow()

    private val _ocrMode = MutableStateFlow("TODO")
    val ocrMode: StateFlow<String> = _ocrMode.asStateFlow()

    private val _enableJCModifiers = MutableStateFlow(false)
    val enableJCModifiers: StateFlow<Boolean> = _enableJCModifiers.asStateFlow()

    private val _jCModifiers = MutableStateFlow(JCMState())
    val jCModifiers: StateFlow<JCMState> = _jCModifiers.asStateFlow()

    init {
        viewModelScope.launch {
            dataStoreManager.pageSegMode.collect { _pageSegMode.value = it }
        }
        viewModelScope.launch {
            dataStoreManager.ocrMode.collect { _ocrMode.value = it }
        }

        viewModelScope.launch {
            dataStoreManager.enableJCModifier.collect { _enableJCModifiers.value = it }
        }

        viewModelScope.launch {
            dataStoreManager.preserveInterWordSpaces.collect {
                _jCModifiers.value.preserveInterWordSpaces = it
            }
        }

        viewModelScope.launch {
            dataStoreManager.chopEnable.collect { _jCModifiers.value.chopEnable = it }
            dataStoreManager.newStateCost.collect { _jCModifiers.value.newStateCost = it }
            dataStoreManager.segmentSegCostRating.collect {
                _jCModifiers.value.segmentSegCostRating = it
            }
            dataStoreManager.newSegSearch.collect { _jCModifiers.value.newSegSearch = it }
            dataStoreManager.languageNgramOn.collect { _jCModifiers.value.languageNgramOn = it }
            dataStoreManager.textortForceMakePropWords.collect {
                _jCModifiers.value.textortForceMakePropWords = it
            }
            dataStoreManager.edgeMaxChildrenPerOutline.collect {
                _jCModifiers.value.edgeMaxChildrenPerOutline = it
            }

        }

    }


    fun updatePageSegMode(value: String) = viewModelScope.launch {
        dataStoreManager.setPageSegMode(value)
    }

    fun updateOCRMode(value: String) = viewModelScope.launch {
        dataStoreManager.setOCRMode(value)
    }

    fun updateEnableJCModifiers(value: Boolean) = viewModelScope.launch {
        dataStoreManager.setJCModifier(value)
    }

    fun updateJCModifiers(value: JCMState) = viewModelScope.launch {
        dataStoreManager.setPreserveInterwordSpaces(value.preserveInterWordSpaces)
        dataStoreManager.setChop(value.chopEnable)
        dataStoreManager.setNewStateCost(value.newStateCost)
        dataStoreManager.setSegmentSegCostRating(value.segmentSegCostRating)
        dataStoreManager.setNewSegSearch(value.newSegSearch)
        dataStoreManager.setLanguageNgramOn(value.languageNgramOn)
        dataStoreManager.setTexortForceMakePropWords(value.textortForceMakePropWords)
        dataStoreManager.setEdgeMaxChildPerOutline(value.edgeMaxChildrenPerOutline)
    }


}