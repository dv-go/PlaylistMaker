package com.practicum.playlistmaker.settings.domain.api

import com.practicum.playlistmaker.settings.data.dto.SupportEmailData

interface SettingsInteractor {
    fun isDarkThemeEnabled(): Boolean
    fun switchTheme(isEnabled: Boolean)
    fun getShareLink(): String
    fun getSupportEmailData(): SupportEmailData
    fun getUserAgreementLink(): String
}
