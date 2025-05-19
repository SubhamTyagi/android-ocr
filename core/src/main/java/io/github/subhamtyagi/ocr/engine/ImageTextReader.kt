package io.github.subhamtyagi.ocr.engine

import android.graphics.Bitmap
import com.googlecode.tesseract.android.TessBaseAPI

//TODO: change this value
var KEY_OCR_PSM_MODE=""

class ImageTextReader {

    var success: Boolean = false

    companion object {
         private var api: TessBaseAPI? = null

        /**
         * Initialize and train the tesseract engine
         */
        fun getInstance(
            path: String,
            languages: Set<String>,
            pageSegMode: Int,
            parameters: Map<String, String>,
            isParameterSet: Boolean,
            progressNotifier: TessBaseAPI.ProgressNotifier?
        ): ImageTextReader? {
            return try {
                val imageTextReader = ImageTextReader()
                api = TessBaseAPI(progressNotifier)
                val success = api?.init(
                    path,
                    languages.joinToString("+")
                ) == true
                api?.setPageSegMode(pageSegMode)
                imageTextReader.success = success

                if (isParameterSet) {
                    parameters.forEach { (key, value) ->
                        if (key != KEY_OCR_PSM_MODE) {
                            api?.setVariable(key, value)
                        }
                    }
                }
                imageTextReader
            } catch (e: Exception) {
                null
            }
        }
    }


    /**
     * Get the text from bitmap
     */
    fun getTextFromBitmap(bitmap: Bitmap): String {
        return try {
            api?.setImage(bitmap)
            val textOnImage = api?.getHOCRText(1) ?: ""
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
        api?.stop()
    }

    /**
     * Get the mean confidence
     */
    fun getAccuracy(): Int {
        return api?.meanConfidence() ?: 0
    }

    /**
     * Closes down tesseract and free up all memory.
     */
    fun tearDownEverything() {
        api?.recycle()
    }

    /**
     * Frees up recognition results and any stored image data.
     */
    fun clearPreviousImage() {
        api?.clear()
    }
}
