package io.github.subhamtyagi.ocr.downloader

import io.github.subhamtyagi.ocr.data.model.Language

sealed class DownloadResult {
    data class Success(val language: Language) : DownloadResult()
    data class Failure(val language: Language, val reason: String) : DownloadResult()
}