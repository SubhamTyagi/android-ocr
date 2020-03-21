package io.github.subhamtyagi.ocr.utils;

import android.os.Environment;

/**
 * Various constant: Self explanatory
 */
public class Constants {


    /***
     *Paths to tesseract directory( only used to create to directory)
     */
    public static final String PATH_OF_TESSERACT_DATA_BEST = Environment.getExternalStorageDirectory() + "/tesseract4/best/tessdata/";
    public static final String PATH_OF_TESSERACT_DATA_FAST = Environment.getExternalStorageDirectory() + "/tesseract4/fast/tessdata/";
    public static final String PATH_OF_TESSERACT_DATA_STANDARD = Environment.getExternalStorageDirectory() + "/tesseract4/standard/tessdata/";

    /***
     * folder path used for training Tesseract Api
     */

    public static final String TESSERACT_PATH_BEST = Environment.getExternalStorageDirectory() + "/tesseract4/best/";
    public static final String TESSERACT_PATH_FAST = Environment.getExternalStorageDirectory() + "/tesseract4/fast/";
    public static final String TESSERACT_PATH_STANDARD = Environment.getExternalStorageDirectory() + "/tesseract4/standard/";

    /***
     *TRAINING DATA URL TEMPLATES for downloading
     */
    public static final String TESSERACT_DATA_DOWNLOAD_URL_BEST = "https://github.com/tesseract-ocr/tessdata_best/raw/4.0.0/%s.traineddata";
    public static final String TESSERACT_DATA_DOWNLOAD_URL_STANDARD = "https://github.com/tesseract-ocr/tessdata/raw/4.0.0/%s.traineddata";
    public static final String TESSERACT_DATA_DOWNLOAD_URL_FAST = "https://github.com/tesseract-ocr/tessdata_fast/raw/4.0.0/%s.traineddata";

    /**
     * TRAINING DATA FILEs to save
     */
    public static final String TESSERACT_DATA_FILE_NAME_BEST = Environment.getExternalStorageDirectory() + "/tesseract4/best/tessdata/%s.traineddata";
    public static final String TESSERACT_DATA_FILE_NAME_FAST = Environment.getExternalStorageDirectory() + "/tesseract4/fast/tessdata/%s.traineddata";
    public static final String TESSERACT_DATA_FILE_NAME_STANDARD = Environment.getExternalStorageDirectory() + "/tesseract4/standard/tessdata/%s.traineddata";


}
