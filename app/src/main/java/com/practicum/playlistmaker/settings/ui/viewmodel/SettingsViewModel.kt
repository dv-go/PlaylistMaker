package com.practicum.playlistmaker.settings.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.data.dto.SupportEmailData
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor

class SettingsViewModel(private val settingsInteractor: SettingsInteractor) : ViewModel() {

    private val _isDarkThemeEnabled = MutableLiveData<Boolean>()
    val isDarkThemeEnabled: LiveData<Boolean> get() = _isDarkThemeEnabled

    private val _shareLink = MutableLiveData<String>()
    val shareLink: LiveData<String> get() = _shareLink

    private val _supportEmailData = MutableLiveData<SupportEmailData>()
    val supportEmailData: LiveData<SupportEmailData> get() = _supportEmailData

    private val _userAgreementLink = MutableLiveData<String>()
    val userAgreementLink: LiveData<String> get() = _userAgreementLink

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
        _shareLink.value = settingsInteractor.getShareLink()
    }

    fun onSupportButtonClicked() {
        _supportEmailData.value = settingsInteractor.getSupportEmailData()
    }

    fun onAgreementButtonClicked() {
        _userAgreementLink.value = settingsInteractor.getUserAgreementLink()
    }
}
