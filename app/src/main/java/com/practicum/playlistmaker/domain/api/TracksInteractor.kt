package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)
    fun getSearchHistory(): List<Track>
    fun saveToHistory(track: Track)
    fun clearHistory()

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>)
    }
}
