package com.practicum.playlistmaker.settings.domain.api

interface ThemeRepository {
    fun isDarkThemeEnabled(): Boolean
    fun switchTheme(isEnabled: Boolean)
}
