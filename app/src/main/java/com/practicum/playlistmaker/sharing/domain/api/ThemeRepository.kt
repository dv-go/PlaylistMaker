package com.practicum.playlistmaker.sharing.domain.api

interface ThemeRepository {
    fun isDarkThemeEnabled(): Boolean
    fun switchTheme(isEnabled: Boolean)
}
