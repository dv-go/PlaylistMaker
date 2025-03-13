package com.practicum.playlistmaker.search.domain.interactors

import com.practicum.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.viewmodel.ResultWrapper
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executors

class TracksInteractorImpl(
    private val repository: TracksRepository,
    private val searchHistory: SearchHistoryRepositoryImpl
) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            try {
                val foundTracks = repository.search(expression).map { track ->
                    track.copy(trackTimeMillis = formatTrackDuration(track.trackTimeMillis))
                }
                if (foundTracks.isEmpty()) {
                    consumer.consume(ResultWrapper.Empty)
                } else {
                    consumer.consume(ResultWrapper.Success(foundTracks))
                }
            } catch (e: Exception) {
                consumer.consume(ResultWrapper.NetworkError)
            }
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
