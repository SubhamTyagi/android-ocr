package io.github.subhamtyagi.ocr.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Language(
    val code: String,
    val name: String,
    var isDownloaded: Boolean,
    var isSelected: Boolean = false
)