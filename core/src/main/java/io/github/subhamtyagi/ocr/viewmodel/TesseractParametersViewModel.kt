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
class TesseractParametersViewModel @Inject constructor(private val dataStoreManager: DataStoreManager) :
    ViewModel()  {

    private val _pageSegMode = MutableStateFlow("best")
    val pageSegMode: StateFlow<String> = _pageSegMode.asStateFlow()

    private val _ocrMode=MutableStateFlow("TODO")
    val ocrMode: StateFlow<String> = _ocrMode.asStateFlow()

    private val _enableJCModifiers=MutableStateFlow(false)
    val enableJCModifiers: StateFlow<Boolean> = _enableJCModifiers.asStateFlow()

    private val _preserveInterWordSpaces=MutableStateFlow("TODO")
    val preserveInterWordSpaces: StateFlow<String> = _preserveInterWordSpaces.asStateFlow()

    private val _chopEnable=MutableStateFlow("TODO")
    val chopEnable: StateFlow<String> = _chopEnable.asStateFlow()

    private val _newStateCost=MutableStateFlow("TODO")
    val newStateCost: StateFlow<String> = _newStateCost.asStateFlow()

    private val _segmentSegCostRating=MutableStateFlow("TODO")
    val segmentSegCostRating: StateFlow<String> = _segmentSegCostRating.asStateFlow()

    private val _newSegSearch=MutableStateFlow("TODO")
    val newSegSearch: StateFlow<String> = _newSegSearch.asStateFlow()

    private val _languageNgramOn=MutableStateFlow("TODO")
    val languageNgramOn: StateFlow<String> = _languageNgramOn.asStateFlow()

    private val _textortForceMakePropWords=MutableStateFlow("TODO")
    val textortForceMakePropWords: StateFlow<String> = _textortForceMakePropWords.asStateFlow()

    private val _edgeMaxChildrenPerOutline=MutableStateFlow("TODO")
    val edgeMaxChildrenPerOutline: StateFlow<String> = _edgeMaxChildrenPerOutline.asStateFlow()

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
            dataStoreManager.preserveInterWordSpaces.collect { _preserveInterWordSpaces.value = it }
        }

        viewModelScope.launch {
            dataStoreManager.chopEnable.collect { _chopEnable.value = it }
        }

        viewModelScope.launch {
            dataStoreManager.newStateCost.collect { _newStateCost.value = it }
        }

        viewModelScope.launch {
            dataStoreManager.segmentSegCostRating.collect { _segmentSegCostRating.value = it }
        }

        viewModelScope.launch {
            dataStoreManager.newSegSearch.collect { _newSegSearch.value = it }
        }

        viewModelScope.launch {
            dataStoreManager.languageNgramOn.collect { _languageNgramOn.value = it }
        }

        viewModelScope.launch {
            dataStoreManager.textortForceMakePropWords.collect { _textortForceMakePropWords.value = it }
        }

        viewModelScope.launch {
            dataStoreManager.edgeMaxChildrenPerOutline.collect { _edgeMaxChildrenPerOutline.value = it }
        }

    }

    fun updatePageSegMode(value: String) = viewModelScope.launch {
        dataStoreManager.setPageSegMode(value)
    }
    fun updateOCRMode(value: String) = viewModelScope.launch {
        dataStoreManager.setOCRMode(value)
    }
    fun updateJCModifiers(value: Boolean) = viewModelScope.launch {
        dataStoreManager.setJCModifier(value)
    }
    fun updatePreserverInterWordSpaces(value: String) = viewModelScope.launch {
        dataStoreManager.setPreserveInterwordSpaces(value)
    }
    fun updateChopEnable(value: String) = viewModelScope.launch {
        dataStoreManager.setChop(value)
    }
    fun updateNewStateCost(value: String) = viewModelScope.launch {
        dataStoreManager.setNewStateCost(value)
    }
    fun updateSegmentSegCostRating(value: String) = viewModelScope.launch {
        dataStoreManager.setSegmentSegCostRating(value)
    }
    fun updateNewSegSearch(value: String) = viewModelScope.launch {
        dataStoreManager.setNewSegSearch(value)
    }
    fun updateLanguageNgramOn(value: String) = viewModelScope.launch {
        dataStoreManager.setLanguageNgramOn(value)
    }
    fun updateTextortForceMakePropWords(value: String) = viewModelScope.launch {
        dataStoreManager.setTexortForceMakePropWords(value)
    }
    fun updateEdgeMaxChildrenPerOutline(value: String) = viewModelScope.launch {
        dataStoreManager.setEdgeMaxChildPerOutline(value)
    }


}