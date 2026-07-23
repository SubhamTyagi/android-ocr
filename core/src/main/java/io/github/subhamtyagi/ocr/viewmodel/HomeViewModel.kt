package io.github.subhamtyagi.ocr.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
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
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.subhamtyagi.ocr.data.HistoryRepository
import io.github.subhamtyagi.ocr.data.datastore.ImageProcessingDataManager
import io.github.subhamtyagi.ocr.data.datastore.LanguageDataManager
import io.github.subhamtyagi.ocr.data.datastore.TesseractParameterDataManager
import io.github.subhamtyagi.ocr.data.model.JCMState
import io.github.subhamtyagi.ocr.data.model.Language
import io.github.subhamtyagi.ocr.data.room.History
import io.github.subhamtyagi.ocr.engine.ImageTextReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

data class HomeUiState(
    val history: List<History> = emptyList(),
    val selectedLanguages: Set<Language> = emptySet(),
    val isProcessing: Boolean = false,
    val ocrProgress: Int = 100,
    val errorMessage: String? = null
)

@OptIn(kotlinx.coroutines.FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val tesseractParameterDataManager: TesseractParameterDataManager,
    private val imageProcessingDataManager: ImageProcessingDataManager,
    private val languageDataManager: LanguageDataManager,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _isProcessing = MutableStateFlow(false)
    private val _ocrProgress = MutableStateFlow(100)
    private val _errorMessage = MutableStateFlow<String?>(null)

    private val _selectedLanguage = MutableStateFlow<Set<Language>>(emptySet())
    private val _pageSegMode = MutableStateFlow(6)
    private val _ocrMode = MutableStateFlow(0)
    private val _enableJCModifiers = MutableStateFlow(false)
    private val _jCModifiers = MutableStateFlow(JCMState())
    private val _enhanceContrast = MutableStateFlow(false)
    private val _unSharpMasking = MutableStateFlow(false)
    private val _otsu = MutableStateFlow(false)
    private val _deSkew = MutableStateFlow(false)

    private val ocrMutex = Mutex()

    val uiState: StateFlow<HomeUiState> = combine(
        historyRepository.getHistoryList(),
        _selectedLanguage,
        _isProcessing,
        _ocrProgress,
        _errorMessage
    ) { args ->
        HomeUiState(
            history = args[0] as List<History>,
            selectedLanguages = args[1] as Set<Language>,
            isProcessing = args[2] as Boolean,
            ocrProgress = args[3] as Int,
            errorMessage = args[4] as? String
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    private var ocr: ImageTextReader? = null

    init {
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            combine(
                _selectedLanguage,
                _pageSegMode,
                _enableJCModifiers,
                _ocrMode,
                _jCModifiers,
                _enhanceContrast,
                _unSharpMasking,
                _otsu,
                _deSkew
            ) { args -> args.toList() }
                .debounce(300.milliseconds)
                .distinctUntilChanged()
                .collect {
                    Log.d("HomeViewModel", "observeSettings: Settings changed, initializing OCR")
                    initOCR()
                }
        }

        viewModelScope.launch {
            languageDataManager.selectedLanguages.collect { _selectedLanguage.value = it }
        }

        viewModelScope.launch {
            tesseractParameterDataManager.ocrMode.collect { _ocrMode.value = it }
        }

        viewModelScope.launch {
            tesseractParameterDataManager.pageSegMode.collect { _pageSegMode.value = it }
        }

        viewModelScope.launch {
            tesseractParameterDataManager.enableJCModifier.collect { _enableJCModifiers.value = it }
        }

        viewModelScope.launch {
            combine(
                tesseractParameterDataManager.preserveInterWordSpaces,
                tesseractParameterDataManager.chopEnable,
                tesseractParameterDataManager.languageNgramOn,
                tesseractParameterDataManager.textortForceMakePropWords,
                tesseractParameterDataManager.edgeMaxChildrenPerOutline
            ) { preserve, chop, ngram, textort, edge ->
                JCMState(preserve, chop, ngram, textort, edge)
            }.collect { _jCModifiers.value = it }
        }

        viewModelScope.launch {
            imageProcessingDataManager.enhanceContrast.collect { _enhanceContrast.value = it }
        }
        viewModelScope.launch {
            imageProcessingDataManager.unSharpMasking.collect { _unSharpMasking.value = it }
        }
        viewModelScope.launch {
            imageProcessingDataManager.otsu.collect { _otsu.value = it }
        }
        viewModelScope.launch {
            imageProcessingDataManager.deSkew.collect { _deSkew.value = it }
        }
    }

    fun initOCR() = viewModelScope.launch {
        ocrMutex.withLock {
            val allSelectedLanguages = _selectedLanguage.value
            val downloadedLanguages = allSelectedLanguages.filter {
                languageDataManager.isLanguageDataDownloaded(it.code)
            }

            if (downloadedLanguages.isEmpty()) {
                Log.d(
                    "HomeViewModel",
                    "initOCR: No downloaded languages selected. Selected: ${allSelectedLanguages.map { it.code }}"
                )
                ocr?.let {
                    it.stop()
                    it.tearDownEverything()
                }
                ocr = null
                return@launch
            }

            Log.d(
                "HomeViewModel",
                "initOCR: Initializing with ${downloadedLanguages.map { it.code }}"
            )
            val baseDir = File(context.filesDir, "best")
            ocr?.let {
                it.stop()
                it.tearDownEverything()
            }

            ocr = ImageTextReader(
                path = baseDir.absolutePath,
                pageSegMode = _pageSegMode.value,
                ocrMode = _ocrMode.value,
                languages = downloadedLanguages.toSet(),
                parameters = _jCModifiers.value.getParameters(),
                isParameterSet = _enableJCModifiers.value,
            ) {
                _ocrProgress.value = it.percent
            }
        }
    }

    fun processImage(uri: Uri) = viewModelScope.launch(Dispatchers.IO) {
        _isProcessing.value = true
        _errorMessage.value = null
        try {
            val bitmap = if (Build.VERSION.SDK_INT < 28) {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source)
            }

            val processedBitmap = preProcessBitmap(bitmap)

            val text = ocrMutex.withLock {
                ocr?.getTextFromBitmap(processedBitmap)
            } ?: "OCR not initialized properly."

            processedBitmap.recycle()

            val fileName = "cropped_image_${System.currentTimeMillis()}.png"
            val file = File(context.filesDir, fileName)
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
            bitmap.recycle()

            historyRepository.insert(
                History(
                    title = "Ocr Text",
                    ocrText = text,
                    imagePath = file.absolutePath
                )
            )
        } catch (e: Exception) {
            _errorMessage.value = "Failed to process image: ${e.message}"
            e.printStackTrace()
        } finally {
            _isProcessing.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        ocr?.stop()
        ocr?.tearDownEverything()
        super.onCleared()
    }

    fun preProcessBitmap(bitmap: Bitmap): Bitmap {
        var pix = preparePix(bitmap)

        if (_enhanceContrast.value) {
            val oldPix = pix
            pix = AdaptiveMap.pixContrastNorm(pix)
            oldPix.recycle()
        }

        if (_unSharpMasking.value) {
            val oldPix = pix
            pix = Enhance.unsharpMasking(pix)
            oldPix.recycle()
        }

        if (_otsu.value) {
            val oldPix = pix
            pix = Binarize.otsuAdaptiveThreshold(pix)
            oldPix.recycle()
        }

        if (_deSkew.value) {
            val oldPix = pix
            pix = rotateToCorrectSkew(pix)
            oldPix.recycle()
        }

        val resultBitmap = WriteFile.writeBitmap(pix)
        pix.recycle()
        return resultBitmap
    }

    private fun preparePix(bitmap: Bitmap): Pix {
        val copy = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val originalPix = ReadFile.readBitmap(copy)
        val pix = Convert.convertTo8(originalPix)
        originalPix.recycle()
        copy.recycle()
        return pix
    }

    private fun rotateToCorrectSkew(pix: Pix): Pix {
        val skewAngle = Skew.findSkew(pix)
        return Rotate.rotate(pix, skewAngle)
    }

    fun deleteHistory(history: History) = viewModelScope.launch {
        historyRepository.delete(history = history)
    }
}
