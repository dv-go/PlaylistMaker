package com.practicum.playlistmaker.di

import android.content.Context
import com.practicum.playlistmaker.data.SearchHistory
import com.practicum.playlistmaker.data.repository.ThemeRepositoryImpl
import com.practicum.playlistmaker.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.domain.api.SettingsInteractor
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.interactor.SettingsInteractorImpl
import com.practicum.playlistmaker.domain.interactors.TracksInteractorImpl

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
