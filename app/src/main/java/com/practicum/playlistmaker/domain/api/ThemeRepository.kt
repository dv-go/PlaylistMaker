package com.practicum.playlistmaker.domain.api

interface ThemeRepository {
    fun isDarkThemeEnabled(): Boolean
    fun switchTheme(isEnabled: Boolean)
}
