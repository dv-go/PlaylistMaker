package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.mediateka.ui.viewmodel.FavoritesViewModel
import com.practicum.playlistmaker.mediateka.ui.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val MediatekaUIModule = module {

    viewModel { FavoritesViewModel() }

    viewModel { PlaylistsViewModel() }
}