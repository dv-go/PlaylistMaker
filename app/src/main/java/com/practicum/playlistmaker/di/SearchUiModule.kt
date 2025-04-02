package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.search.ui.viewmodel.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val SearchUiModule = module {
    viewModel {
        SearchViewModel(get())
    }
}
