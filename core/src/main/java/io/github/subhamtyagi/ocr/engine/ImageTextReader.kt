package io.github.subhamtyagi.ocr.engine

import android.graphics.Bitmap
import com.googlecode.tesseract.android.TessBaseAPI
import io.github.subhamtyagi.ocr.data.model.Language

class ImageTextReader {
    var api: TessBaseAPI
    var success: Boolean = false

    constructor(
        path: String,
        languages: Set<Language>,
        pageSegMode: Int,
        ocrMode:Int,
        parameters: Map<String, String>,
        isParameterSet: Boolean,
        progressNotifier: TessBaseAPI.ProgressNotifier
    ) {
        api = TessBaseAPI(progressNotifier)
        success = if (isParameterSet) {
            api.init(
                path,
                languages.joinToString("+") { it.code },
                ocrMode,
                parameters
            ) == true

        } else{
            api.init(
                path,
                languages.joinToString("+") { it.code }
            ) == true
        }
        api.setPageSegMode(pageSegMode)

    }

    /**
     * Get the text from bitmap
     */
    fun getTextFromBitmap(bitmap: Bitmap): String {

        return try {
            api.setImage(bitmap)
            val textOnImage = api.getHOCRText(1) ?: ""
            if (textOnImage.isEmpty()) {
                "Scan Failed: Couldn't read the image\nProblem may be related to Tesseract or no Text on Image!"
            } else {
                textOnImage
            }
        } catch (e: Exception) {
            "Scan Failed: WTF: Must be reported to developer!"
        }
    }

    /**
     * Stop the image text reader
     */
    fun stop() {
        api.stop()
    }

    /**
     * Get the mean confidence
     */
    fun getAccuracy(): Int {
        return api.meanConfidence()
    }

    /**
     * Closes down tesseract and free up all memory.
     */
    fun tearDownEverything() {
        api.recycle()
    }

    /**
     * Frees up recognition results and any stored image data.
     */
    fun clearPreviousImage() {
        api.clear()
    }
}
