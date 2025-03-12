package com.practicum.playlistmaker.sharing.data.repository

import android.content.Context
import com.practicum.playlistmaker.app.App
import com.practicum.playlistmaker.sharing.domain.api.ThemeRepository

class ThemeRepositoryImpl(private val context: Context) : ThemeRepository {

    private val app = context.applicationContext as App

    override fun isDarkThemeEnabled(): Boolean {
        return app.isDarkThemeEnabled()
    }

    override fun switchTheme(isEnabled: Boolean) {
        app.switchTheme(isEnabled)
    }
}
