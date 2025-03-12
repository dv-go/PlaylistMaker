package com.practicum.playlistmaker.sharing.data

import android.content.SharedPreferences
import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.dto.SearchHistoryDto
import com.practicum.playlistmaker.search.data.dto.toDto
import com.practicum.playlistmaker.search.domain.models.Track

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    private val HISTORY_KEY = "search_history"
    private val MAX_HISTORY_SIZE = 10
    private val dto = SearchHistoryDto(Gson())

    fun getHistory(): List<Track> {
        val historyJson = sharedPreferences.getString(HISTORY_KEY, null)
        val trackDtoList = dto.fromJson(historyJson)
        return trackDtoList.map { it.toDomain() }
    }

    fun saveToHistory(track: Track) {
        val currentHistory = getHistory().toMutableList()
        currentHistory.removeAll { it.trackId == track.trackId }
        currentHistory.add(0, track)

        if (currentHistory.size > MAX_HISTORY_SIZE) {
            currentHistory.removeAt(currentHistory.lastIndex)
        }

        saveHistory(currentHistory)
    }

    fun clearHistory() {
        sharedPreferences.edit().remove(HISTORY_KEY).apply()
    }

    private fun saveHistory(history: List<Track>) {
        val trackDtoList = history.map { it.toDto() }
        val historyJson = dto.toJson(trackDtoList)
        sharedPreferences.edit().putString(HISTORY_KEY, historyJson).apply()
    }
}
