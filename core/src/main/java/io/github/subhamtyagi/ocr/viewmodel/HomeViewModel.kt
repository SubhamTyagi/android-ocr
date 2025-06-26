package io.github.subhamtyagi.ocr.viewmodel

import android.content.Context
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
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.subhamtyagi.ocr.data.HistoryRepository
import io.github.subhamtyagi.ocr.data.datastore.ImageProcessingDataManager
import io.github.subhamtyagi.ocr.data.datastore.LanguageDataManager
import io.github.subhamtyagi.ocr.data.datastore.TesseractParameterDataManager
import io.github.subhamtyagi.ocr.data.room.History
import io.github.subhamtyagi.ocr.data.model.JCMState
import io.github.subhamtyagi.ocr.data.model.Language
import io.github.subhamtyagi.ocr.engine.ImageTextReader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
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

    private val _ocrMode = MutableStateFlow(0)
    val ocrMode: StateFlow<Int> = _ocrMode.asStateFlow()

    private val _enableJCModifiers = MutableStateFlow(false)
    val enableJCModifiers: StateFlow<Boolean> = _enableJCModifiers.asStateFlow()

    private val _jCModifiers = MutableStateFlow(JCMState())
    val jCModifiers: StateFlow<JCMState> = _jCModifiers.asStateFlow()

    private val _enhanceContrast = MutableStateFlow(false)
    val enhanceContrast: StateFlow<Boolean> = _enhanceContrast.asStateFlow()

    private val _unSharpMasking = MutableStateFlow(false)
    val unSharpMasking: StateFlow<Boolean> = _unSharpMasking.asStateFlow()

    private val _otsu = MutableStateFlow(false)
    val otsu: StateFlow<Boolean> = _otsu.asStateFlow()

    private val _deSkew = MutableStateFlow(false)
    val deSkew: StateFlow<Boolean> = _deSkew.asStateFlow()

    private val _hasSettingsChanged = MutableStateFlow(true)
    val hasSettingsChanged: StateFlow<Boolean> = _hasSettingsChanged.asStateFlow()

    var ocr: ImageTextReader? = null

//    var hasSettingsChanged: StateFlow<Boolean> = combine(
//        _selectedLanguage,
//        _pageSegMode,
//        _enableJCModifiers,
//        _jCModifiers,
//        _enhanceContrast,
//        _unSharpMasking,
//        _otsu,
//        _deSkew
//    ) { true }.stateIn(viewModelScope, SharingStarted.Eagerly, true)

    init {
        viewModelScope.launch {
            combine(
                _selectedLanguage,
                _pageSegMode,
                _enableJCModifiers,
                _jCModifiers,
                _enhanceContrast,
                _unSharpMasking,
                _otsu,
                _deSkew
            ) {true}.collect {
                _hasSettingsChanged.value = it
            }

        }
        viewModelScope.launch {
            languageDataManager.selectedLanguages.collect {
                _selectedLanguage.value = it
            }
        }

        viewModelScope.launch {
            tesseractParameterDataManager.ocrMode.collect {
                _ocrMode.value = it
            }
        }

        viewModelScope.launch {
            tesseractParameterDataManager.preserveInterWordSpaces.collect {
                _jCModifiers.value = _jCModifiers.value.copy(preserveInterWordSpaces = it)
            }
        }

        viewModelScope.launch {
            tesseractParameterDataManager.chopEnable.collect {
                _jCModifiers.value = jCModifiers.value.copy(chopEnable = it)
            }
        }
        viewModelScope.launch {
            tesseractParameterDataManager.languageNgramOn.collect {
                _jCModifiers.value = jCModifiers.value.copy(languageNgramOn = it)
            }
        }
        viewModelScope.launch {
            tesseractParameterDataManager.textortForceMakePropWords.collect {
                _jCModifiers.value = jCModifiers.value.copy(textortForceMakePropWords = it)
            }
        }
        viewModelScope.launch {
            tesseractParameterDataManager.edgeMaxChildrenPerOutline.collect {
                _jCModifiers.value = jCModifiers.value.copy(edgeMaxChildrenPerOutline = it)
            }
        }

        viewModelScope.launch {
            imageProcessingDataManager.enhanceContrast.collect {
                _enhanceContrast.value = it
            }
        }
        viewModelScope.launch {
            imageProcessingDataManager.unSharpMasking.collect {
                _unSharpMasking.value = it
            }
        }

        viewModelScope.launch {
            imageProcessingDataManager.otsu.collect {
                _otsu.value = it
            }
        }

        viewModelScope.launch {
            imageProcessingDataManager.deSkew.collect {
                _deSkew.value = it
            }
        }
    }


    fun initOCR(context: Context, onProgress: (Int) -> Unit) = viewModelScope.launch {

        val baseDir = File(context.filesDir, "best")
        ocr?.let {
            it.stop()
            it.tearDownEverything()
        }
        //Todo check for all languages are downloaded,
        ocr = ImageTextReader(
            path = baseDir.absolutePath,
            pageSegMode = pageSegMode.value,
            ocrMode = ocrMode.value,
            languages = selectedLanguages.value,
            parameters = jCModifiers.value.getParameters(),
            isParameterSet = enableJCModifiers.value,
        ) {
            onProgress(it.percent)
        }
        _hasSettingsChanged.value = false
    }

    override fun onCleared() {
        ocr?.stop()
        ocr?.tearDownEverything()
        super.onCleared()
    }

    fun getTextFromBitmap(bitmap: Bitmap): String {
        return ocr?.getTextFromBitmap(preProcessBitmap(bitmap)) ?: "OCR not initialized properly."
    }

    fun saveBitmapToStorage(context: Context, bitmap: Bitmap): File {
        val fileName = "cropped_image_${System.currentTimeMillis()}.png"
        val file = File(context.filesDir, fileName)
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }
        return file
    }

    fun preProcessBitmap(bitmap: Bitmap): Bitmap {
        var pix = preparePix(bitmap)

        if (enhanceContrast.value) {
            pix = AdaptiveMap.pixContrastNorm(pix)
        }

        if (unSharpMasking.value) {
            pix = Enhance.unsharpMasking(pix)
        }

        if (otsu.value) {
            pix = Binarize.otsuAdaptiveThreshold(pix)
        }

        if (deSkew.value) {
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