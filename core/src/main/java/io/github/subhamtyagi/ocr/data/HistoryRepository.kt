package io.github.subhamtyagi.ocr.data

import io.github.subhamtyagi.ocr.data.dao.HistoryDao
import io.github.subhamtyagi.ocr.data.model.History
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepository @Inject constructor(private val historyDao: HistoryDao) {
    /*fun getHistoryList(): ArrayList<History> {
        val list = arrayListOf<History>()
        repeat(5) {
            list.add(
                History(
                    title = "Title $it",
                    ocrText = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.",
                    imagePath = "R.drawable.drawable_default_image_60"
                )
            )
        }
        return list
    }*/
    fun getHistoryList() = historyDao.getHistory()
    suspend fun insert(history: History) = historyDao.insertHistory(history = history)
    suspend fun delete(history: History) = historyDao.deleteHistory(history = history)
}