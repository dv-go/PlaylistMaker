package com.practicum.playlistmaker.search.data.datasource

interface SearchHistoryDataSource {
    fun getHistoryJson(key: String): String?
    fun saveHistoryJson(key: String, json: String)
    fun clearHistory(key: String)
}
