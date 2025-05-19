package io.github.subhamtyagi.ocr.downloader


interface DownloadProgressListener {
    fun update(bytesRead: Long, contentLength: Long, done: Boolean)
}
