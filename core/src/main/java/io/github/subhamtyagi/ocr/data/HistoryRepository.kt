package io.github.subhamtyagi.ocr.data

import io.github.subhamtyagi.ocr.data.room.History
import io.github.subhamtyagi.ocr.data.room.HistoryDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(private val historyDao: HistoryDao) {
    fun getHistoryList() = historyDao.getHistory()
    suspend fun insert(history: History) = historyDao.insertHistory(history = history)
    suspend fun delete(history: History) = historyDao.deleteHistory(history = history)
}