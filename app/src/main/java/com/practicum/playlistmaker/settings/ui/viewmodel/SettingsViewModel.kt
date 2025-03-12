package com.practicum.playlistmaker.settings.ui.viewmodel

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    fun getShareIntent(): Intent = settingsInteractor.getShareIntent()

    fun getSupportEmailIntent(): Intent = settingsInteractor.getSupportEmailIntent()

    fun getUserAgreementIntent(): Intent = settingsInteractor.getUserAgreementIntent()
}
