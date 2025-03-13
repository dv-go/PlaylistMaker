package com.practicum.playlistmaker.settings.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.data.dto.SupportEmailData
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor

class SettingsViewModel(private val settingsInteractor: SettingsInteractor) : ViewModel() {

    private val _isDarkThemeEnabled = MutableLiveData<Boolean>()
    val isDarkThemeEnabled: LiveData<Boolean> get() = _isDarkThemeEnabled

    init {
        _isDarkThemeEnabled.value = settingsInteractor.isDarkThemeEnabled()
    }

    fun switchTheme(isEnabled: Boolean) {
        settingsInteractor.switchTheme(isEnabled)
        _isDarkThemeEnabled.value = isEnabled
    }

    fun getShareLink(): String {
        return settingsInteractor.getShareLink()
    }

    fun getSupportEmailData(): SupportEmailData {
        return settingsInteractor.getSupportEmailData()
    }

    fun getUserAgreementLink(): String {
        return settingsInteractor.getUserAgreementLink()
    }
}
