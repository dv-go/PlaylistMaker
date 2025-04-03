package com.practicum.playlistmaker.player.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.domain.interactors.PlayerInteractorImpl
import com.practicum.playlistmaker.player.ui.presentation.PlayerScreenState
import com.practicum.playlistmaker.search.domain.models.Track

class PlayerViewModel(
    private val playerInteractor: PlayerInteractorImpl,
    private val track: Track
) : ViewModel() {

    private val _screenState = MutableLiveData<PlayerScreenState>()
    val screenState: LiveData<PlayerScreenState> get() = _screenState

    init {
        loadTrackData()
        playerInteractor.setOnTimeUpdateListener { time ->
            updateContentState(currentTime = time)
        }

        playerInteractor.setOnPlaybackStateChangedListener { isPlaying ->
            val icon = if (isPlaying) R.drawable.ic_pause_button else R.drawable.ic_play_button
            updateContentState(playButtonIcon = icon)
        }
    }

    private fun loadTrackData() {
        _screenState.value = PlayerScreenState.Content(
            track = track,
            currentTime = "0:00",
            playButtonIcon = R.drawable.ic_play_button,
            releaseYear = extractYear(track.releaseDate),
            genre = track.primaryGenreName,
            country = track.country,
            artworkUrl = track.artworkUrl100.replace("100x100bb.jpg", "512x512bb.jpg")
        )
    }

    fun togglePlayback() {
        playerInteractor.togglePlayback()
        val icon = if (playerInteractor.isPlaying()) {
            R.drawable.ic_pause_button
        } else {
            R.drawable.ic_play_button
        }
        updateContentState(playButtonIcon = icon)
    }

    fun pausePlayback() {
        playerInteractor.pausePlayback()
        updateContentState(playButtonIcon = R.drawable.ic_play_button)
    }

    fun releasePlayer() {
        playerInteractor.release()
    }

    private fun updateContentState(
        currentTime: String? = null,
        playButtonIcon: Int? = null
    ) {
        val currentState = _screenState.value
        if (currentState is PlayerScreenState.Content) {
            _screenState.value = currentState.copy(
                currentTime = currentTime ?: currentState.currentTime,
                playButtonIcon = playButtonIcon ?: currentState.playButtonIcon
            )
        }
    }

    private fun extractYear(dateString: String): String {
        return if (dateString.length >= 4) dateString.substring(0, 4) else "Unknown"
    }
}
