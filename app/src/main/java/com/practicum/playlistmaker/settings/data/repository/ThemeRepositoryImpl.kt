package com.practicum.playlistmaker.settings.data.repository

import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.settings.data.datasource.ThemeDataSource
import com.practicum.playlistmaker.settings.domain.api.ThemeRepository

class ThemeRepositoryImpl(
    private val themeDataSource: ThemeDataSource
) : ThemeRepository {

    companion object {
        private const val THEME_KEY = "key_for_darkTheme"
    }

    override fun isDarkThemeEnabled(): Boolean {
        return themeDataSource.isDarkThemeEnabled()    }

    override fun switchTheme(isEnabled: Boolean) {
        themeDataSource.saveThemeState(isEnabled)
        AppCompatDelegate.setDefaultNightMode(
            if (isEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}