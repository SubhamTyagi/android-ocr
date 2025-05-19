package io.github.subhamtyagi.ocr.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.subhamtyagi.ocr.data.dao.HistoryDao
import io.github.subhamtyagi.ocr.data.model.History

@Database(entities = [History::class], version = 1, exportSchema = false)
abstract class HistoryDatabase: RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}