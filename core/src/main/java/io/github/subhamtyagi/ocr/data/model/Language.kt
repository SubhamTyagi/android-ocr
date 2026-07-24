package io.github.subhamtyagi.ocr.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Language(
    val code: String,
    val name: String,
    var isDownloaded: Boolean=false,
    var downloadedProgress:Int=-1,
    var isSelected: Boolean = false
){
    override fun equals(other: Any?): Boolean {
        return other is Language && code== other.code
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}