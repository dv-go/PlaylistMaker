package com.practicum.playlistmaker.settings.data.datasource

import android.content.SharedPreferences

class ThemeDataSource(private val sharedPreferences: SharedPreferences) {

    fun isDarkThemeEnabled(key: String, default: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, default)
    }

    fun saveThemeState(key: String, isEnabled: Boolean) {
        sharedPreferences.edit().putBoolean(key, isEnabled).apply()
    }
}
