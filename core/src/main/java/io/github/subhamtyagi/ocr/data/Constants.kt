package io.github.subhamtyagi.ocr.data


object Constants {

    const val TESSERACT_DATA_DOWNLOAD_URL_BEST: String =
        "https://github.com/tesseract-ocr/tessdata_best/raw/4.0.0/%s.traineddata"
    const val TESSERACT_DATA_DOWNLOAD_URL_STANDARD: String =
        "https://github.com/tesseract-ocr/tessdata/raw/4.0.0/%s.traineddata"
    const val TESSERACT_DATA_DOWNLOAD_URL_FAST: String =
        "https://github.com/tesseract-ocr/tessdata_fast/raw/4.0.0/%s.traineddata"

    const val TESSERACT_DATA_DOWNLOAD_URL_EQU: String =
        "https://github.com/tesseract-ocr/tessdata/raw/3.04.00/equ.traineddata"

    const val TESSERACT_DATA_DOWNLOAD_URL_AKK_BEST: String =
        "https://github.com/tesseract-ocr/tessdata_contrib/raw/main/akk/best/akk.traineddata"
    const val TESSERACT_DATA_DOWNLOAD_URL_AKK_STANDARD: String =
        "https://github.com/tesseract-ocr/tessdata_contrib/raw/main/akk/legacy/akk.traineddata"
    const val TESSERACT_DATA_DOWNLOAD_URL_AKK_FAST: String =
        "https://github.com/tesseract-ocr/tessdata_contrib/raw/main/akk/fast/akk.traineddata"

    const val LANGUAGE_DATA_FILE_NAME: String = "%s.traineddata"

}