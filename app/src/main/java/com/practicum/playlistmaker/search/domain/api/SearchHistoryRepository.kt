package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track

interface SearchHistoryRepository {
    fun getHistory(): List<Track>
    fun saveToHistory(track: Track)
    fun clearHistory()
}