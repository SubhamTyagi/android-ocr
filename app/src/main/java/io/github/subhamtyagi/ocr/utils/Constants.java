package io.github.subhamtyagi.ocr.utils;

/**
 * Various constant: Self explanatory
 */
public class Constants {


    /***
     *TRAINING DATA URL TEMPLATES for downloading
     */
    public static final String TESSERACT_DATA_DOWNLOAD_URL_BEST = "https://github.com/tesseract-ocr/tessdata_best/raw/4.0.0/%s.traineddata";
    public static final String TESSERACT_DATA_DOWNLOAD_URL_STANDARD = "https://github.com/tesseract-ocr/tessdata/raw/4.0.0/%s.traineddata";
    public static final String TESSERACT_DATA_DOWNLOAD_URL_FAST = "https://github.com/tesseract-ocr/tessdata_fast/raw/4.0.0/%s.traineddata";

    public static final String LANGUAGE_CODE = "%s.traineddata";

    public static final String KEY_LANGUAGE_FOR_TESSERACT = "language_for_tesseract";
    public static final String KEY_ENABLE_MULTI_LANG = "key_enable_multiple_lang";
    public static final String KEY_TESS_TRAINING_DATA_SOURCE = "tess_training_data_source";
    public static final String KEY_LANGUAGE_FOR_TESSERACT_MULTI = "multi_languages";
    public static final String KEY_GRAYSCALE_IMAGE_OCR = "grayscale_image_ocr";
    public static final String KEY_LAST_USE_IMAGE_LOCATION = "last_use_image_location";
    public static final String KEY_LAST_USE_IMAGE_TEXT = "last_use_image_text";
    public static final String KEY_PERSIST_DATA = "persist_data";

    public static final String KEY_CONTRAST = "process_contrast";
    public static final String KEY_UN_SHARP_MASKING = "un_sharp_mask";
    public static final String KEY_OTSU_THRESHOLD = "otsu_threshold";
    public static final String KEY_FIND_SKEW_AND_DESKEW = "deskew_img";


}
