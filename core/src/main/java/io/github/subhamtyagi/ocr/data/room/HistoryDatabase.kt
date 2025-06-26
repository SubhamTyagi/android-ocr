package io.github.subhamtyagi.ocr.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.subhamtyagi.ocr.data.room.HistoryDao

@Database(entities = [History::class], version = 1, exportSchema = false)
abstract class HistoryDatabase: RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}