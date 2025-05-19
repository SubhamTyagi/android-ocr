package io.github.subhamtyagi.ocr.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.subhamtyagi.ocr.data.datastore.ImageProcessingDataManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageProcessingViewModel @Inject constructor(private val imageDataManager:ImageProcessingDataManager) :
    ViewModel() {

    private val _enhanceContrast = MutableStateFlow(false)
    val enhanceContrast: StateFlow<Boolean> = _enhanceContrast.asStateFlow()

    private val _unsharpMasking = MutableStateFlow(false)
    val unsharpMasking: StateFlow<Boolean> = _unsharpMasking.asStateFlow()

    private val _otsu = MutableStateFlow(false)
    val otsu: StateFlow<Boolean> = _otsu.asStateFlow()

    private val _deskew = MutableStateFlow(false)
    val deskew: StateFlow<Boolean> = _deskew.asStateFlow()

    init {
        viewModelScope.launch {
            imageDataManager.enhanceContrast.collect { _enhanceContrast.value = it }
        }
        viewModelScope.launch {
            imageDataManager.unSharpMasking.collect { _unsharpMasking.value = it }
        }

        viewModelScope.launch {
            imageDataManager.otsu.collect { _otsu.value = it }
        }

        viewModelScope.launch {
            imageDataManager.deSkew.collect { _deskew.value = it }
        }
    }

    fun updateEnhanceContrast(value: Boolean) = viewModelScope.launch {
        imageDataManager.setEnhanceContrast(value)
    }

    fun updateUnSharpMasking(value: Boolean) = viewModelScope.launch {
        imageDataManager.setUnSharpMasking(value)
    }

    fun updateOTSU(value: Boolean) = viewModelScope.launch {
        imageDataManager.setOTSU(value)
    }

    fun updateDeSkew(value: Boolean) = viewModelScope.launch {
        imageDataManager.setDeSkew(value)
    }

}