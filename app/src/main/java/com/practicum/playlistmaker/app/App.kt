package com.practicum.playlistmaker.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.di.*
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.get

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                SearchDataModule,
                SearchDomainModule,
                SearchUiModule,
                PlayerDataModule,
                PlayerDomainModule,
                PlayerUiModule,
                SettingsDataModule,
                SettingsDomainModule,
                SettingsUiModule,
                mediatekaUIModule
            )
        }

        val settingsInteractor: SettingsInteractor = get(SettingsInteractor::class.java)
        applyTheme(settingsInteractor.isDarkThemeEnabled())
    }

    private fun applyTheme(isDarkTheme: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
