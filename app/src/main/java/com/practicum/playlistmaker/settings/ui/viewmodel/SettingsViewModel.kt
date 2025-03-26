package com.practicum.playlistmaker.settings.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.data.dto.SupportEmailData
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.ui.presentation.SettingsCommand

class SettingsViewModel(private val settingsInteractor: SettingsInteractor) : ViewModel() {

    private val _command = MutableLiveData<SettingsCommand>()
    val command: LiveData<SettingsCommand> = _command

    private val _isDarkThemeEnabled = MutableLiveData<Boolean>()
    val isDarkThemeEnabled: LiveData<Boolean> get() = _isDarkThemeEnabled

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        _isDarkThemeEnabled.value = settingsInteractor.isDarkThemeEnabled()
    }

    fun switchTheme(isEnabled: Boolean) {
        settingsInteractor.switchTheme(isEnabled)
        _isDarkThemeEnabled.value = isEnabled
    }

    fun onShareButtonClicked() {
        _command.value = SettingsCommand.Share(settingsInteractor.getShareLink())
    }

    fun onSupportButtonClicked() {
        _command.value = SettingsCommand.SendEmail(settingsInteractor.getSupportEmailData())
    }

    fun onAgreementButtonClicked() {
        _command.value = SettingsCommand.OpenUrl(settingsInteractor.getUserAgreementLink())
    }

}
