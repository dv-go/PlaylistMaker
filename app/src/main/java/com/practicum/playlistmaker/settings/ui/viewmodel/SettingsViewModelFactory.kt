package com.practicum.playlistmaker.settings.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor

class SettingsViewModelFactory(
    private val settingsInteractor: SettingsInteractor
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(settingsInteractor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
