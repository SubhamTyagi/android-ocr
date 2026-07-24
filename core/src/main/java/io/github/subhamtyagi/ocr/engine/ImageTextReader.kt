package io.github.subhamtyagi.ocr.engine

import android.graphics.Bitmap
import android.text.Html
import android.util.Log
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
        val languageString = languages.joinToString("+") { it.code }
        Log.d("ImageTextReader", "Initializing Tesseract with languages: $languageString at path: $path")
        
        success = if (isParameterSet) {
            api.init(
                path,
                languageString,
                ocrMode,
                parameters
            )

        } else{
            api.init(
                path,
                languageString
            )
        }
        Log.d("ImageTextReader", "Tesseract initialization success: $success")
        api.setPageSegMode(pageSegMode)

    }

    val TAG="ImageTextReader"
    /**
     * Get the text from bitmap
     */
    fun getTextFromBitmap(bitmap: Bitmap): String {
        if (!success) return "OCR engine was not initialized successfully."
        if (bitmap.isRecycled) return "Provided bitmap is recycled."

        return try {
            Log.d(TAG, "getTextFromBitmap: get text called with bitmap $bitmap")
            api.setImage(bitmap)
            Log.d(TAG, "getTextFromBitmap: image set")
            val textOnImage = api.getHOCRText(1) ?: ""
            Log.d(TAG, "getTextFromBitmap: text gethocrtext called")
            val cleanText = Html.fromHtml(textOnImage).toString().trim { it <= ' ' };
            Log.d(TAG, "getTextFromBitmap: get text html")
            if (textOnImage.isEmpty()) {
                "Scan Failed: Couldn't read the image\nProblem may be related to Tesseract or no Text on Image!"
            } else {
                cleanText
            }
        } catch (e: Exception) {
            "Scan Failed: WTF: Must be reported to developer!\n ${e.stackTraceToString()}"
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
