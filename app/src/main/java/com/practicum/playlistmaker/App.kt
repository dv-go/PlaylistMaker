package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferencies"
const val THEME_KEY = "key_for_darkTheme"

class App: Application() {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        val sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)

        darkTheme = if (!sharedPrefs.contains(THEME_KEY)) {
            val currentNightMode = resources.configuration.uiMode and
                    android.content.res.Configuration.UI_MODE_NIGHT_MASK
            val isSystemDarkTheme = currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES

            sharedPrefs.edit().putBoolean(THEME_KEY, isSystemDarkTheme).apply()
            isSystemDarkTheme
        } else {
            sharedPrefs.getBoolean(THEME_KEY, false)
        }

        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun switchTheme(darkThemeEnabled: Boolean){
        darkTheme = darkThemeEnabled

        val sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putBoolean(THEME_KEY, darkThemeEnabled)
            apply()
        }

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}