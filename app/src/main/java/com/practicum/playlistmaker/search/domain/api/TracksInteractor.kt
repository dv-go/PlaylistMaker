package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.viewmodel.ResultWrapper

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)
    fun getSearchHistory(): List<Track>
    fun saveToHistory(track: Track)
    fun clearHistory()

    interface TracksConsumer {
        fun consume(result: ResultWrapper)
    }

}
