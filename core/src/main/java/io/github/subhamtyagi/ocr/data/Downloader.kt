package io.github.subhamtyagi.ocr.data


import io.github.subhamtyagi.ocr.data.model.Language
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


class Downloader(
    private val dataType: String,
    private val languages: Set<Language>
) : Runnable {

    private var size: String? = null

    override fun run() {
        //update before download

        val success = booleanArrayOf(true)
        for (lang in languages) {
            success[0] = success[0] && downloadTrainingData(dataType, lang.code)
        }

        //update after download
    }


    private fun downloadTrainingData(dataType: String, lang: String): Boolean {
        var downloadURL = getDownloadUrl(dataType, lang)

        return try {
            var conn = URL(downloadURL).openConnection() as HttpURLConnection
            conn.instanceFollowRedirects = false
            downloadURL = followRedirects(conn, downloadURL)
            conn = URL(downloadURL).openConnection() as HttpURLConnection
            conn.connect()

            val totalContentSize = conn.contentLength
            if (totalContentSize <= 0) return false

            size = Utils.getSize(totalContentSize)

            //before progress

            val input = BufferedInputStream(conn.inputStream)
            val outputFile = File("", String.format(Constants.LANGUAGE_DATA_FILE_NAME, lang))
            val output = FileOutputStream(outputFile)

            val data = ByteArray(6 * 1024)
            var downloaded = 0
            var count: Int

            while (input.read(data).also { count = it } != -1) {
                output.write(data, 0, count)
                downloaded += count
                val percentage = (downloaded * 100) / totalContentSize
                //TODO: post percentage
            }

            output.flush()
            input.close()
            output.close()
            true
        } catch (e: IOException) {
            false
        }
    }

    private fun getDownloadUrl(dataType: String, lang: String): String {
        return when (dataType) {
            "best" -> when (lang) {
                "akk" -> Constants.TESSERACT_DATA_DOWNLOAD_URL_AKK_BEST
                "eqo" -> Constants.TESSERACT_DATA_DOWNLOAD_URL_EQU
                else -> String.format(Constants.TESSERACT_DATA_DOWNLOAD_URL_BEST, lang)
            }

            "standard" -> when (lang) {
                "akk" -> Constants.TESSERACT_DATA_DOWNLOAD_URL_AKK_STANDARD
                "eqo" -> Constants.TESSERACT_DATA_DOWNLOAD_URL_EQU
                else -> String.format(Constants.TESSERACT_DATA_DOWNLOAD_URL_STANDARD, lang)
            }

            else -> when (lang) {
                "akk" -> Constants.TESSERACT_DATA_DOWNLOAD_URL_AKK_FAST
                "eqo" -> Constants.TESSERACT_DATA_DOWNLOAD_URL_EQU
                else -> String.format(Constants.TESSERACT_DATA_DOWNLOAD_URL_FAST, lang)
            }
        }
    }

    @Throws(IOException::class)
    private fun followRedirects(conn: HttpURLConnection, downloadURL: String): String {
        var currentURL = downloadURL
        var connection = conn

        while (true) {
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_MOVED_PERM ||
                responseCode == HttpURLConnection.HTTP_MOVED_TEMP
            ) {
                val location = connection.getHeaderField("Location")
                val base = URL(currentURL)
                currentURL = URL(base, location).toExternalForm()
                connection = URL(currentURL).openConnection() as HttpURLConnection
            } else {
                break
            }
        }
        return currentURL
    }
}
