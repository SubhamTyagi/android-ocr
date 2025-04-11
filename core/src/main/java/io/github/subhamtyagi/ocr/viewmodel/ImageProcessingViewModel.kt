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
class ImageProcessingViewModel @Inject constructor(private val dataStoreManager: DataStoreManager) :
    ViewModel()  {

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
            dataStoreManager.enhanceContrast.collect { _enhanceContrast.value = it }
        }
        viewModelScope.launch {
            dataStoreManager.unSharpMasking.collect { _unsharpMasking.value=it }
        }

        viewModelScope.launch {
            dataStoreManager.otsu.collect { _otsu.value=it }
        }

        viewModelScope.launch {
            dataStoreManager.deSkew.collect { _deskew.value=it }
        }
    }

    fun updateEnhanceContrast(value: Boolean) = viewModelScope.launch {
        dataStoreManager.setEnhanceContrast(value)
    }

    fun updateUnSharpMasking(value: Boolean)=viewModelScope.launch {
        dataStoreManager.setUnSharpMasking(value)
    }

    fun updateOTSU(value: Boolean)=viewModelScope.launch {
        dataStoreManager.setOTSU(value)
    }

    fun updateDeSkew(value: Boolean)=viewModelScope.launch {
        dataStoreManager.setDeSkew(value)
    }

}