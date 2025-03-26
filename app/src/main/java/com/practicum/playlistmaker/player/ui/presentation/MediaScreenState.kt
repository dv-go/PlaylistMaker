package com.practicum.playlistmaker.player.ui.presentation

import com.practicum.playlistmaker.search.domain.models.Track

sealed class MediaScreenState {
    object Loading : MediaScreenState()
    data class Error(val message: String) : MediaScreenState()
    data class Content(
        val track: Track,
        val currentTime: String,
        val playButtonIcon: Int,
        val releaseYear: String,
        val genre: String,
        val country: String,
        val artworkUrl: String
    ) : MediaScreenState()
}
