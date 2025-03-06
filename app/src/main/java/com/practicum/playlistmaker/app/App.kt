package com.practicum.playlistmaker.app

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.di.Creator

const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
const val THEME_KEY = "key_for_darkTheme"

class App : Application() {

    private lateinit var sharedPrefs: SharedPreferences
    var darkTheme = false
        private set

    override fun onCreate() {
        super.onCreate()
        sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)

        Creator.init(this)
        initTheme()
    }


    private fun initTheme() {
        darkTheme = sharedPrefs.getBoolean(THEME_KEY, isSystemDarkTheme())

        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    private fun isSystemDarkTheme(): Boolean {
        val currentNightMode = resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        sharedPrefs.edit().putBoolean(THEME_KEY, darkThemeEnabled).apply()

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun isDarkThemeEnabled(): Boolean {
        return darkTheme
    }
}
