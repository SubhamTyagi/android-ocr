package io.github.subhamtyagi.ocr.data


import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import androidx.annotation.RequiresPermission


object Utils {

    fun getDownloadUrl(lang: String): String {
        return when (lang) {
            "akk" -> Constants.TESSERACT_DATA_DOWNLOAD_URL_AKK_BEST
            "eqo" -> Constants.TESSERACT_DATA_DOWNLOAD_URL_EQU
            else -> String.format(Constants.TESSERACT_DATA_DOWNLOAD_URL_BEST, lang)
        }
    }

    private const val DEFAULT_LANGUAGE = "eng"

    @SuppressLint("DefaultLocale")
    fun getSize(size: Int): String {
        return when {
            size < 0 -> "Invalid size"
            size < 1024 -> "$size Bytes"
            else -> {
                val kb = size / 1024.0
                if (kb < 1024) {
                    String.format("%.2f KB", kb)
                } else {
                    val mb = kb / 1024.0
                    String.format("%.2f MB", mb)
                }
            }
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isNetworkAvailable(application: Application): Boolean {
        val connectivityManager =
            application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nw = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(nw)
        return (actNw != null) && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || actNw.hasTransport(
            NetworkCapabilities.TRANSPORT_CELLULAR
        ) || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) || actNw.hasTransport(
            NetworkCapabilities.TRANSPORT_BLUETOOTH
        ))

    }
}
