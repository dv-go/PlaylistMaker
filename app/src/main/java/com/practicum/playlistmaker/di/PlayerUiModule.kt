package com.practicum.playlistmaker.di

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.data.player.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.interactors.PlayerInteractorImpl
import com.practicum.playlistmaker.player.ui.viewmodel.MediaViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val PlayerUiModule = module {

    viewModel { (track: Track) ->
        val mediaPlayer = MediaPlayer()
        val repository = PlayerRepositoryImpl(track.previewUrl, mediaPlayer)
        val interactor = PlayerInteractorImpl(repository)
        MediaViewModel(interactor, track)
    }
}
