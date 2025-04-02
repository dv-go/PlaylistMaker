package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val SettingsUiModule = module {

    viewModel {
        SettingsViewModel(get())
    }
}
