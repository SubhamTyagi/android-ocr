package io.github.subhamtyagi.ocr.data.model

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class History(
    @PrimaryKey(autoGenerate = true) val id: Int=0,
    val title: String,
    val ocrText: String,
    val imagePath: String
)
