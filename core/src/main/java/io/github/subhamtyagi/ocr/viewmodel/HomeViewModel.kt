package io.github.subhamtyagi.ocr.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.googlecode.leptonica.android.AdaptiveMap
import com.googlecode.leptonica.android.Binarize
import com.googlecode.leptonica.android.Convert
import com.googlecode.leptonica.android.Enhance
import com.googlecode.leptonica.android.Pix
import com.googlecode.leptonica.android.ReadFile
import com.googlecode.leptonica.android.Rotate
import com.googlecode.leptonica.android.Skew
import com.googlecode.leptonica.android.WriteFile
import com.googlecode.tesseract.android.TessBaseAPI
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.subhamtyagi.ocr.data.datastore.SettingsDataManager
import io.github.subhamtyagi.ocr.data.HistoryRepository
import io.github.subhamtyagi.ocr.data.datastore.ImageProcessingDataManager
import io.github.subhamtyagi.ocr.data.datastore.LanguageDataManager
import io.github.subhamtyagi.ocr.data.datastore.TesseractParameterDataManager
import io.github.subhamtyagi.ocr.data.model.History
import io.github.subhamtyagi.ocr.data.model.JCMState
import io.github.subhamtyagi.ocr.data.model.Language
import io.github.subhamtyagi.ocr.engine.ImageTextReader
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val settingsDataManager: SettingsDataManager,
    private val tesseractParameterDataManager: TesseractParameterDataManager,
    private val imageProcessingDataManager: ImageProcessingDataManager,
    private val languageDataManager: LanguageDataManager,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    val history = historyRepository.getHistoryList()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList<History>())

    private val _selectedLanguage = MutableStateFlow<Set<Language>>(emptySet())
    val selectedLanguages = _selectedLanguage.asStateFlow()

    private val _pageSegMode = MutableStateFlow(6)
    val pageSegMode: StateFlow<Int> = _pageSegMode.asStateFlow()

    private val _tessDataSource = MutableStateFlow("best")
    val tessDataSource: StateFlow<String> = _tessDataSource.asStateFlow()

    private val _ocrMode = MutableStateFlow(0)
    val ocrMode: StateFlow<Int> = _ocrMode.asStateFlow()

    private val _enableJCModifiers = MutableStateFlow(false)
    val enableJCModifiers: StateFlow<Boolean> = _enableJCModifiers.asStateFlow()

    private val _jCModifiers = MutableStateFlow(JCMState())
    val jCModifiers: StateFlow<JCMState> = _jCModifiers.asStateFlow()

//////////////////
    private val _enhanceContrast = MutableStateFlow(false)
    val enhanceContrast: StateFlow<Boolean> = _enhanceContrast.asStateFlow()

    private val _unsharpMasking = MutableStateFlow(false)
    val unsharpMasking: StateFlow<Boolean> = _unsharpMasking.asStateFlow()

    private val _otsu = MutableStateFlow(false)
    val otsu: StateFlow<Boolean> = _otsu.asStateFlow()

    private val _deskew = MutableStateFlow(false)
    val deskew: StateFlow<Boolean> = _deskew.asStateFlow()
///////////////////
    var ocr: ImageTextReader? = null

    init {
        viewModelScope.launch {
            settingsDataManager.tessDataSource.collect { _tessDataSource.value = it }
        }

        viewModelScope.launch {
            tesseractParameterDataManager.pageSegMode.collect { _pageSegMode.value = it }
        }
        viewModelScope.launch {
            languageDataManager.selectedLanguages.collect {
                _selectedLanguage.value = it
            }
        }

        viewModelScope.launch {
            tesseractParameterDataManager.ocrMode.collect { _ocrMode.value = it }
        }

        viewModelScope.launch {
            tesseractParameterDataManager.preserveInterWordSpaces.collect {
                _jCModifiers.value.preserveInterWordSpaces = it
            }
        }

        viewModelScope.launch {
            tesseractParameterDataManager.chopEnable.collect { _jCModifiers.value.chopEnable = it }
            tesseractParameterDataManager.languageNgramOn.collect { _jCModifiers.value.languageNgramOn = it }
            tesseractParameterDataManager.textortForceMakePropWords.collect {
                _jCModifiers.value.textortForceMakePropWords = it
            }
            tesseractParameterDataManager.edgeMaxChildrenPerOutline.collect {
                _jCModifiers.value.edgeMaxChildrenPerOutline = it
            }
        }

        viewModelScope.launch {
            imageProcessingDataManager.enhanceContrast.collect { _enhanceContrast.value = it }
        }
        viewModelScope.launch {
            imageProcessingDataManager.unSharpMasking.collect { _unsharpMasking.value = it }
        }

        viewModelScope.launch {
            imageProcessingDataManager.otsu.collect { _otsu.value = it }
        }

        viewModelScope.launch {
            imageProcessingDataManager.deSkew.collect { _deskew.value = it }
        }
    }

    fun initOCR(onProgress: (Int) -> TessBaseAPI.ProgressNotifier) = viewModelScope.launch {
        ocr = ImageTextReader(
            path = tessDataSource.value,
            pageSegMode = pageSegMode.value,
            ocrMode = ocrMode.value,
            languages = selectedLanguages.value,
            parameters = jCModifiers.value.getParameters(),
            isParameterSet = enableJCModifiers.value,
        ) {
            onProgress(it.percent)
        }
    }

    fun preProcessBitmap(bitmap: Bitmap): Bitmap {
        var pix = preparePix(bitmap)

        if (enhanceContrast.value) {
            pix = AdaptiveMap.pixContrastNorm(pix)
        }

        if (unsharpMasking.value) {
            pix = Enhance.unsharpMasking(pix)
        }

        if (otsu.value) {
            pix = Binarize.otsuAdaptiveThreshold(pix)
        }

        if (deskew.value) {
            pix = rotateToCorrectSkew(pix)
        }

        return WriteFile.writeBitmap(pix)
    }

    private fun preparePix(bitmap: Bitmap): Pix {
        return Convert.convertTo8(ReadFile.readBitmap(bitmap.copy(Bitmap.Config.ARGB_8888, true)))
    }

    private fun rotateToCorrectSkew(pix: Pix): Pix {
        val skewAngle = Skew.findSkew(pix)
        return Rotate.rotate(pix, skewAngle)
    }

    fun addHistory(history: History) = viewModelScope.launch {
        historyRepository.insert(history = history)
    }

    fun deleteHistory(history: History) = viewModelScope.launch {
        historyRepository.delete(history = history)
    }


}