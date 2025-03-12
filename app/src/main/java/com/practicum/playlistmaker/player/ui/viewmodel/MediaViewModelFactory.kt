package com.practicum.playlistmaker.player.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.player.data.player.MediaPlayerManager
import com.practicum.playlistmaker.search.domain.models.Track

class MediaViewModelFactory(
    private val mediaPlayerManager: MediaPlayerManager,
    private val track: Track
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MediaViewModel::class.java)) {
            MediaViewModel(mediaPlayerManager, track) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}