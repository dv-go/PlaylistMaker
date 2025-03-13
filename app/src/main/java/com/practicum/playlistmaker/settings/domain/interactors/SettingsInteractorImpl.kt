package com.practicum.playlistmaker.settings.domain.interactors

import com.practicum.playlistmaker.settings.data.dto.SupportEmailData
import com.practicum.playlistmaker.settings.domain.api.ExternalNavigator
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.api.ThemeRepository

class SettingsInteractorImpl(
    private val themeRepository: ThemeRepository,
    private val externalNavigator: ExternalNavigator
) : SettingsInteractor {

    override fun isDarkThemeEnabled(): Boolean {
        return themeRepository.isDarkThemeEnabled()
    }

    override fun switchTheme(isEnabled: Boolean) {
        themeRepository.switchTheme(isEnabled)
    }

    override fun getShareLink(): String {
        return externalNavigator.getShareLink()
    }

    override fun getSupportEmailData(): SupportEmailData {
        return externalNavigator.getSupportEmailData()
    }

    override fun getUserAgreementLink(): String {
        return externalNavigator.getUserAgreementLink()
    }
}
