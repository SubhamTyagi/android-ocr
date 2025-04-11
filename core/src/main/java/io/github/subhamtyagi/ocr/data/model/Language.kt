package io.github.subhamtyagi.ocr.data.model

data class Language(
    val key: String,
    val name: String,
    var isDownloaded: Boolean,
    var isSelected:Boolean
)