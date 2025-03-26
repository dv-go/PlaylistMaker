package com.practicum.playlistmaker.settings.data.datasource

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration

class ThemeDataSource(
    private val sharedPreferences: SharedPreferences,
    private val context: Context
) {

    companion object {
        private const val THEME_KEY = "key_for_darkTheme"
    }

    fun isDarkThemeEnabled(): Boolean {
        return sharedPreferences.getBoolean(THEME_KEY, isSystemDarkTheme())
    }

    fun saveThemeState(isEnabled: Boolean) {
        sharedPreferences.edit().putBoolean(THEME_KEY, isEnabled).apply()
    }

    fun isSystemDarkTheme(): Boolean {
        return context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
}
