package com.practicum.playlistmaker.domain.interactors

import com.practicum.playlistmaker.data.SearchHistory
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executors

class TracksInteractorImpl(
    private val repository: TracksRepository,
    private val searchHistory: SearchHistory
) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            val foundTracks = repository.search(expression).map { track ->
                track.copy(trackTimeMillis = formatTrackDuration(track.trackTimeMillis))
            }
            consumer.consume(foundTracks)
        }
    }

    override fun getSearchHistory(): List<Track> {
        return searchHistory.getHistory()
    }

    override fun saveToHistory(track: Track) {
        searchHistory.saveToHistory(track)
    }

    override fun clearHistory() {
        searchHistory.clearHistory()
    }

    private fun formatTrackDuration(durationMs: String): String {
        return try {
            val durationLong = durationMs.toLong()
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(durationLong)
        } catch (e: NumberFormatException) {
            "00:00"
        }
    }
}
