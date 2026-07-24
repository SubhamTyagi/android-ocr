package io.github.subhamtyagi.ocr.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.subhamtyagi.ocr.data.room.HistoryDao

@Database(entities = [History::class], version = 2, exportSchema = false)
abstract class HistoryDatabase: RoomDatabase() {
    abstract fun historyDao(): HistoryDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE history ADD COLUMN accuracy INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}