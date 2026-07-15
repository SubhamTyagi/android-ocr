package io.github.subhamtyagi.ocr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.subhamtyagi.ocr.data.datastore.TesseractParameterDataManager
import io.github.subhamtyagi.ocr.data.model.JCMState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TesseractParametersViewModel @Inject constructor(private val dataStoreManager: TesseractParameterDataManager) :
    ViewModel() {

    private val _pageSegMode = MutableStateFlow(0)
    val pageSegMode: StateFlow<Int> = _pageSegMode.asStateFlow()

    private val _ocrMode = MutableStateFlow(0)
    val ocrMode: StateFlow<Int> = _ocrMode.asStateFlow()

    private val _enableJCModifiers = MutableStateFlow(false)
    val enableJCModifiers: StateFlow<Boolean> = _enableJCModifiers.asStateFlow()
   // TODO: check for its correctness : whether is work or not(copy function)
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
            dataStoreManager.languageNgramOn.collect { _jCModifiers.value.languageNgramOn = it }
            dataStoreManager.textortForceMakePropWords.collect {
                _jCModifiers.value.textortForceMakePropWords = it
            }
            dataStoreManager.edgeMaxChildrenPerOutline.collect {
                _jCModifiers.value.edgeMaxChildrenPerOutline = it
            }
        }
    }

    fun updatePageSegMode(value: Int) = viewModelScope.launch {
        dataStoreManager.setPageSegMode(value)
    }

    fun updateOCRMode(value: Int) = viewModelScope.launch {
        dataStoreManager.setOCRMode(value)
    }

    fun updateEnableJCModifiers(value: Boolean) = viewModelScope.launch {
        dataStoreManager.setJCModifier(value)
    }

    fun updateJCModifiers(value: JCMState) = viewModelScope.launch {
        dataStoreManager.setPreserveInterwordSpaces(value.preserveInterWordSpaces)
        dataStoreManager.setChop(value.chopEnable)
        dataStoreManager.setLanguageNgramOn(value.languageNgramOn)
        dataStoreManager.setTexortForceMakePropWords(value.textortForceMakePropWords)
        dataStoreManager.setEdgeMaxChildPerOutline(value.edgeMaxChildrenPerOutline)
    }


}