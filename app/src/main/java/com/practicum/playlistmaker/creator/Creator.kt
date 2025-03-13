package com.practicum.playlistmaker.creator

import android.content.Context
import android.media.MediaPlayer
import com.practicum.playlistmaker.settings.data.datasource.SearchHistoryDataSource
import com.practicum.playlistmaker.settings.data.datasource.ThemeDataSource
import com.practicum.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.settings.data.repository.ThemeRepositoryImpl
import com.practicum.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.settings.data.repository.ExternalNavigatorImpl
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.settings.domain.interactors.SettingsInteractorImpl
import com.practicum.playlistmaker.search.domain.interactors.TracksInteractorImpl
import com.practicum.playlistmaker.player.data.player.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.interactors.PlayerInteractorImpl
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.network.RetrofitClientProvider

object Creator {
    private const val SEARCH_PREFS = "search_prefs"
    private const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"

    fun provideTracksInteractor(context: Context): TracksInteractor {
        val retrofit = RetrofitClientProvider.provideRetrofit()
        val networkClient: NetworkClient = RetrofitNetworkClient(retrofit)
        val sharedPreferences = context.getSharedPreferences(SEARCH_PREFS, Context.MODE_PRIVATE)
        val searchHistoryDataSource = SearchHistoryDataSource(sharedPreferences)
        val searchHistory = SearchHistoryRepositoryImpl(searchHistoryDataSource)
        val tracksRepository = TracksRepositoryImpl(networkClient)
        return TracksInteractorImpl(tracksRepository, searchHistory)
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        val sharedPreferences = context.getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
        val themeDataSource = ThemeDataSource(sharedPreferences)
        val themeRepository = ThemeRepositoryImpl(themeDataSource)
        val externalNavigator = ExternalNavigatorImpl(context.applicationContext)
        return SettingsInteractorImpl(themeRepository, externalNavigator)
    }

    fun providePlayerInteractor(previewUrl: String): PlayerInteractorImpl {
        val mediaPlayer = MediaPlayer().apply {
            setDataSource(previewUrl)
            prepareAsync()
        }
        val mediaPlayerManager = PlayerRepositoryImpl(previewUrl, mediaPlayer)
        return PlayerInteractorImpl(mediaPlayerManager)
    }
}
