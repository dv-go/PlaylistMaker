package com.practicum.playlistmaker.search.ui.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.models.PlaceholderState
import com.practicum.playlistmaker.search.domain.models.Track

class SearchViewModel(application: Application, private val tracksInteractor: TracksInteractor) :
    AndroidViewModel(application) {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _tracks = MutableLiveData<List<Track>>(emptyList())
    val tracks: LiveData<List<Track>> = _tracks

    private val _historyVisible = MutableLiveData<Boolean>()
    val historyVisible: LiveData<Boolean> = _historyVisible

    private val _hideKeyboardEvent = MutableLiveData<Unit>()
    val hideKeyboardEvent: LiveData<Unit> = _hideKeyboardEvent

    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    private val _showToastEvent = MutableLiveData<String?>()
    val showToastEvent: LiveData<String?> = _showToastEvent

    private val _navigateToMediaActivity = MutableLiveData<Track?>()
    val navigateToMediaActivity: LiveData<Track?> = _navigateToMediaActivity

    private val _trackListVisible = MutableLiveData<Boolean>()
    val trackListVisible: LiveData<Boolean> = _trackListVisible

    private val _placeholderState = MutableLiveData<PlaceholderState>()
    val placeholderState: LiveData<PlaceholderState> = _placeholderState

    private val _clearButtonState = MutableLiveData<Pair<Boolean, Int?>>()
    val clearButtonState: LiveData<Pair<Boolean, Int?>> = _clearButtonState

    private var isClickAllowed = true

    fun search(query: String) {
        if (query.isBlank()) return

        _hideKeyboardEvent.postValue(Unit)

        _isLoading.postValue(true)
        hidePlaceholder()

        tracksInteractor.searchTracks(query, object : TracksInteractor.TracksConsumer {
            override fun consume(result: ResultWrapper) {
                _isLoading.postValue(false)

                when (result) {
                    is ResultWrapper.Success -> {
                        _tracks.postValue(result.tracks)
                        updateHistoryVisibility(false)
                        _trackListVisible.postValue(true)
                    }
                    is ResultWrapper.Empty -> {
                        _tracks.postValue(emptyList())
                        showError(
                            getApplication<Application>().getString(R.string.nothing_found),
                            R.drawable.nothing_found,
                            false
                        )
                    }
                    is ResultWrapper.NetworkError -> {
                        _tracks.postValue(emptyList())
                        showError(
                            getApplication<Application>().getString(R.string.something_went_wrong),
                            R.drawable.no_connection,
                            true
                        )
                    }
                }
            }
        })
    }

    fun loadHistory() {
        val historyTracks = tracksInteractor.getSearchHistory()
        _tracks.value = historyTracks
        updateHistoryVisibility(historyTracks.isNotEmpty())
    }

    fun clearHistory() {
        tracksInteractor.clearHistory()
        loadHistory()
        updateHistoryVisibility(false)
        _showToastEvent.value = "История очищена"
    }

    fun updateHistoryVisibility(isVisible: Boolean) {
        _historyVisible.postValue(isVisible)
    }

    fun clearSearchResults() {
        _tracks.value = emptyList()
        hidePlaceholder()
    }

    fun searchDebounce(query: String) {
        searchRunnable?.let { searchHandler.removeCallbacks(it) }
        searchRunnable = Runnable { search(query) }
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
        updateClearButtonVisibility(query.isNotEmpty())

        if (query.isEmpty()) {
            loadHistory()
            updateHistoryVisibility(true)
            _trackListVisible.postValue(true)
            cancelSearchDebounce()
        } else {
            updateHistoryVisibility(false)
            _trackListVisible.postValue(false)
            searchDebounce(query)
        }
    }

    fun showError(message: String, imageRes: Int, showRefreshButton: Boolean) {
        _placeholderState.postValue(
            PlaceholderState(message, imageRes, true, showRefreshButton)
        )
    }

    fun hidePlaceholder() {
        _placeholderState.postValue(
            PlaceholderState(null, null, false, false)
        )
    }

    fun updateClearButtonVisibility(visible: Boolean) {
        val iconRes = if (visible) R.drawable.ic_clear_button else null
        _clearButtonState.value = Pair(visible, iconRes)
    }

}