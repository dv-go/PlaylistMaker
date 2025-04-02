package com.practicum.playlistmaker.di

import android.content.Context
import com.practicum.playlistmaker.settings.data.datasource.ThemeDataSource
import com.practicum.playlistmaker.settings.data.repository.ExternalNavigatorImpl
import com.practicum.playlistmaker.settings.data.repository.ThemeRepositoryImpl
import com.practicum.playlistmaker.settings.domain.api.ExternalNavigator
import com.practicum.playlistmaker.settings.domain.api.ThemeRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val SettingsDataModule = module {

    single {
        androidContext().getSharedPreferences(
            "playlist_maker_preferences",
            Context.MODE_PRIVATE
        )
    }

    single {
        ThemeDataSource(
            sharedPreferences = get(),
            context = androidContext()
        )
    }

    single<ThemeRepository> {
        ThemeRepositoryImpl(get())
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }
}
