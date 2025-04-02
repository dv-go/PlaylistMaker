package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.interactors.SettingsInteractorImpl
import org.koin.dsl.module

val SettingsDomainModule = module {
    single<SettingsInteractor> {
        SettingsInteractorImpl(
            themeRepository = get(),
            externalNavigator = get()
        )
    }
}
