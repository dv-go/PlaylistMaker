package com.practicum.playlistmaker.creator

import android.content.Context
import com.practicum.playlistmaker.sharing.data.SearchHistory
import com.practicum.playlistmaker.sharing.data.repository.ThemeRepositoryImpl
import com.practicum.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.settings.domain.interactors.SettingsInteractorImpl
import com.practicum.playlistmaker.search.domain.interactors.TracksInteractorImpl

object Creator {
    private lateinit var searchHistory: SearchHistory
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
        val sharedPreferences = context.getSharedPreferences("search_prefs", Context.MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)
    }

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient)
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(), searchHistory)
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        val themeRepository = ThemeRepositoryImpl(appContext)
        return SettingsInteractorImpl(appContext, themeRepository)
    }
}
