package com.practicum.playlistmaker.player.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.data.player.MediaPlayerManager
import com.practicum.playlistmaker.search.domain.models.Track

class MediaViewModel(
    private val mediaPlayerManager: MediaPlayerManager,
    private val track: Track
) : ViewModel() {

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> get() = _isPlaying

    private val _currentTime = MutableLiveData<String>()
    val currentTime: LiveData<String> get() = _currentTime

    private val _trackData = MutableLiveData<Map<String, String>>()
    val trackData: LiveData<Map<String, String>> get() = _trackData

    private val _defaultTime = MutableLiveData<String>()
    val defaultTime: LiveData<String> = _defaultTime

    private val _artworkUrl = MutableLiveData<String>()
    val artworkUrl: LiveData<String> = _artworkUrl

    private val _playButtonIcon = MutableLiveData<Int>()
    val playButtonIcon: LiveData<Int> = _playButtonIcon

    init {
        mediaPlayerManager.setOnTimeUpdateListener { time ->
            _currentTime.postValue(time)
        }

        mediaPlayerManager.setOnPlaybackStateChangedListener { isPlaying ->
            _isPlaying.postValue(isPlaying)
        }

        loadTrackData()
        _defaultTime.value = "0:00"
        _artworkUrl.value = track.artworkUrl100.replace("100x100bb.jpg", "512x512bb.jpg")
        _playButtonIcon.value = R.drawable.ic_play_button
    }

    fun togglePlayback() {
        mediaPlayerManager.togglePlayback()
        _playButtonIcon.value = if (mediaPlayerManager.isPlaying()) {
            R.drawable.ic_pause_button
        } else {
            R.drawable.ic_play_button
        }
    }

    fun releasePlayer() {
        mediaPlayerManager.release()
    }

    fun pausePlayback() {
        mediaPlayerManager.pausePlayback()
    }

    private fun loadTrackData() {
        _trackData.postValue(
            mapOf(
                "trackName" to track.trackName,
                "artistName" to track.artistName,
                "trackTime" to track.trackTimeMillis,
                "releaseYear" to extractYear(track.releaseDate),
                "genre" to track.primaryGenreName,
                "country" to track.country,
                "artworkUrl" to track.artworkUrl100.replace("100x100bb.jpg", "512x512bb.jpg")
            )
        )
    }

    private fun extractYear(dateString: String): String {
        return if (dateString.length >= 4) dateString.substring(0, 4) else "Unknown"
    }
}
