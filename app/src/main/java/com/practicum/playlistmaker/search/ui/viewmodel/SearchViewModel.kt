package com.practicum.playlistmaker.search.ui.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.ui.presentation.SearchScreenState
import com.practicum.playlistmaker.search.domain.models.Track

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    private val _navigateToMediaActivity = MutableLiveData<Track?>()
    val navigateToMediaActivity: LiveData<Track?> = _navigateToMediaActivity

    private val _searchScreenState = MutableLiveData<SearchScreenState>()
    val searchScreenState: LiveData<SearchScreenState> = _searchScreenState

    private var isClickAllowed = true

    fun search(query: String) {
        if (query.isBlank()) return

        _searchScreenState.postValue(SearchScreenState.Loading)

        tracksInteractor.searchTracks(query, object : TracksInteractor.TracksConsumer {
            override fun consume(result: ResultWrapper) {
                when (result) {
                    is ResultWrapper.Success -> {
                        if (result.tracks.isNotEmpty()) {
                            _searchScreenState.postValue(SearchScreenState.Loaded(result.tracks))
                        } else {
                            _searchScreenState.postValue(SearchScreenState.Error(SearchScreenState.ErrorType.NoResults))
                        }
                    }
                    is ResultWrapper.Empty -> {
                        _searchScreenState.postValue(SearchScreenState.Error(SearchScreenState.ErrorType.NoResults))
                    }
                    is ResultWrapper.NetworkError -> {
                        _searchScreenState.postValue(SearchScreenState.Error(SearchScreenState.ErrorType.NoNetwork))
                    }
                }
            }
        })
    }

    fun loadHistory() {
        val historyTracks = tracksInteractor.getSearchHistory()

        if (historyTracks.isNotEmpty()) {
            _searchScreenState.value = SearchScreenState.History(historyTracks)
        } else {
            _searchScreenState.value = SearchScreenState.EmptyHistory
        }
    }

    fun clearHistory() {
        tracksInteractor.clearHistory()
        loadHistory()
        _searchScreenState.value = SearchScreenState.ShowToast("История очищена")
    }

    private fun searchDebounce(query: String) {
        cancelSearchDebounce()

        searchRunnable = Runnable {
                _searchScreenState.postValue(SearchScreenState.Loading)
                search(query)
        }

        searchHandler.postDelayed(searchRunnable!!, SEARCH_DEBOUNCE_DELAY)
    }

    fun performClickWithDebounce(action: () -> Unit) {
        if (isClickAllowed) {
            isClickAllowed = false
            action()
            Handler(Looper.getMainLooper()).postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
    }

    fun onTrackClicked(track: Track) {
        performClickWithDebounce {
            tracksInteractor.saveToHistory(track)
            _navigateToMediaActivity.value = track
        }
    }

    fun cancelSearchDebounce() {
        searchRunnable?.let { searchHandler.removeCallbacks(it) }
    }

    fun onSearchTextChanged(query: String) {
            _searchScreenState.postValue(SearchScreenState.Typing)
            searchDebounce(query)
    }
}