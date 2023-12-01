package io.github.subhamtyagi.ocr.tasker;

import android.content.Context
import com.googlecode.tesseract.android.TessBaseAPI
import io.github.subhamtyagi.ocr.utils.SpUtil
import io.github.subhamtyagi.ocr.utils.Utils
import java.io.File

interface OCRFactory {
    fun createAPI(): Any
}

class TessBaseOCRFactory(private val context: Context) : OCRFactory{
    override fun createAPI(): TessBaseAPI {
        SpUtil.getInstance().init(context)
        val mLanguage = Utils.getTrainingDataLanguage()
        val mPageSegMode = Utils.getPageSegMode()
        val dir = File(context.getExternalFilesDir(Utils.getTrainingDataType().toString())!!.absolutePath)
        val mEngineMode = Utils.getEngineMode();
        val api = TessBaseAPI()
        api.init(dir.absolutePath, mLanguage, mEngineMode)
        api.pageSegMode = mPageSegMode
        return api
    }
}