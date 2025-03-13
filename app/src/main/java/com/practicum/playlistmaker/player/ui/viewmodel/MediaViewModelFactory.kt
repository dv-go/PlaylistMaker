package com.practicum.playlistmaker.player.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.search.domain.models.Track

class MediaViewModelFactory(
    private val track: Track,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MediaViewModel::class.java)) {
            val tracksInteractor = Creator.providePlayerInteractor(track.previewUrl)

            @Suppress("UNCHECKED_CAST")
            return MediaViewModel(tracksInteractor, track) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
