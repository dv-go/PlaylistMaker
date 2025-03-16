package com.practicum.playlistmaker.search.data.repository

import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.dto.SearchHistoryDto
import com.practicum.playlistmaker.search.data.dto.toDto
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.settings.data.datasource.SearchHistoryDataSource
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository

class SearchHistoryRepositoryImpl(
    private val searchHistoryDataSource: SearchHistoryDataSource
) : SearchHistoryRepository {

    private val HISTORY_KEY = "search_history"
    private val MAX_HISTORY_SIZE = 10
    private val dto = SearchHistoryDto(Gson())

    override fun getHistory(): List<Track> {
        val historyJson = searchHistoryDataSource.getHistoryJson(HISTORY_KEY)
        val trackDtoList = dto.fromJson(historyJson)
        return trackDtoList.map { it.toDomain() }
    }

    override fun saveToHistory(track: Track) {
        val currentHistory = getHistory().toMutableList()
        currentHistory.removeAll { it.trackId == track.trackId }
        currentHistory.add(0, track)

        if (currentHistory.size > MAX_HISTORY_SIZE) {
            currentHistory.removeAt(currentHistory.lastIndex)
        }

        val trackDtoList = currentHistory.map { it.toDto() }
        val historyJson = dto.toJson(trackDtoList)
        searchHistoryDataSource.saveHistoryJson(HISTORY_KEY, historyJson)
    }

    override fun clearHistory() {
        searchHistoryDataSource.clearHistory(HISTORY_KEY)
    }
}
