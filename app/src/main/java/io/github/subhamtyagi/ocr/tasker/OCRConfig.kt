package io.github.subhamtyagi.ocr.tasker

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.core.content.FileProvider
import com.googlecode.tesseract.android.TessBaseAPI
import com.googlecode.tesseract.android.TessBaseAPI.PageIteratorLevel
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigHelper
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.theartofdev.edmodo.cropper.BitmapUtils
import com.theartofdev.edmodo.cropper.CropFileProvider
import io.github.subhamtyagi.ocr.databinding.ActivityConfigOcrBinding
import io.github.subhamtyagi.ocr.tasker.ChannelManager.channelResult
import io.github.subhamtyagi.ocr.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File


class BackgroundWork: CoroutineScope by MainScope() {
    suspend fun readText(context: Context, imagePathName: String) {
        launch {
            // start OCR
            // Use the withContext function to switch context. The withContext function
            // can switch to the specified scheduler inside the coroutine.
            withContext(IO) {
                val result = readTextFromOCRAPI(context, imagePathName)
                channelResult.send(result)
            }
        }
    }
    private fun readTextFromOCRAPI(context: Context, pathName: String): OCROutput {
        pathName.ifEmpty { return OCROutput("Scan Failed: Must choose image file!","") }
        val imageFile = File(pathName)
        val imageUri = FileProvider.getUriForFile(context, CropFileProvider.authority(context), imageFile)
        var bitmap = BitmapUtils.decodeSampledBitMap(context, imageUri)?.bitmap
        // call image reader api
        val output = TessBaseOCRFactory(context).let {
            val api = it.createAPI()
            if (Utils.isPreProcessImage()) bitmap = Utils.preProcessBitmap(bitmap)
            api.setImage(bitmap)
            val textOnImage = try {
                api.utF8Text
            } catch (e: Exception) {
                "Scan Failed: WTF: Must be reported to developer!"
            }
            textOnImage.ifEmpty {
                OCROutput("Scan Failed: Couldn't read the image\nProblem may be related to Tesseract or no Text on Image!","")
            }
            val coordinatesData = assembleCoordinatesData(api)
            OCROutput(textOnImage, coordinatesData)
        }
        return output
    }

    /**
     * coordinates with top-left of the top-left contained pixel
     * to the bottom-right of the bottom-right contained pixel
     * @return A map. Key for each individual symbol, value for an array of coordinates
     */
    private fun readCoordinates(api: TessBaseAPI): Map<String, Array<IntArray>> {
        val coordinatesMap: MutableMap<String, Array<IntArray>> = mutableMapOf()
        val resultIterator = api.resultIterator
        resultIterator.begin()
        do {
            val symbol = resultIterator.getUTF8Text(PageIteratorLevel.RIL_SYMBOL)
            val boundingBox = resultIterator.getBoundingBox(PageIteratorLevel.RIL_SYMBOL)

            if (symbol.isNotBlank()) {
                coordinatesMap[symbol] = arrayOf(boundingBox)
            }

        } while (resultIterator.next(PageIteratorLevel.RIL_SYMBOL))
        resultIterator.delete()

        return coordinatesMap
    }

    /**
     * Assemble the key-value information for the coordinates of each individual symbol.
     * @return JSON string. eg:{"1":[[151,33,154,72]],"0":[[151,33,171,67]],"g":[[176,44,196,72]]}
     */
    private fun assembleCoordinatesData(api: TessBaseAPI): String {
        val coordinatesMap = readCoordinates(api)
        val jsonObject = JSONObject()
        coordinatesMap.forEach { (symbol, boundingBoxes) ->
            jsonObject.put(symbol, JSONArray(boundingBoxes.map { JSONArray(it) }))
        }
        Log.d("json data","===>$jsonObject")
        return jsonObject.toString()
    }

}

class BackgroundHelper(config: TaskerPluginConfig<OCRInput>): TaskerPluginConfigHelper<OCRInput,OCROutput, OCRRunner>(config){
    override val inputClass get() = OCRInput::class.java
    override val outputClass get() = OCROutput::class.java
    override val runnerClass = OCRRunner::class.java

    override fun addToStringBlurb(input: TaskerInput<OCRInput>, blurbBuilder: StringBuilder) {
        blurbBuilder.append("\nConfigure some variables to perform actions ")
    }
}

class ActivityConfigOCR : ActivityConfigTasker<OCRInput,OCROutput,OCRRunner,BackgroundHelper,ActivityConfigOcrBinding>() {

    private val taskHelper by lazy {BackgroundHelper(this)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding?.editImagePathName?.setOnClickListener { showVariableDialog() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        finishForTasker()
    }

    private fun showVariableDialog() {
        val relevantVariables = taskerHelper.relevantVariables.toList()
        if (relevantVariables.isEmpty()) return "No variables to select.\n\nCreate some local variables in Tasker to show here.".toToast(this)
        selectOne("Select a Tasker variable", relevantVariables) { binding?.editImagePathName?.setText(it) }
    }
    private fun finishForTasker() {
        taskHelper.finishForTasker()
    }

    override fun getNewHelper(config: TaskerPluginConfig<OCRInput>) = BackgroundHelper(config)

    override fun inflateBinding(layoutInflater: LayoutInflater) = ActivityConfigOcrBinding.inflate(layoutInflater)

    override val inputForTasker get() = TaskerInput(OCRInput(binding?.editImagePathName?.text?.toString(),binding?.editResultVariableName?.text?.toString()))

    override fun assignFromInput(input: TaskerInput<OCRInput>) {
        input.regular.run {
            binding?.editImagePathName?.setText(imagePathName)
            binding?.editResultVariableName?.setText(resultVariableName)
        }
    }

}